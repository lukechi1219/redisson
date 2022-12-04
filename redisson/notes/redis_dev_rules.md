# Redis開發規範

一般規範

- 情境: use redis as cache
  - 目的: 緩存處理，加快讀取效率 -> avoid read db
  - 使用過程中需要注意合理的使用
    - 一般存儲 全局配置 data  (global, 跨 server)
    - 和一些訪問 非常頻繁 的 較為靜態 的資料 
  - 注意過期時間控制，減少資源的不必要消耗
  - .

.

- key name
  - 模塊級固定段：服務簡碼:模塊簡碼: 例：hpfm:fnd:
  - 服務級固定段：服務簡碼: 例：hpfm:

.

- 先操作緩存，還是先操作資料庫
  - 希望保證操作緩存和操作資料庫的原子性，要么同時成功，要么同時失敗。
  - 這演變為一個分佈式事務的問題，保證原子性十分困難，很有可能出現一半成功，一半失敗。
- 5.總結
  - (1) 讀請求：先讀cache，再讀db；如果cache hit，則直接返回資料 ；如果cache miss，則訪問db，並將資料 set回緩存。
  - (2) 更新請求：先刪緩存，再操作資料庫。
  - (3) 如果先操作資料庫，再刪緩存，因為可能存在刪除緩存失敗的問題，可以提供一個補償措施，例如利用消息隊列。
  - (4) 對於對像類型，或者文本類型，修改緩存value的成本較高，一般選擇直接淘汰緩存(delete key)
  - (5) 建議淘汰(delete)緩存，而不是更新(set)緩存。
  - (6) 一般來說， 資料 最終以資料庫為準，寫緩存成功，其實並不算成功。
  - (7) 如果對 資料 有強一致性要求，就不能放緩存。我們所做的一切，只能保證最終一致性。
  - (8) 緩存盡量加上失效時間，可以避免永久性的 髒 資料 。
  - (9) 緩存失效時間不要一樣，可以加上一個隨機值，避免集體失效。否則容易導致緩存雪崩，即緩存同一時間大面積的失效，這個時候又來了一波請求，結果請求都懟到資料庫上，從而導致資料庫連接異常。
- .


---

- Redis開發規範

https://open.hand-china.com/hzero-docs/v1.3/zh/docs/development-specification/backent-development-specification/redis/

總體規約以《阿里雲Redis開發規範》為主，請開發人員至少閱讀一遍該手冊。

---

- 簡介： 本文介紹了在使用阿里雲Redis的開發規範，
  - 從鍵值設計、命令使用、客戶端使用、相關工具等方面進行說明，
- 通過本文的介紹可以減少使用Redis過程帶來的問題。

---

- 阿里雲Redis開發規範

https://developer.aliyun.com/article/531067



一、鍵值設計

1. key名設計
   (1)【建議】: 可讀性和可管理性
   以業務名(或資料庫名)為前綴(防止key衝突)，用冒號分隔，比如業務名:表名:id

ugc:video:1

(2)【建議】：簡潔性

保證語義的前提下，控制key的長度，當key較多時，內存佔用也不容忽視，例如：

user:{uid}:friends:messages:{mid} 简化为 u:{uid}:fr:m:{mid}。

(3)【強制】：不要包含特殊字符

反例：包含空格、換行、單雙引號以及其他轉義字符 ( Escape Character )

詳細解析 https://ost.51cto.com/posts/11393

Redis開發規範解析(一)--鍵名設計

去年我寫過一個《阿里雲Redis開發規範》，在網上轉載很多，但其實說心裡話，我並不認為寫的多好，受制一些客觀因素和篇幅，有些不夠細緻和深入，所以想在公眾號裡詳細解析下，希望對大家有幫助。

本篇是第一篇：由鍵名設計想到的SDS內存優化

解析

上面這些內容本來沒什麼好說的，但是這個就和做菜“放鹽少許”一樣，key多長才算最佳呢？

從之前遇到的一個問題討論下。

下面來看看為什麼選的是44字節：

1.內部編碼

Redis中的字符串類型，有三種內部編碼：raw、embstr、int。當值小於44字節（Redis 3.2+），使用embstr，否則使用raw（這裡不討論int）

現在回過頭來屢一下兩個問題：

1.字符串多短為好：其實就是要盡量使用embstr。

四、結論

- 1.SDS在Redis 3.2+有可能節省更多的空間，但3.2更像一個過渡版本，Redis 4更加適合（異步刪除、psync2、碎片率整理），我已經在線上大量使用，“趕緊”去用吧。
- 2.embstr從Redis3 39字節->Redis3.2+ 44字節
- 3.做個環保的程序員，小優化大效果。


2. value設計

3. (1)【強制】：拒絕bigkey(防止網卡流量、慢查詢)

string 類型控制在10KB以內，hash、list、set、zset元素個數不要超過5000。

反例：一個包含200萬個元素的list。

非字符串的 bigkey，不要使用del刪除，使用 hscan、sscan、zscan 方式漸進式刪除，同時要注意防止 bigkey 過期時間 自動刪除 問題 (
例如一個200萬的 zset 設置1小時過期，會觸發 del 操作，造成阻塞，而且該操作不會不出現在慢查詢中(latency可查))，查找方法和刪除方法

詳細解析 https://www.modb.pro/db/52894

Redis開發規範解析(二)--老生常談bigkey

二、危害

bigkey可以說就是Redis的老鼠屎，具體表現在：

1.內存空間不均勻：這樣會不利於集群對內存的統一管理，存在丟失 資料 的隱患 ???，下圖中的三個節點是同屬於一個集群，鍵值個數也接近，但內存容量相差較多。

2.超時阻塞：由於Redis單線程的特性，操作bigkey的通常比較耗時，也就意味著阻塞Redis可能性越大，這樣會造成客戶端阻塞或者引起故障切換，它們通常出現在慢查詢中。

3.網絡擁塞：

bigkey也就意味著每次獲取要產生的網絡流量較大，假設一個bigkey為 1MB，客戶端每秒訪問量為
1,000，那麼每秒產生1000MB的流量，對於普通的千兆網卡(
按照字節算是128MB/s)的服務器來說簡直是滅頂之災，而且一般服務器會採用單機多實例的方式來部署，也就是說一個bigkey可能會對其他實例造成影響，其後果不堪設想。

三、怎麼產生的？
一般來說，bigkey的產生都是由於程序設計不當，或者對於 資料 規模預料不清楚造成的，來看幾個🌰：

(1) 社交類：粉絲列表，如果某些明星或者大v不精心設計下，必是bigkey。

(2) 統計類：例如按天存儲某項功能或者網站的用戶集合，除非沒幾個人用，否則必是bigkey。

(3) 緩存類：將 資料 從資料庫load出來序列化放到Redis裡，這個方式非常常用，但有兩個地方需要注意，第一，是不是有必要把所有字段都緩存，第二，有沒有相關關聯的 資料。

例如我之前遇到過一個例子，該同學將某明星一個專輯下所有視頻信息都緩存一個巨大的json中，造成這個json達到6MB，後來這個明星發了一個官宣。。。這個我就不多說了，領盒飯去吧。

六、如何優化

1. 拆
   大名單：list1、list2、...listN

big hash：可以做二次的hash，例如hash%100

日期類：key 20190320、key 20190321、key_20190322。

(2)【推薦】：選擇適合的資料類型。

例如：實體類型(要合理控制和使用 資料 結構 memory 編碼優化配置,例如ziplist，但也要注意節省內存和性能之間的平衡)

反例：

- set user:1:name tom
- set user:1:age 19
- set user:1:favor football

積極的：

- hmset user:1 name tom age 19 favor football

3.【推薦】：控制key的生命週期，redis不是垃圾桶。

建議使用expire設置過期時間(條件允許可以打散過期時間，防止集中過期)，不過期的 資料 重點關注 idletime。

Redis开发规范解析(三)--一个Redis最好存多少key

https://ost.51cto.com/posts/11387

总结：

本文首先通过Redis到底能存储多少个键值对，引出Redis的Hash表实现方式（数组链表）、扩缩容等原理，最后通过一个开脑洞的思考探讨，分析了各种利弊，最终讨论Redis到底存储多少个键值对会比较好（最多千万级别）。

二、命令使用

1.【推薦】 O(N)命令關注N的數量

- 例如hgetall、lrange、smembers、zrange、sinter等並非不能使用，但是需要明確N的值。有遍歷的需求可以使用hscan、sscan、zscan代替。

2.【推薦】：禁用命令

- 禁止線上使用keys、flushall、flushdb等，通過redis的rename機制禁掉命令，或者使用scan的方式漸進式處理。

3.【推薦】合理使用select
redis的多資料庫較弱，使用數字進行區分，很多客戶端支持較差，同時多業務用多資料庫實際還是單線程處理，會有乾擾。

4.【推薦】使用批量操作提高效率

- 原生命令：例如mget、mset。
- 非原生命令：可以使用pipeline提高效率。
- 但要注意控制一次批量操作的元素個數(例如500以內，實際也和元素字節數有關)。

注意兩者不同：

-
   1. 原生是原子操作，pipeline是非原子操作。
-
   2. pipeline可以打包不同的命令，原生做不到
-
   3. pipeline需要客户端和服务端同时支持。

5.【建議】Redis事務功能較弱，不建議過多使用

- Redis的事務功能較弱(不支持回滾)，而且集群版本(自研和官方)要求一次事務操作的key必須在一個slot上(可以使用hashtag功能解決)

6.【建議】Redis集群版本在使用Lua上有特殊要求：

- 1.所有key都應該由KEYS 數組來傳遞，redis.call/pcall 裡面調用的redis命令，key的位置，必須是KEYS array, 否則直接返回error，"
  -ERR bad lua script for redis cluster, all the keys that the script uses should be passed using the KEYS array"
- 2.所有key，必須在1個slot上，否則直接返回error, "-ERR eval/evalsha command keys must in same slot"

7.【建議】必要情況下使用monitor命令時，要注意不要長時間使用。

三、客戶端使用

1.【推薦】

- 避免多個應用使用一個Redis實例

正例：不相干的業務拆分，公共 資料 做服務化。

解釋： player server, bo, api, affiliate 各自 可以有自己的 redis instance

3.【建議】

高並發下建議客戶端添加熔斷功能(例如netflix hystrix)

?????

5.【建議】

根據自身業務類型，選好maxmemory-policy(最大內存淘汰策略)，設置好過期時間。

默認策略是volatile-lru，即超過最大內存後，在過期鍵中使用lru算法進行key的剔除，保證不過期 資料 不被刪除，但是可能會出現OOM問題。

其他策略如下：

- allkeys-lru：根據LRU算法刪除鍵，不管 資料 有沒有設置超時屬性，直到騰出足夠空間為止。
- allkeys-random：隨機刪除所有鍵，直到騰出足夠空間為止。
- volatile-random:隨機刪除過期鍵，直到騰出足夠空間為止。
- volatile-ttl：根據鍵值對象的ttl屬性，刪除最近將要過期 資料 。如果沒有，回退到noeviction策略。
- noeviction：不會剔除任何 資料 ，拒絕所有寫入操作並返回客戶端錯誤信息"(error) OOM command not allowed when used memory"
  ，此時Redis只響應讀操作。

2.【推薦】：big key搜索

redis大key搜索工具

https://yq.aliyun.com/articles/117042?spm=a2c6h.12873639.article-detail.13.753b1feeHmLqfB

3.【推薦】：熱點key尋找(內部實現使用monitor，所以建議短時間使用)

facebook的redis-faina

https://github.com/facebookarchive/redis-faina?spm=a2c6h.12873639.article-detail.14.753b1feeHmLqfB




















