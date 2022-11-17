# Redis 數據類型

https://redis.io/docs/data-types/

https://redis.io/docs/data-types/tutorial/

---

Redis 數據類型 : Redis支持的數據類型概述

Redis 是一個 數據結構 server。Redis 的核心是提供一組原生數據類型，可幫助您解決從緩存到 queuing 再到事件處理 (event
processing) 的各種問題。下面是每種數據類型的簡短描述，以及更廣泛的概述和命令參考的鏈接。

如果您想嘗試綜合教程，請參閱 Redis 數據類型教程 https://redis.io/docs/data-types/tutorial/。

- Core
- .
  - strings
  - Redis strings 是最基本的 Redis 數據類型，代表一個 sequence of bytes。有關詳細信息，請參閱 Redis strings 概述：
    - .
    - Redis strings 儲存 sequences of bytes, 包括 text, serialized objects, and binary arrays. 因此, strings 是最基本的
      Redis data type. 它們通常用於 緩存 caching, 但它們支持額外的功能，使您也可以實現 計數器 和 執行 bitwise 運算.
    - .
    - 例子
      - SET user:1 salvatore
      - SET ticket:27 "\"{'username': 'priya', 'ticket_id': 321}\"" EX 100
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
    - Getting Strings
      - GET retrieves a string value.
      - MGET retrieves multiple string values in a single operation.
        -
          * 實用的技巧
            -.
    - Managing counters
      - INCRBY "atomically" increments (and decrements when passing a negative number) counters stored at a given key.
        - INCRBY {key} 10
        - INCRBY {key} -5
        - INCRBYFLOAT {key} 0.1
      - .
    - Bitwise 運算
      - see the bitmaps data type docs.
      - .
    - Performance
      - 大多數字符串操作的複雜度為 O(1)，這意味著它們非常高效。
      - 但是，請注意 SUBSTR, GETRANGE, and SETRANGE 命令，它們的複雜度可能為 O(n)。
      - 這些隨機訪問字符串命令在處理大字符串時可能會導致性能問題。
      - .
    - Alternatives
      - 如果您將結構化數據存儲為序列化字符串，您可能也想要考慮 Redis hashes 或 RedisJSON。
      - .
    - Learn more
      - Redis 大學的 RU101 詳細介紹了 Redis 字符串。
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



