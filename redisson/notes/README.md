# Redis & Redisson

# Learning Path 學習路徑

```mermaid
graph TD;
    背景知識-->data types Basic;
    A-->C;
    B-->D;
    C-->D;
```

```mermaid
graph TD;
  背景知識-->data types: Basic;
  data types: Basic-->data types: Special;
  data types: Special-->redis 其他功能;
  redis 其他功能-->Redisson;
  Redisson-->Spring Data Redis;
```

- 背景知識
	- 效能: 時間複雜度
		- O(1) (constant-time),
		- O(log(n)) (logarithmic),
		- O(n) (linear-time)
	- .
	- 原子操作 atomic operation
	- .
	- race condition: 執行緒 或 程序 訪問資料的先後順序決定了資料修改的結果
		- redis 基本型別保證 原子操作 安全, 但是無法避免 race condition
			- i++ ok
			- set i = 1, set i = 2 -> 看誰先搶到
			- get then set -> 看誰先搶到
			- SET account:21_sam "{"name": "sam", "balance": 1,000,000}"
			- .
	- redis 提供 交易控制 -> 基本型別 之外的 原子操作
	- .
	- redis 是 單線程 還是 多線程?
	- .
	- 把某個 key 設定過期時間之後，redis server 重新啟動了，這個 key 還會過期嗎？
	- .
	- Redis.com 有推出 雲端解決方案 Redis Enterprise
		- 自動擴展 / HA / 備份 / 災難復原
		- 增加記憶體, 升級, 完全 不影響 現有服務的運作
		- 30 MB RAM 免費
	- .
- redis
	- redis 開發規範
	- redis data types
	- php
		- A Twitter-toy clone written in PHP and Redis, used in the early days to introduce Redis data types.
		- 使用 Predis
		- https://redis.io/docs/manual/patterns/twitter-clone/
		- https://github.com/antirez/retwis
		- .
	- .
- redisson
	- redisson_api.mmd 心智圖
	- redisson api -> Redis 6.x 不支援的 Redisson API
	- redisson api additional
		- 批次執行，Transaction 交易機制，Lua Script
			- 建議: Redis事務功能較弱，不建議過多使用 by 阿里雲 Redis 開發規範
			- Redis的事務功能較弱 (不支持回滾)，而且集群版本(自研和官方)要求一次事務操作的key必須在一個slot上(
				可以使用hashtag功能解決)
		- 多個連續命令可以通過 RBatch 在一次網絡請求裡合併發送，這樣省去了產生多個請求消耗的時間和資源。這在 Redis 中叫做
			pipeline。
	- redisson api objects
	- redisson api collections
	- redisson api locks
	- redisson api services
	- .
- Spring Data Redis
	- redisson-spring-boot-starter
	- Jedis & Lettuce
		- Redisson distinguishes itself from Lettuce because it is a higher-level client with another layer of abstraction,
			offering collections and other interfaces instead of raw Redis commands.
		- .
	- https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#why-spring-redis
	- https://github.com/spring-attic/spring-data-keyvalue-examples
	- https://docs.spring.io/spring-data/data-keyvalue/examples/retwisj/current/
	- .
- Spring Data Redis + Redisson
	- Redisson + redis + springboot + 哨兵模式
	- https://segmentfault.com/q/1010000041193322
	- .
	- springboot整合redisson
		- 怎麼設定 兩組 client?
			- cluster
			- sentinel
			- 可參考以下文章
			- https://www.cnblogs.com/east7/p/16255253.html
			- .
		- https://github.com/redisson/redisson/tree/master/redisson-spring-boot-starter
		- https://zhuanlan.zhihu.com/p/380530036
		- https://blog.csdn.net/qq_27579471/article/details/103921489
		- Redisson 的优势
			- Redisson 提供了一个监控锁的看门狗（watch dog），它的作用是在Redisson实例被关闭前，不断(默认每10s)的延长锁(
				redis中的目标key)的有效期(默认续期到30s)
				，也就是说，如果一个拿到锁的线程一直没有完成逻辑，那么看门狗会帮助线程不断的延长锁的超时时间，锁不会因为超时而被释放。加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，锁默认会在30s内自动过期，不会产生死锁问题；
				————————————————
				版权声明：本文为CSDN博主「qq_三哥啊」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
				原文链接：https://blog.csdn.net/qq_27579471/article/details/103921489
			- .
		- Redis操作注入RedisTemplate进行使用 ?
		- Redission分布式锁可以引入RedissionClient进行使用
		- https://www.cnblogs.com/east7/p/16255253.html
		- http://www.yinzhongnet.com/526.html
	- .
- .

