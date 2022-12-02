# Redisson API

.

# Objects

- These distributed objects follow specifications from the java.util.concurrent.atomic package. They support lock-free,
  thread-safe and atomic operations on objects stored in Redis.
- .

## RBucket aka ObjectHolder

Represented by the RBucket class, this object can hold any type of object. This object has a maximum size of 512MB

|                 | 6.2.5 |                                     | 
|-----------------|-------|-------------------------------------|
| getBucket()     | O     |                                     | 
| getBuckets()    | O     |                                     | 
| getJsonBucket() | X     | need enable Redis.Stack.JSON module | 


## BinaryStreamHolder

- binaryStream.set( "hello world 1".getBytes() );
- final byte[] bytes = binaryStream.get();
- binaryStream.getOutputStream().write( ", hello world 1.1".getBytes() );
- .
- final SeekableByteChannel channel = binaryStream.getChannel();
- channel.write( ByteBuffer.wrap("hello world 2".getBytes()) );
- final int read = channel.read( ByteBuffer.wrap(byteArray) );


## GeospatialHolder

```java
public class Demo {

	public static void main(String[] args) {

		final RGeo<Object> geo = client.getGeo(key + ":1");

		final String geoMemberName = "Palermo";

		geo.add(13.361389, 38.115556, geoMemberName);
	}
}
```


## BitSet

The RBitSet interface in Redisson represents a bit vector that can expand as necessary, with a maximum size of
4,294,967,295 bits.

```java
public class Demo {

	public static void main(String[] args) {

		final String key = RedisUtil.getKey("luke.test", "bitSet", "key");

		final RBitSet bitSet = client.getBitSet(key + ":1");

		bitSet.set(0, true);
		bitSet.set(2, false);
		bitSet.set(4, true);
	}
}
```


## AtomicLong

as title

## AtomicDouble

as title


## Topic

The Topic object supports the Redis' “publish and subscribe” mechanism.

|                                        | 6.2.5 | 7.x | 
|----------------------------------------|-------|-----|
| getTopic()                             | O     | O   | 
| getPatternTopic()                      | O     | O   | 
| getReliableTopic() <br/> // 其實是 stream | O     | O   | 
| getShardedTopic()                      | X     | O   |

```java
public class Demo {

	public static void main(String[] args) {

		RTopic topicChannel = client.getTopic("channel");

		topicChannel.addListener(CustomMessage.class,
			(channel, customMessage) -> future.complete(customMessage.getMessage()));
	}
}
```

- Above, the Topic is registered to listen to messages from the “channel” channel.
- We then add a listener to the topic to handle incoming messages from that channel.
- We can add multiple listeners to a channel.

```java
public class Demo {

	public static void main(String[] args) {

		RTopic topicChannel = client.getTopic("channel");

		long clientsReceivedMessage
			= topicChannel.publish(new CustomMessage("This is a message"));
	}
}
```


## LongAdder

这个类的功能类似于AtomicLong，但是LongAdder的高并发时性能会好很多，非常适合高并发时的计数。（DoubleAdder类似）

## DoubleAdder

这个类的功能类似于AtomicLong，但是LongAdder的高并发时性能会好很多，非常适合高并发时的计数。（DoubleAdder类似）


## BloomFilter

bloomFilter.tryInit(1000, 0.03);

bloomFilter.add("hello");

final boolean contains = bloomFilter.contains("hello");


## HyperLogLog

統計一個頁面的每天被多少個不同賬戶訪問量（Unique Visitor，UV）


## RateLimiter

这个类的目的在于实现一些速度限制实现。但是acquire会阻塞线程，而且不保证公平性。

final boolean result = rateLimiter.trySetRate(RateType.OVERALL, 5, 2, RateIntervalUnit.SECONDS);

final long availablePermits = rateLimiter.availablePermits();

// blocking
rateLimiter.acquire();


## RIdGenerator

这个Id生成器生成一般情况下递增的整数值，效率比较高。

final boolean result = idGenerator.tryInit(1L, 10L);

final long id1 = idGenerator.nextId();



