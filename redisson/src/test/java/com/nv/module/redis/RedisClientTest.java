package com.nv.module.redis;

import java.sql.Timestamp;

import com.nv.module.redisson.AbstractRedissonBaseTest;
import com.nv.util.RedisUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RList;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RQueue;
import org.redisson.client.codec.Codec;
import org.redisson.config.Config;

public class RedisClientTest extends AbstractRedissonBaseTest {


	@BeforeAll
	public static void beforeAll() {
	}

	@AfterAll
	public static void afterAll() {
		AbstractRedissonBaseTest.afterAll();
	}

	/**
	 *
	 */
	@Test
	public void test() {

		//		System.out.println("test");

		final Config config = getClient().getConfig();

		final Codec codec = config.getCodec();

		System.out.println(codec);

		final String key = RedisUtil.getKey("luke.test");

		getClient().getBucket(key).set("test");
	}

	@Test
	public void testList() {

		String data1 = "data 1";
		String data2 = "data 2";
		String data3 = "data 3";

		final String key = RedisUtil.getKey("luke.test.list", "strList");

		RList<String> list = getClient().getList(key);

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

		RQueue<String> queue = getClient().getQueue(key);

		queue.add(data1);
		queue.add(data2);
		queue.add(data3);

		queue.poll();

		queue.remove(data1);
	}

	@Test
	public void testLocalCachedMap() throws InterruptedException {

		getClient().getMap("test");

		final LocalCachedMapOptions<String, String> options = LocalCachedMapOptions.defaults();

		options
			.reconnectionStrategy(
				LocalCachedMapOptions.ReconnectionStrategy.CLEAR);

		final String key = RedisUtil.getKey("luke.test.localCachedMap", "map");
		System.out.println(key);

		final RLocalCachedMap<String, String> map = getClient().getLocalCachedMap(key, options);

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
