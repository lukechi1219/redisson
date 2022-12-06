# Redis 資料類型

https://try.redis.io/

docker run Redis image

https://medium.com/analytics-vidhya/the-most-important-redis-data-structures-you-must-understand-2e95b5cf2bce

https://redis.io/docs/data-types/

https://redis.io/docs/data-types/tutorial/

- 在學習 redis 的時候, 大家可以反問自己的問題
	- 跟 Oracle 資料庫 有什麼不一樣？
	- 跟 Java 用 Map 實作一個 cache 有什麼不一樣？
- .
- Think about how you will query your data before storing it
	- 例如，如果您知道您將按日期範圍查詢資料，請考慮使用包含範圍開始日期和結束日期的鍵來儲存資料。
- .

---

Redis 資料類型 :

- Redis 是一個 資料結構 server
- Redis 的核心, 是提供一群 原生資料類型，可幫助解決從 緩存 (快取, cache) 到 訊息佇 (ㄓㄨˋ) 佇列 (queuing) 再到 事件處理 (
	event processing) 的各種問題。
	- queuing
	- <img src="https://miro.medium.com/max/640/1*aJWxvFXEYKa4DNQcJQGLRg.png" width="500" alt="">
	- event processing
	- <img src="https://hazelcast.com/wp-content/uploads/2021/12/diagram-EventandStreamProc.png" width="500" alt="">
- .
- 關於 Redis 能處理 Queuing 跟 Event Processing，這一點對於我們的架構轉型很重要
	- 業務流程 以及 任務 的處理，如果要轉型成微服務，達成橫向擴展的話
	- 就需要將流程跟任務，調整成 事件驅動的架構
	- 而 Redis 可以提供我們這樣的資料結構跟功能
	- .
	- 關於這個議題，有興趣的人可以參考: [ 落實任務狀態事件化: 無痛導入微服務架構的起手式 ]
	- https://medium.brobridge.com/%E8%90%BD%E5%AF%A6%E4%BB%BB%E5%8B%99%E7%8B%80%E6%85%8B%E4%BA%8B%E4%BB%B6%E5%8C%96-813a9366d42e
- .
- Redis 原生資料類型
	- basic
		- Strings
		- Hashes
		- Lists
		- Sets
		- Sorted Sets
	- advanced
		- Geospatial indexes
		- Bitmaps
		- Bitfields
		- HyperLogLog
		- Streams
			<br><img src="https://assets-global.website-files.com/622642781cd7e96ac1f66807/623136c006b2386cd97d7cbe_image-101.png" style="width: 650px;" alt="">
			.
- 不是 data type 的功能
	- Transactions 交易控制
	- Pub/Sub
	- <img src="https://assets-global.website-files.com/622642781cd7e96ac1f66807/623136bf4a52fcd6339f0d2b_image3-1.png" style="width: 600px;" alt="">
	- .
- Extensions 擴充模組
	- Lua scripting --> 可以比喻成 Oracle 的 stored procedure
	- modules API
	- Redis Stack
		- RedisJSON
			- 支援 JSONPath 直接操作 JSON documents 資料
		- <img src="https://i.ytimg.com/vi/NLRbq2FtcIk/maxresdefault.jpg" style="width: 650px;" alt="">
	- .
		- RediSearch
			- Queries, 二級索引 secondary indexing, and full-text search for Redis
				- BSUVMFTF
				- (ZEHJPECZ)|(BSUV*)
			- index and query JSON documents
			- .
		- RedisTimeSeries
			- 時間序列資料 -> 股價
			- https://www.youtube.com/watch?v=NpK0p7ad55k
			- .
		- RedisGraph
			- Resource management (supply chain)
			- 誰是我朋友 A 的朋友，誰也是 B 的朋友？
			- 其他用戶與該用戶剛剛添加到購物車的產品一起購買了哪些產品？
			- 欺詐識別
			- 機率資料結構 Probabilistic data structures
			- 檢查用戶名是否被佔用（SaaS、內容發佈平台）
	- .
- Redis 的效能，取決於不同指令的複雜度
	- O(1)
	- O(n)
	- O(log(N))
	- .
- .

.

- Core
- .
	- strings
	- Redis strings 是最基本的 Redis 資料類型，代表一串 sequence of bytes (010101010101....)。
	- 由於 Redis key 是 strings，當我們也使用 strings 類型作為值時，我們是將一個 strings 映射到另一個 strings。
	- strings 資料類型可用於許多 use case，例如 緩存 HTML 片段 or 頁面 or jpeg 圖像 (而不用直接讀取硬碟)。
		- .
		- Redis strings 儲存 sequences of bytes, 包括 text, serialized objects, and binary arrays.
		- 它們通常用於 緩存 caching, 但它們支持額外的功能，使您也可以實現 計數器 和 執行 bitwise 運算.
		- .
		- 實務上，Redis strings 最好維持在 10KB 以下 -> alibaba redis 開發規範
		- .
		- 如果要將 結構化資料 存儲為 序列化字串，您可能可以需要考慮 Redis hashes 或 RedisJSON
		- .

| command                                                              |     |     |
|----------------------------------------------------------------------|-----|-----|
| OBJECT ENCODING mykey                                                |     |     |
| set mykey 1234567890123456789                                        |     |     |
| set mykey 12345678901234567890                                       |     |     |
| set mykey 9223372036854775807 (2 power 63 -1)                        |     |     |
| INCRBY mykey 1                                                       |     |     |
| SET user:1 salvatore                                                 |     |     |
| SET ticket:27 "{\"username\": \"priya\", \"ticket_id\": 321}" EX 100 |     |     |
| INCR views:page:2                                                    |     |     |
| INCRBY views:page:2 10                                               |     |     |
| set mykey newval                                                     |     |     |
| getset mykey newval2                                                 |     |     |
| get mykey                                                            |     |     |
| set mykey 100                                                        |     |     |
| type mykey                                                           |     |     |
| set 2022:金曲獎:最佳男歌手入圍名單 "Tony Kuo" ex 60                              |     |     |
| set 2022:金曲獎:最佳男歌手入圍名單 "Tony Kuo"                                    |     |     |
| .                                                                    |     |     |
| set inventory:五月天演唱會:20230501 5000                                   |     |     |
| decrby inventory:五月天演唱會:20230501 1                                   |     |     |
| get inventory:五月天演唱會:20230501                                        |     |     |
| type inventory:五月天演唱會:20230501                                       |     |     |
| object encoding inventory:五月天演唱會:20230501                            |     |     |
| .                                                                    |     |     |
| set mykey newval nx                                                  |     |     |
| set mykey newval xx                                                  |     |     |
|                                                                      |     |     |

- .
	- .
		- .
		- 增加一個計數器
			- INCR views:page:2
			- INCRBY views:page:2 10
			- .
			- INCRBY user:101:credit-balance -50
			- .
		- 限制
			- 默認情況下，單個 Redis string 最大為 512 MB
			- 實務上，Redis strings 最好維持在 10KB 以下
				- 最省空間的長度是 44 bytes 以內
			- .
		- Getting, Setting Strings
			- GET / SET
			- GETSET
			- MGET / MSET
				- 實用的技巧 -> 減少網路延遲
			- .
		- 必不可少的 TYPE 命令
			- set mykey 100
			- type mykey
			- incrby mykey 1
			- type mykey
			- del mykey
			- type mykey
			- get mykey
			- .
		- Managing counters
			- 原子增量 atomic increment
			- INCRBY "atomically" increments (and decrements when passing a negative number) counters stored at a given
				key.
				- INCRBY {key} 10
				- INCRBY {key} -5
				- INCRBYFLOAT {key} 0.1
		- .
		- 如果要避免 負數 或是 有其他商品 sku 要一起扣除，為了保持 原子操作，只能使用 Lua script.
		- .
			- .
		- Bitwise 運算
			- see the bitmaps data type docs.
			- .
		- Performance
			- 大多數 strings 操作的 複雜度為 O(1)，這意味著它們非常 高效。
			- 但是，請注意 SUBSTR, GETRANGE, and SETRANGE 命令，它們的複雜度可能為 O(n)。
			- 這些隨機訪問 strings 命令在處理大 strings 時可能會導致性能問題。
			- .
		- Alternatives
			- 如果您將結構化 資料 存儲為序列化 strings，您可能也想要考慮 Redis hashes 或 RedisJSON。
			- .
		- Learn more
			- Redis 大學的 RU101 詳細介紹了 Redis strings。
			- .
		- .
	- .
- .
- Keys
	- Redis keys 是 binary safe 的，這意味著您可以使用任何二進制序列作為 key，從 “foo” 這樣的 strings 到 JPEG
		文件的內容。空字串 "" 也是一個有效的 key。
		- any binary sequence can be used as a key,
		- anything from a simple string like Foo,
			numbers like 42, or 3.1415, 0xff, or a binary value.
		- .
	- 很長的 key 不是一個好主意。例如，1024 bytes 的 key 不僅在內存方面是個壞主意，而且因為在 資料集 中查找 key 可能需要多次代價高昂的
		key 比較。
	- 非常短的 key 通常不是一個好主意。如果您可以寫成 “user:1000:followers”，那麼將 “u1000flw” 寫成 key 就沒什麼意義了。前者更具可讀性。
	- 訂出屬於我們自己的規範:
		- “object-type:id”
		- user:101
		- comment:4321:reply.to 或 comment:4321:reply-to 是兩種不同的命名規範
		- .
		- user:101:timezone UTC+8
		- user:101:visit-count
		- user:101:credit-balance
		- .
		- cache api response: usage:101 -> JSON
		- .
		- Dev Rule: service A 不應該直接存取 service B 的 redid 資料
		- .
		- 大小寫不同
			- registeredusers:1000:followers
			- RegisteredUsers:1000:followers
			- registeredUsers:1000:followers
			- .
		- set 2022:金曲獎:最佳男歌手入圍名單 "Tony Kuo" ex 60
		- set 2022:金曲獎:最佳男歌手入圍名單 "Tony Kuo"
		- .
	- .
- .
- Key expiration
	- 在繼續之前，我們應該了解一個重要的 Redis 功能，無論您存儲的值是什麼類型，它都可以 work：Key expiration。Key expiration
		允許您為 key 設置 timeout，也稱為 “生存時間 time to live ” 或 “TTL”。
	- 當過期時間過去時，key 將自動銷毀。
		- 但是，過期時間 分辨率 始終為 1 毫秒。
		- 關於過期的信息會被複製並保存在磁盤上，當您的 Redis 服務器保持停止時，時間實際上已經過去了（這意味著 Redis
			會保存 key 過期的日期）。
		- .
	- set mykey 100 ex 100
	- ttl key
	- How can the expiration of keys be set?
		- In seconds
		- In milliseconds
		- In Unix epoch time (timestamp)
	- .
- .
- Key delete
	- 如果有需要 手動 刪除 key
		- 實務上，不要用 Del 命令，也不要把 Expire 命令設置為 0
		- 而是使用 Unlink 命令
			- However the command performs the actual memory reclaiming in a different thread, so it is not blocking,
			- while DEL is blocking.
	- .
- .

.

- 跟 redis Strings 有關的 Redisson classes
	- 
	-
	- .
- .

.

- .
	- hashes
	- Redis hashes 是建模為 Strings pair 集合的記錄類型。因此，Redis 哈希類似於Python 字典、Java HashMaps 和 Ruby 哈希。
		- 如果要存儲 json data，您可能需要考慮 RedisJSON
		- .
- .

| command                                                                                                                                     |     |     |
|---------------------------------------------------------------------------------------------------------------------------------------------|-----|-----|
| OBJECT ENCODING user:123                                                                                                                    |     |     |
| HSET user:123 username martina firstName Martina lastName Elisa country GB                                                                  |     |     |
| HINCRBY device:777:stats pings 1                                                                                                            |     |     |
| HINCRBY device:777:stats errors 1                                                                                                           |     |     |
| HINCRBY device:777:stats requests 1                                                                                                         |     |     |
| HMGET device:777:stats requests errors                                                                                                      |     |     |
| .                                                                                                                                           |     |     |
| HMSET coin heads obverse tails reverse edge null                                                                                            |     |     |
| HRANDFIELD coin                                                                                                                             |     |     |
| HRANDFIELD coin -5 WITHVALUES -> The order of fields in the reply is truly random.                                                          |     |     |
| HRANDFIELD coin +5 WITHVALUES -> The order of fields in the reply is not truly random, so it is up to the client to shuffle them if needed. |     |     |
| .                                                                                                                                           |     |     |

- .
	- Redis hashes are record types structured as collections of field-value pairs.
	- .
	- You can use hashes to represent
		- basic objects and
		- to store groupings of counters,
		- among other things.
		- .
	- redis 中 hashes 的使用场景：
		- ref: https://www.cnblogs.com/east7/p/16271043.html
		- .
		- 存储结构化的数据，比如 Java 中的 Object 。其实 Java 中的 Object 也可以用 string 进行存储，只需要将 Object 序列化成
			json
			字符串就可以，但是如果这个 Object 的某个属性更新比较频繁的话，那么每次就需要重新将整个 Object 序列化存储，这样消耗开销比较大。可如果用
			hash 来存储 Object 的每个属性，那么每次 只需要更新 要更新 的属性就可以。
		- .
		- 购物车场景。
			- 以业务线+用户id作为key，以店铺编号 + 商品的 id 作为存储的 field，以选购商品数量作为键值对的 value，这样就构成了购物车的三个要素。
	- .
	- 儲存 session attributes
		- The session data could be stored in a Redis hash, with the session ID serving as the key and the session data
			being stored as fields within the hash.
	- .
	- Storing user data:
		- Redis hashes can be used to store and manage data about individual users in an application.
		- For example, you could use a Redis hash to store information about a user's profile, such as their name, email
			address, and preferences.
	- .
	- Storing application settings:
		- Redis hashes can be used to store and manage configuration data for an application.
		- For example, you could use a Redis hash to store settings that control the behavior of the application,
		- such as the timeouts for API requests, the minimum and maximum values for input fields, and the default values for
			form fields.
	- .
	- Cache api calls
		- in or out
	- .
	- Caching database query results
		- to reduce the load on the database and improve the performance of the application.
		- The cache could be implemented using Redis hashes,
		- with the cache key being the query itself and the fields within the hash being the results of the query.
	- .
	- 常見的錯誤
		- 用錯指令: HSET, HGET
	- 濫用 HGETALL 指令
	- 刪除的時候，直接整個 hash 刪除
	- .
	- hashes 跟 strings 的比較
		- key expiration
		- hashes 最多放幾千筆資料，不建議放到上萬或上百萬
		- .
- .

.

- 跟 redis Hashes 有關的 Redisson classes
	- 
	-
	- .
- .

.

- .
	- lists
	- Redis lists 是按插入順序排序的 strings 列表。有關詳細信息，請參閱：
		- Redis lists 概述
- .
- <img alt="" class="ce kb kc c" width="422" height="133" loading="lazy" role="presentation" src="https://miro.medium.com/max/422/0*mmsvg6TEGzUdP-0Z.png">
- .
- 使用情境:
	- Queue
	- Stack
	- Circular list 循環列表
	- .
	- Twitter, FB, IG 最新 10 篇貼文
	- 使用者的最新 10 個通知訊息
		- photoshop 特效: undo / redo
		- .
		- 商品 sku counter -> 好處，不會扣到負數。但如果有兩個以上 sku 要同時扣除，也是只能用 Lua script.
		- .
- .

| command                                              |     |     |
|------------------------------------------------------|-----|-----|
| LPUSH mylist <some element>                          |     |     |
| LTRIM mylist 0 99                                    |     |     |
| .                                                    |     |     |
| LPUSH board:todo:ids 101                             |     |     |
| LPUSH board:todo:ids 273                             |     |     |
| .                                                    |     |     |
| LRANGE board:todo:ids 0 0                            |     |     |
| LRANGE board:todo:ids 1 1                            |     |     |
| LRANGE board:todo:ids 10 10                          |     |     |
| LRANGE board:todo:ids -1 -1                          |     |     |
| LRANGE board:todo:ids 0 -1                           |     |     |
| .                                                    |     |     |
| LMOVE board:todo:ids board:in-progress:ids LEFT LEFT |     |     |
| .                                                    |     |     |
| LRANGE board:todo:ids 0 -1                           |     |     |
| LRANGE board:in-progress:ids 0 -1                    |     |     |
| .                                                    |     |     |
| brpop tasks 5                                        |     |     |
| 等待列表中的元素tasks，但如果 5 秒後沒有元素可用則返回                      |     |     |
| .                                                    |     |     |

- notes: Bad Practices
	- Redis lists are great for storing and processing small amounts of data? -> 頭 尾 優化，不適合存取中間
	- 不適合存放 complex data structures
	- If you need to access elements in a Redis list by index, it is better to use a sorted set instead.
	- avoid modifying Redis lists in a loop. 它 不是 array, 不要用 loop 去存取。可以直接用 LTRIM
	- 沒有善用 pipelines
	- .
		- .
- .

.

- 跟 redis Lists 有關的 Redisson classes
	- 
	-
	- .
- .

.

- .
	- sets 集合
	- Redis sets are unordered collections of strings, where each element in the set is unique.
	- 其作用類似於您最喜歡的編程語言（例如，Java HashSets、Python 集等）中的 Set。
		- 使用 Redis sets，您可以添加、刪除和測試是否存在 O(1) 時間（換句話說，無論集元素的數量如何）。
- .
- Redis sets are useful for storing and processing data that needs to be unique, such as user IDs or email addresses.
- .
- 集合有利於表達對象之間的關係。例如，我們可以輕鬆地使用集合來實現 標籤 / 過濾器。
- .
- .
- .
- .

| command                       |     |     |
|-------------------------------|-----|-----|
| OBJECT ENCODING mykey         |     |     |
| set mykey 1234567890123456789 |     |     |

.

- 跟 redis Sets 有關的 Redisson classes
	- 
	-
	- .
- .

.

- .
	- sorted sets
	- Redis sorted sets 是 unique strings 的集合，這些 strings 按每個 strings 的關聯分數保持順序。有關詳細信息，請參閱：
		- Redis sorted sets 概述
- .

| command                       |     |     |
|-------------------------------|-----|-----|
| OBJECT ENCODING mykey         |     |     |
| set mykey 1234567890123456789 |     |     |

.

- 跟 redis Sorted Set 有關的 Redisson classes
	- 
	-
	- .
- .

.

- .
	- stream
	- Redis stream 是一種 資料 結構，其作用類似於僅附加日誌。stream 幫助按事件發生的順序記錄事件，然後聯合它們進行處理。有關詳細信息，請參閱：
		- Redis stream 概述
		- Redis stream 教程
- .

| command                       |     |     |
|-------------------------------|-----|-----|
| OBJECT ENCODING mykey         |     |     |
| set mykey 1234567890123456789 |     |     |

.

- 跟 redis Stream 有關的 Redisson classes
	- 
	-
	- .
- .

.

- .
	- geospatial indexes
	- Redis geospatial indexes 對於查找給定地理半徑或邊界框內的位置很有用。有關詳細信息，請參閱：
		- Redis geospatial indexes 概述
- .

| command                       |     |     |
|-------------------------------|-----|-----|
| OBJECT ENCODING mykey         |     |     |
| set mykey 1234567890123456789 |     |     |

.

- 跟 redis Geo 有關的 Redisson classes
	- 
	-
	- .
- .

.

- .
	- bitmaps
	- Redis bitmaps 可讓您對 strings 執行 bitwise 運算。有關詳細信息，請參閱：
		- Redis bitmaps 概述
- .

| command                       |     |     |
|-------------------------------|-----|-----|
| OBJECT ENCODING mykey         |     |     |
| set mykey 1234567890123456789 |     |     |

.

- 跟 redis Bitmaps 有關的 Redisson classes
	- 
	-
	- .
- .

.

- .
	- bitfields
	- Redis bitfields 有效地將多個計數器編碼為一個 strings 值。位域提供原子獲取、設置和遞增操作，並支持不同的溢出策略。有關詳細信息，請參閱：
		- Redis bitfields 概述
- .

| command                       |     |     |
|-------------------------------|-----|-----|
| OBJECT ENCODING mykey         |     |     |
| set mykey 1234567890123456789 |     |     |

.

- 跟 redis Bitfields 有關的 Redisson classes
	- 
	-
	- .
- .

.

- .
	- HyperLogLog
	- Redis HyperLogLog 資料 結構 提供大型集合的基數（即元素數量）的概率估計。有關詳細信息，請參閱：
		- Redis HyperLogLog 概述
		- .
	- .
- .

| command                       |     |     |
|-------------------------------|-----|-----|
| OBJECT ENCODING mykey         |     |     |
| set mykey 1234567890123456789 |     |     |

.

- 跟 redis HyperLogLog 有關的 Redisson classes
	- 
	-
	- .
- .

.

- Extensions

- 要擴展包含的資料類型提供的功能，請使用以下選項之一：
	- 在 Lua 中編寫您自己的自定義服務器端函數。
	- 使用模塊 API編寫您自己的 Redis 模塊或查看社區支持的模塊。
	- 使用JSON、查詢、時間序列和Redis Stack提供的其他功能。



