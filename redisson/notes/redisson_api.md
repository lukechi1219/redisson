# Redisson API

# pre-condition

- using redis 6.2.5
- is clustered

refs:

- https://www.baeldung.com/redis-redisson

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

## ObjectHolder

Represented by the RBucket class, this object can hold any type of object. This object has a maximum size of 512MB

## BinaryStreamHolder

## GeospatialHolder

## BitSet

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

# Collections

## Map

## Set

## List

## Queue





