package com.nv.module.redisson;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.nv.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBinaryStream;
import org.redisson.api.RBucket;
import org.redisson.api.RBuckets;
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
		System.out.println(new String(bytes11get));

		final SeekableByteChannel channel = binaryStream.getChannel();

		channel.write(ByteBuffer.wrap("hello world 2".getBytes()));
		System.out.println("size: " + channel.size());

		// MEMO: important to reset position
		channel.position(0);

		final byte[] bytesRead = new byte[(int) channel.size()];
		final int read = channel.read(ByteBuffer.wrap(bytesRead));
		System.out.println("read: " + read);
		System.out.println(new String(bytesRead));
	}

}
