package com.nv.module.redisson;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.nv.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.redisson.api.GeoPosition;
import org.redisson.api.RAtomicDouble;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBinaryStream;
import org.redisson.api.RBitSet;
import org.redisson.api.RBucket;
import org.redisson.api.RBuckets;
import org.redisson.api.RGeo;
import org.redisson.api.RedissonClient;

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

	@Test
	public void testTopic() {

	}
}
