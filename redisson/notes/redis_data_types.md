# Redis 數據類型

https://try.redis.io/

https://medium.com/analytics-vidhya/the-most-important-redis-data-structures-you-must-understand-2e95b5cf2bce

https://redis.io/docs/data-types/

https://redis.io/docs/data-types/tutorial/

---

Redis 數據類型 : Redis支持的數據類型概述

- Redis 是一個 數據結構 server。
- Redis 的核心, 是提供一群 原生數據類型，可幫助您解決從 緩存 到 queuing 再到 事件處理 (event processing) 的各種問題。
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
  - Redis strings 是最基本的 Redis 數據類型，代表一串 sequence of bytes (010101010101....)。
  - 由於 Redis key 是 strings，當我們也使用 strings 類型作為值時，我們是將一個 strings 映射到另一個 strings。
  - strings 數據類型可用於許多 use case，例如 緩存 HTML 片段 or 頁面 or jpeg 圖像 (而不用直接讀取硬碟)。
    - .
    - Redis strings 儲存 sequences of bytes, 包括 text, serialized objects, and binary arrays.
    - 因此, strings 是最基本的 Redis data type.
    - 它們通常用於 緩存 caching, 但它們支持額外的功能，使您也可以實現 計數器 和 執行 bitwise 運算.
    - .

| command                                                            |     |     |
|--------------------------------------------------------------------|-----|-----|
| SET user:1 salvatore                                               |     |     |
| SET ticket:27 "\"{'username': 'priya', 'ticket_id': 321}\"" EX 100 |     |     |
| INCR views:page:2                                                  |     |     |
| INCRBY views:page:2 10                                             |     |     |
| set mykey newval                                                   |     |     |
| getset mykey newval2                                               |     |     |
| get mykey                                                          |     |     |
| set mykey 100                                                      |     |     |
| type mykey                                                         |     |     |
|                                                                    |     |     |
|                                                                    |     |     |

- . 
  - .
    - .
    - 增加一個計數器
      - INCR views:page:2
      - INCRBY views:page:2 10
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
      - INCRBY "atomically" increments (and decrements when passing a negative number) counters stored at a given key.
        - INCRBY {key} 10
        - INCRBY {key} -5
        - INCRBYFLOAT {key} 0.1
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
      - 如果您將結構化數據存儲為序列化 strings，您可能也想要考慮 Redis hashes 或 RedisJSON。
      - .
    - Learn more
      - Redis 大學的 RU101 詳細介紹了 Redis strings。
      - .
    - .
  - .
- .
- Keys
  - Redis keys 是 binary safe 的，這意味著您可以使用任何二進制序列作為密鑰，從 “foo” 這樣的 strings 到 JPEG
    文件的內容。空字串 "" 也是一個有效的 key。
  - 很長的鍵不是一個好主意。例如，1024 字節的鍵不僅在內存方面是個壞主意，而且因為在數據集中查找鍵可能需要多次代價高昂的鍵比較。
  - 非常短的鍵通常不是一個好主意。如果您可以寫成“user:1000:followers”，那麼將“u1000flw”寫成鍵就沒什麼意義了。後者更具可讀性。
  - 訂出屬於我們自己的規範:
    - “object-type:id”是個好主意
    - user:1000
    - comment:4321:reply.to 或 comment:4321:reply-to 是兩種不同的命名規範
    - .
  - .
- .
- Key expiration
  - 在繼續之前，我們應該了解一個重要的 Redis 功能，無論您存儲的值是什麼類型，它都可以 work：Key expiration。Key expiration 允許您為 key 設置 timeout，也稱為 “生存時間 time to live ” 或 “TTL”。
  - 當過期時間過去時，key 將自動銷毀。
    - 可以使用 秒 或 毫秒 精度設置它們。
    - 但是，過期時間 分辨率 始終為 1 毫秒。
    - 關於過期的信息會被複製並保存在磁盤上，當您的 Redis 服務器保持停止時，時間實際上已經過去了（這意味著 Redis 會保存密鑰過期的日期）。
  - set mykey 100 ex 100
  - ttl key
  - .
- .
-
  - lists
  - Redis lists 是按插入順序排序的 strings 列表。有關詳細信息，請參閱：
    - Redis lists 概述
- .
-
  - sets
  - Redis sets 是 unique strings 的無序集合，其作用類似於您最喜歡的編程語言（例如，Java HashSets、Python 集等）中的集。使用
    Redis 集，您可以添加、刪除和測試是否存在 O(1) 時間（換句話說，無論集元素的數量如何）。有關詳細信息，請參閱：
    - Redis sets 概述
- .
-
  - hashes
  - Redis hashes 是建模為字段值對集合的記錄類型。因此，Redis 哈希類似於Python 字典、Java HashMaps 和 Ruby 哈希。有關詳細信息，請參閱：
    - Redis hashes 概述
- .
  - sorted sets
  - Redis sorted sets 是 unique strings 的集合，這些 strings 按每個 strings 的關聯分數保持順序。有關詳細信息，請參閱：
    - Redis sorted sets 概述
- .
-
  - stream
  - Redis stream 是一種數據結構，其作用類似於僅附加日誌。stream 幫助按事件發生的順序記錄事件，然後聯合它們進行處理。有關詳細信息，請參閱：
    - Redis stream 概述
    - Redis stream 教程
- .
  - geospatial indexes
  - Redis geospatial indexes 對於查找給定地理半徑或邊界框內的位置很有用。有關詳細信息，請參閱：
    - Redis geospatial indexes 概述
- .
-
  - bitmaps
  - Redis bitmaps 可讓您對 strings 執行 bitwise 運算。有關詳細信息，請參閱：
    - Redis bitmaps 概述
- .
-
  - bitfields
  - Redis bitfields 有效地將多個計數器編碼為一個 strings 值。位域提供原子獲取、設置和遞增操作，並支持不同的溢出策略。有關詳細信息，請參閱：
    - Redis bitfields 概述
- .
-
  - HyperLogLog
  - Redis HyperLogLog 數據結構提供大型集合的基數（即元素數量）的概率估計。有關詳細信息，請參閱：
    - Redis HyperLogLog 概述

.

- Extensions

- 要擴展包含的數據類型提供的功能，請使用以下選項之一：
  - 在 Lua 中編寫您自己的自定義服務器端函數。
  - 使用模塊 API編寫您自己的 Redis 模塊或查看社區支持的模塊。
  - 使用JSON、查詢、時間序列和Redis Stack提供的其他功能。



