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

# Objects

- These distributed objects follow specifications from the java.util.concurrent.atomic package. They support lock-free,
  thread-safe and atomic operations on objects stored in Redis.
- .
-

## RBucket aka ObjectHolder

Represented by the RBucket class, this object can hold any type of object. This object has a maximum size of 512MB

|                 | 6.2.5 |                    | 
|-----------------|-------|--------------------|
| getBucket()     | O     |                    | 
| getBuckets()    | O     |                    | 
| getJsonBucket() | X     | need enable module | 

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

## AtomicLong

as title

## AtomicDouble

as title

## Topic

The Topic object supports the Redis' “publish and subscribe” mechanism.

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

## BloomFilter

## HyperLogLog

.

# Collections

## Map

## Set

## List

## Queue





