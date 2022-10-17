# Redisson API

# pre-condition

- using redis 6.2.5
- is clustered

refs:

- https://www.baeldung.com/redis-redisson
- https://dzone.com/articles/overview-of-redisson-the-redis-java-client

# Configurations

Redisson supports connections to the following Redis configurations:

- Single node
- Master with slave nodes
- Sentinel nodes
- Clustered nodes
- Replicated nodes

# Notes

- Redisson supports synchronous, asynchronous and reactive interfaces. Operations over these interfaces are thread-safe.

# RExpirable

Redis 6.x 不支援的 Redisson API

|                    | 6.2.5 | 7.x | 
|--------------------|----|----|
| clearExpire()      | O  | O  | 
| expire( Instant )  | O  | O  | 
| expire( Duration ) | O  | O  | 
| remainTimeToLive() | O  | O  | 
|                    |    |    | 
| expireIfSet()      | X  | O  | 
| expireIfNotSet()   | X  | O  | 
| expireIfGreater()  | X  | O  | 
| expireIfLess()     | X  | O  | 
| getExpireTime()    | X  | O  | 
|                    |    |    | 


