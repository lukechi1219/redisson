package com.nv.module.redis;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RList;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.config.Config;

public class RedisClientTest {

	private static final String fileName = "redis.yaml";
	private static RedissonClient client = null;

	@BeforeAll
	public static void beforeAll() throws IOException {

		URL url = RedisClientTest.class.getClassLoader().getResource("properties/" + fileName);

		if (url == null) {
			throw new RuntimeException("File not found: " + fileName);
		}

		final File file = new File(url.getPath());

		//		System.out.println(file.getAbsolutePath());

		client = Redisson.create(Config.fromYAML(file));
	}

	@AfterAll
	public static void afterAll() {

		if (client != null) {
			client.shutdown();
		}
	}

	public static class RedisUtil {

		public static final String REDIS_TAG = ":";
		private static final String SERVER_TYPE = "redisson";
		public static final String REDIS_PROJECT_ENV_PREFIX = "cps" + REDIS_TAG + SERVER_TYPE + REDIS_TAG;

		public static String getKey(String... values) {

			return REDIS_PROJECT_ENV_PREFIX + String.join(REDIS_TAG, values);
		}
	}

	/**
	 *
	 */
	@Test
	public void test() {

		//		System.out.println("test");

		final Config config = client.getConfig();

		final Codec codec = config.getCodec();

		System.out.println(codec);

		final String key = RedisUtil.getKey("luke.test");

		client.getBucket(key).set("test");
	}

	@Test
	public void testList() {

		String data1 = "data 1";
		String data2 = "data 2";
		String data3 = "data 3";

		final String key = RedisUtil.getKey("luke.test.list", "strList");

		RList<String> list = client.getList(key);

		list.add(data1);
		list.add(data2);
		list.add(data3);

		final String result = list.get(0);
		System.out.println(result);

		list.remove(data1);
	}

	@Test
	public void testQueue() {

		String data1 = "data 1";
		String data2 = "data 2";
		String data3 = "data 3";

		final String key = RedisUtil.getKey("luke.test.queue", "strQueue");

		RQueue<String> queue = client.getQueue(key);

		queue.add(data1);
		queue.add(data2);
		queue.add(data3);

		queue.poll();

		queue.remove(data1);
	}

	@Test
	public void testLocalCachedMap() throws InterruptedException {

		final LocalCachedMapOptions<String, String> options = LocalCachedMapOptions.defaults();

		options
			.reconnectionStrategy(
				LocalCachedMapOptions.ReconnectionStrategy.CLEAR);

		final String key = RedisUtil.getKey("luke.test.localCachedMap", "map");
		System.out.println(key);

		final RLocalCachedMap<String, String> map = client.getLocalCachedMap(key, options);

		map.put("key1", "value1");
		map.put("key2", "value2");

		map.get("key1");

		Thread.sleep(10 * 1000);

		System.out.println(".");
		System.out.println(new Timestamp(System.currentTimeMillis()) + " remove key1");
		map.remove("key1");

		System.out.println(".");
		map.get("key1");
	}
}
