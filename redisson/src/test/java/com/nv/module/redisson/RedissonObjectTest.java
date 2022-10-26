package com.nv.module.redisson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.nv.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.redisson.api.GeoPosition;
import org.redisson.api.RAtomicDouble;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBinaryStream;
import org.redisson.api.RBitSet;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RBucket;
import org.redisson.api.RBuckets;
import org.redisson.api.RDoubleAdder;
import org.redisson.api.RGeo;
import org.redisson.api.RHyperLogLog;
import org.redisson.api.RIdGenerator;
import org.redisson.api.RLiveObjectService;
import org.redisson.api.RLongAdder;
import org.redisson.api.RPatternTopic;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RReliableTopic;
import org.redisson.api.RScript;
import org.redisson.api.RShardedTopic;
import org.redisson.api.RTopic;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateLimiterConfig;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.StatusListener;
import org.redisson.client.codec.StringCodec;

public class RedissonObjectTest extends AbstractRedissonBaseTest {

	private final RedissonClient client = getClient();

	@Test
	public void testRedissonBucket() {

		final String key = RedisUtil.getKey("luke.test", "bucket", "key");

		final RBucket<String> bucketStr1 = client.getBucket(key + ":string1");
		bucketStr1.set("hello world 1");

		final RBucket<String> bucketStr2 = client.getBucket(key + ":string2");
		bucketStr2.set("hello world 2");

		final RBucket<AtomicInteger> bucketStr3 = client.getBucket(key + ":atomicInteger3");
		bucketStr3.set(new AtomicInteger(1));

		final RBuckets buckets = client.getBuckets();

		final Map<String, Object> map = buckets.get(
			key + ":string1",
			key + ":string2",
			key + ":atomicInteger3"
		);

		for (Map.Entry<String, Object> entry : map.entrySet()) {

			System.out.println(entry.getKey() + " : " + entry.getValue());
		}

		/*
		 * ERR unknown command `JSON.SET`
		 * require to enable redis module: redis-server --loadmodule redis-json.so
		 */
		/*
		final RJsonBucket<AtomicInteger> jsonBucket = client.getJsonBucket(key + ":json",
			new JacksonCodec<>(AtomicInteger.class));

		final AtomicInteger atomicInteger = new AtomicInteger(1);
		jsonBucket.set(atomicInteger);

		final AtomicInteger result = jsonBucket.get();
		System.out.println(result.incrementAndGet());
		 */

		/*
		 * RExpirable
		 */
		testRExpirable(bucketStr1);

		/*
		 * RObject
		 */
		testRObject(bucketStr1);
	}

	@Test
	public void testRedissonBucket2() {

		final String key = RedisUtil.getKey("luke.test", "bucket", "key");

		final String redisKey = key + ":object1";

		final RBucket<Object> bucketObj1 = client.getBucket(redisKey);

		bucketObj1.set(10);

		final Object value = bucketObj1.get();

		System.out.println("value: " + value.getClass().getSimpleName());

		final String luaScript = "return redis.call('decr', KEYS[1])";

		final RScript script = client.getScript();

		for (int i = 1; i <= 10; i++) {
			try {
				final Object eval = script.eval(RScript.Mode.READ_WRITE, luaScript, RScript.ReturnType.VALUE,
					Arrays.asList(new String[] {redisKey}));

				System.out.println(eval);

				if (Integer.parseInt(String.valueOf(eval)) == 0) {
					bucketObj1.set("sold out");

					final Object newValue = bucketObj1.get();
					System.out.println("newValue: " + newValue.getClass().getSimpleName());
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
	}

	@Test
	public void testBufferedRead() throws IOException {

		final String key = RedisUtil.getKey("luke.test", "binaryStream", "key");

		final RBucket<String> bucket = client.getBucket(key + ":1", new StringCodec());
		System.out.println(bucket.get());

		final RBinaryStream binaryStream = client.getBinaryStream(key + ":1");

		final InputStream inputStream = binaryStream.getInputStream();

		String data = "";
		byte[] buffer = new byte[10];

		int read;

		while ((read = inputStream.read(buffer)) != -1) {
			System.out.println("read: " + read);
			data += new String(buffer, 0, read, StandardCharsets.UTF_8);
		}

		System.out.println(data);
	}

	@Test
	public void testBinaryStreamHolder() throws IOException {

		final String key = RedisUtil.getKey("luke.test", "binaryStream", "key");

		final RBinaryStream binaryStream = client.getBinaryStream(key + ":1");

		binaryStream.set("hello world 1".getBytes(StandardCharsets.UTF_8));
		final byte[] bytes1;
		bytes1 = binaryStream.get();
		System.out.println(new String(bytes1, StandardCharsets.UTF_8));

		final byte[] bytes11 = ", hello world 1.1".getBytes(StandardCharsets.UTF_8);
		binaryStream.getOutputStream().write(bytes11);
		final byte[] bytes11get = binaryStream.get();
		String data11 = new String(bytes11get, StandardCharsets.UTF_8);
		System.out.println(data11);

		final SeekableByteChannel channel = binaryStream.getChannel();

		ByteBuffer data2 = ByteBuffer.wrap("hello world 2".getBytes(StandardCharsets.UTF_8));
		channel.write(data2);
		System.out.println("size: " + channel.size());

		// MEMO: important to reset position
		channel.position(0);

		final byte[] bytesRead = new byte[(int) channel.size()];
		final int read = channel.read(ByteBuffer.wrap(bytesRead));
		System.out.println("read: " + read);
		System.out.println(new String(bytesRead, StandardCharsets.UTF_8));
	}

	@Test
	public void testGeospatialHolder() {

		final String key = RedisUtil.getKey("luke.test", "geospatialHolder", "key");

		final RGeo<Object> geo = client.getGeo(key + ":1");

		final String geoMemberName = "Palermo";

		geo.add(13.361389, 38.115556, geoMemberName);

		final Map<Object, GeoPosition> pos = geo.pos(geoMemberName);

		System.out.println(pos);
	}

	@Test
	public void testBitSet() {

		final String key = RedisUtil.getKey("luke.test", "bitSet", "key");

		final RBitSet bitSet = client.getBitSet(key + ":1");
		bitSet.clear();

		bitSet.set(0, true);
		bitSet.set(2, false);
		bitSet.set(3, true);

		final long size = bitSet.size();
		System.out.println("size: " + size);

		final long length = bitSet.length();
		System.out.println("length: " + length);
	}

	@Test
	public void testAtomicLong() {

		final String key = RedisUtil.getKey("luke.test", "atomicLong", "key");

		final RAtomicLong atomicLong = client.getAtomicLong(key + ":1");

		// INCR
		// return the value of key after the increment
		final long longVal1 = atomicLong.incrementAndGet();
		System.out.println("longVal1: " + longVal1);

		// DECR
		// return the value of key after the decrement
		final long longVal2 = atomicLong.decrementAndGet();
		System.out.println("longVal2: " + longVal2);

		// INCRBY
		final long longVal3 = atomicLong.getAndIncrement();
		System.out.println("longVal3: " + longVal3);

		// DECRBY + convertor
		final long longVal4 = atomicLong.getAndDecrement();
		System.out.println("longVal4: " + longVal4);
	}

	@Test
	public void testAtomicDouble() {

		final String key = RedisUtil.getKey("luke.test", "atomicDouble", "key");

		final RAtomicDouble atomicDouble = client.getAtomicDouble(key + ":1");
		atomicDouble.set(0.1045);

		final boolean result = atomicDouble.compareAndSet(1, 2);
		System.out.println("result: " + result);

		// INCR
		// return the value of key after the increment
		final double doubleVal1 = atomicDouble.incrementAndGet();
		System.out.println("doubleVal1: " + doubleVal1);

		// DECR
		// return the value of key after the decrement
		final double doubleVal2 = atomicDouble.decrementAndGet();
		System.out.println("doubleVal2: " + doubleVal2);

		// INCRBY
		final double doubleVal3 = atomicDouble.getAndIncrement();
		System.out.println("doubleVal3: " + doubleVal3);

		// DECRBY + convertor
		final double doubleVal4 = atomicDouble.getAndDecrement();
		System.out.println("doubleVal4: " + doubleVal4);
	}

	/**
	 * LongAdder
	 * 这个类的功能类似于AtomicLong，但是LongAdder的高并发时性能会好很多，非常适合高并发时的计数。（DoubleAdder类似）
	 * <p>
	 * 底層是 RTopic
	 */
	@Test
	public void testAdder() {

		final String key1 = RedisUtil.getKey("luke.test", "longAdder", "key");
		final String key2 = RedisUtil.getKey("luke.test", "doubleAdder", "key");

		final RLongAdder longAdder = client.getLongAdder(key1 + ":1");
		longAdder.increment();
		final long sumLong = longAdder.sum();
		System.out.println("sumLong: " + sumLong);

		final RDoubleAdder doubleAdder = client.getDoubleAdder(key2 + ":1");
		doubleAdder.increment();
		final double sumDouble = doubleAdder.sum();
		System.out.println("sumDouble: " + sumDouble);
	}

	@Test
	public void testTopic() throws InterruptedException {

		final String channel = RedisUtil.getKey("luke.test", "topic", "key");

		final RTopic topic = client.getTopic(channel + ":1");

		final RPatternTopic patternTopic = client.getPatternTopic(channel + ":*");

		System.out.println(topic.getChannelNames());

		topic.removeAllListeners();

		/*
		SUBSCRIBE cps:redisson:luke.test:topic:key:1
		 */
		final int listenerId1 = topic.addListener(new StatusListener() {

			@Override
			public void onSubscribe(String channel) {
				System.out.println("onSubscribe: " + channel);
			}

			@Override
			public void onUnsubscribe(String channel) {
				System.out.println("onUnsubscribe: " + channel);
			}
		});

		/*
		SUBSCRIBE cps:redisson:luke.test:topic:key:1
		 */
		final int listenerId2 = topic.addListener(String.class, (channel1, msg) -> {

			System.out.println("onMessage: " + channel1 + ", " + msg);
			System.out.println(".");
		});

		/*
		PSUBSCRIBE cps:redisson:luke.test:topic:key:*
		 */
		patternTopic.addListener(String.class, (pattern, channel12, msg) -> {

			System.out.println("onMessage: pattern " + pattern);
			System.out.println("onMessage: channel " + channel12);
			System.out.println("onMessage: msg " + msg);
			System.out.println(".");
		});

		/*
		PUBSUB NUMSUB cps:redisson:luke.test:topic:key:1
		 */
		System.out.println("countSubscribers:" + topic.countSubscribers());

		/*
		PUBLISH cps:redisson:luke.test:topic:key:1 "hello world"
		 */
		topic.publish("hello world");

		System.out.println("countListeners: " + topic.countListeners());

		Thread.sleep(1000);

		topic.removeListener(listenerId1);
		topic.removeListener(listenerId2);

		Thread.sleep(1000);

		System.out.println("countListeners: " + topic.countListeners());

		System.out.println("done");
	}

	/**
	 * 其實是 stream
	 */
	@Test
	public void testReliableTopic() throws InterruptedException {

		final String channel = RedisUtil.getKey("luke.test", "reliableTopic", "key");

		final RReliableTopic reliableTopic = client.getReliableTopic(channel + ":1");

		reliableTopic.expire(Duration.ofMinutes(1L));

		/*
		xadd cps:redisson:luke.test:reliableTopic:key:1 * m "hello reliableTopic"
		 */
		reliableTopic.publish("hello reliableTopic");

		/*
		Amount of messages stored in Redis Stream object.
		 */
		System.out.println("size: " + reliableTopic.size());

		final String listenerId = reliableTopic.addListener(String.class, (channel1, msg) -> {

			System.out.println("onMessage: " + channel1 + ", " + msg);
			System.out.println(".");
		});

		System.out.println(listenerId);

		Thread.sleep(1000);

		reliableTopic.removeListener(listenerId);
	}

	/**
	 * ERR unknown command `SPUBLISH`
	 */
	@Test
	public void testShardedTopic() {

		final String channel = RedisUtil.getKey("luke.test", "shardedTopic", "key");

		final RShardedTopic shardedTopic = client.getShardedTopic(channel + ":1");

		shardedTopic.publish("hello shardedTopic");
	}

	/**
	 *
	 */
	@Test
	public void testBloomFilter() {

		final String key = RedisUtil.getKey("luke.test", "bloomFilter", "key");

		final RBloomFilter<String> bloomFilter = client.getBloomFilter(key + ":1");

		bloomFilter.tryInit(16, 0.03);

		bloomFilter.add("hello");

		final long count = bloomFilter.count();
		System.out.println("count: " + count);

		final boolean contains = bloomFilter.contains("hello");
		System.out.println("contains: " + contains);

		/*
		 *
		 */
		testRExpirable(bloomFilter);
	}

	/**
	 * PFADD
	 * <p>
	 * <a href="https://iter01.com/678217.html">...</a>
	 * <p>
	 * 統計一個頁面的每天被多少個不同賬戶訪問量（Unique Visitor，UV）
	 * <p>
	 * <a href="https://redis.io/docs/data-types/hyperloglogs/">...</a>
	 */
	@Test
	public void testHyperLogLog() {

		final String key = RedisUtil.getKey("luke.test", "hyperLogLog", "key");

		final RHyperLogLog<String> hyperLogLog = client.getHyperLogLog(key + ":1");

		hyperLogLog.add("hello 1");
		hyperLogLog.add("hello 2");
		hyperLogLog.add("hello 1");
		hyperLogLog.add("hello 3");

		final long count = hyperLogLog.count();
		System.out.println("count: " + count);

		/*
		 *
		 */
		testRExpirable(hyperLogLog);
	}

	/**
	 * TODO: study more on this
	 */
	@Test
	public void testRateLimiter() {

		final String key = RedisUtil.getKey("luke.test", "rateLimiter", "key");

		final RRateLimiter rateLimiter = client.getRateLimiter(key + ":1");

		// Initialization required only once.
		// 5 permits per 2 seconds
		final boolean result = rateLimiter.trySetRate(RateType.OVERALL, 5, 2, RateIntervalUnit.SECONDS);
		System.out.println("trySetRate: " + result);

		final RateLimiterConfig config = rateLimiter.getConfig();
		System.out.println("config: " + config);

		final long availablePermits = rateLimiter.availablePermits();
		System.out.println("availablePermits: " + availablePermits);

		// blocking
		rateLimiter.acquire();

		/*
		 *
		 */
		testRExpirable(rateLimiter);
	}

	/**
	 * setnx
	 * <p>
	 * Redis Setnx（SET if Not eXists） 命令在指定的 key 不存在时，为 key 设置指定的值
	 */
	@Test
	public void testIdGenerator() throws InterruptedException {

		final String key = RedisUtil.getKey("luke.test", "idGenerator", "key");

		final RIdGenerator idGenerator = client.getIdGenerator(key + ":1");

		final AtomicInteger atomicInteger = new AtomicInteger(0);

		final Runnable runnable = () -> {

			final int idx = atomicInteger.incrementAndGet();

			final boolean result = idGenerator.tryInit(1L, 10L);
			System.out.println(idx + " tryInit: " + result);

			final long id1 = idGenerator.nextId();
			System.out.println(idx + " 1 nextId: " + id1);

			final long id2 = idGenerator.nextId();
			System.out.println(idx + " 2 nextId: " + id2);

			final long id3 = idGenerator.nextId();
			System.out.println("3 nextId: " + id3);

			final long id4 = idGenerator.nextId();
			System.out.println("4 nextId: " + id4);
			final long id5 = idGenerator.nextId();
			System.out.println("5 nextId: " + id5);
			final long id6 = idGenerator.nextId();
			System.out.println("6 nextId: " + id6);
			final long id7 = idGenerator.nextId();
			System.out.println("7 nextId: " + id7);
			final long id8 = idGenerator.nextId();
			System.out.println("8 nextId: " + id8);
			final long id9 = idGenerator.nextId();
			System.out.println("9 nextId: " + id9);

			final long id10 = idGenerator.nextId();
			System.out.println(idx + " 10 nextId: " + id10);

			//			final long id11 = idGenerator.nextId();
			//			System.out.println("nextId: " + id11);
		};

		ExecutorService executorService = Executors.newFixedThreadPool(1);
		//		ExecutorService executorService = Executors.newFixedThreadPool(2);

		executorService.execute(runnable);

		executorService.execute(runnable);

		Thread.sleep(10000);

		/*
		 *
		 */
		testRExpirable(idGenerator);
	}

	@Test
	public void testLiveObject() {

		final String key = RedisUtil.getKey("luke.test", "idGenerator", "key");

		final RLiveObjectService liveObjectService = client.getLiveObjectService();
	}
}
