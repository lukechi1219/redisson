package com.nv.module.redis;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.nv.module.redisson.AbstractRedissonBaseTest;
import com.nv.util.RedisUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.ExpiredObjectListener;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RBucket;
import org.redisson.api.RFuture;
import org.redisson.api.RList;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RQueue;
import org.redisson.api.RStream;
import org.redisson.api.RedissonClient;
import org.redisson.api.StreamMessageId;
import org.redisson.api.stream.StreamAddArgs;
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
	public void test() throws InterruptedException {

		//		System.out.println("test");

		final Config config = getClient().getConfig();

		final Codec codec = config.getCodec();

		System.out.println(codec);

		final String key = RedisUtil.getKey("luke.test");

		final RBucket<String> bucket = getClient().getBucket(key);

		bucket.addListener((ExpiredObjectListener) name -> {
			System.out.println("bucket expired");
			System.out.println("ExpiredObjectListener: " + name);
		});

		bucket.set("test", 10L, TimeUnit.SECONDS);
		//		bucket.set("test");
		//		bucket.expire(Duration.ofSeconds(10L));

		Thread.sleep(15000L);

		System.out.println("test end");
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

	@Test
	public void testStream() {

		final String key = RedisUtil.getKey("luke.test.stream", "strStream");

		final RStream<String, String> stream = getClient().getStream(key);

		final boolean noneMatch = stream.listGroups()
			.stream()
			.noneMatch(group -> group.getName().equals("group"));

		if (noneMatch) {
			stream.createGroup("group");
		}

		final RFuture<StreamMessageId> streamMessageIdRFuture = stream.addAsync(
			StreamAddArgs.entry("key", "value"));

		streamMessageIdRFuture.thenAccept(System.out::println);
	}

	/**
	 * test Round Trip Time
	 */
	@Test
	public void testLargeFileRTT() {

		final RedissonClient client = getClient();
		final RBucket<String> bucketTest = client.getBucket("cps:redisson:luke.test:strings:1kb");
		final String str = bucketTest.get();

		final String append = "A";
		final int base = 1024;
		final HashMap<String, String> map = new HashMap<>();

		/*
		 * prepare data
		 */
		for (int idx = 1; idx <= 768; idx++) {

			String data = "";

			final int multiple = idx * base;

			if (idx > 10 && multiple % (10 * base) != 0) {
				continue;
			}

			final String key = RedisUtil.getKey("luke.test", "strings", "" + idx + "kb");

			final String sample = map.get("cps:redisson:luke.test:strings:10kb");

			if (sample == null) {
				for (int i = 0; i < multiple; i++) {
					data += append;
				}
			} else {
				for (int i = 0; i < (multiple / (10 * 1024)); i++) {
					data += sample;
				}
			}

			System.out.println(key + ": " + data.length() / 1024);
			map.put(key, data);
		}

		/*
		 * test redis
		 */
		for (int idx = 1; idx <= 768; idx++) {

			final int multiple = idx * base;

			if (idx > 10 && multiple % (10 * base) != 0) {
				continue;
			}

			final String key = RedisUtil.getKey("luke.test", "strings", "" + idx + "kb");

			final String dataInMap = map.get(key);

			//			final int length = dataInMap.length();

			long start = System.currentTimeMillis();
			final RBucket<String> bucket = client.getBucket(key);
			long end = System.currentTimeMillis();
			long costTime1 = end - start;

			start = System.currentTimeMillis();
			final String origen = bucket.get();
			end = System.currentTimeMillis();
			long costTime2 = end - start;

			start = System.currentTimeMillis();
			bucket.set(dataInMap);
			end = System.currentTimeMillis();
			long costTime3 = end - start;
			//			long costTime3 = 0;

			start = System.currentTimeMillis();
			final String result = bucket.get();
			end = System.currentTimeMillis();
			long costTime4 = end - start;

			bucket.expire(Duration.ofMinutes(30L));

			//			System.out.println("----");
			//			System.out.println(idx + " kb");
			//			System.out.println("data bytes: " + length);
			//			System.out.println("getBucket costTime1: " + costTime1);
			//			System.out.println("get costTime2: " + costTime2);
			//			System.out.println("set costTime3: " + costTime3);
			//			System.out.println("get costTime4: " + costTime4);

			System.out.println(idx + "kb, " + costTime1 + ", " + costTime2 + ", " + costTime3 + ", " + costTime4);
		}
	}

	@Test
	public void testGet() {

		final RedissonClient client = getClient();

		final String postFix = "42kb";
		final String key = "cps:redisson:luke.test:strings:" + postFix;

		final RBucket<String> bucket = client.getBucket(key);

		final String data = bucket.get();

		final int length = data.length();

		System.out.println("key: " + postFix + ", length: " + length / 1024);
	}

	@Test
	public void testMaxOps() throws InterruptedException {

		final RedissonClient client = getClient();

		client.getBucket("cps:redisson:luke.test:strings:1kb").get();
		client.getBucket("cps:redisson:luke.test:strings:1kb").get();
		client.getBucket("cps:redisson:luke.test:strings:1kb").get();

		ExecutorService executor = Executors.newFixedThreadPool(5000);

		for (int idx = 1; idx < 50; idx++) {

			if (idx > 10 && idx % 10 != 0) {
				continue;
			}

			final String key = "cps:redisson:luke.test:strings:" + idx + "kb";

			final AtomicLong atomicLong = new AtomicLong();

			final Runnable runnable = () -> {
				long now = System.currentTimeMillis();
				long end1 = 0;
				do {
					final RBucket<String> bucket = client.getBucket(key);

					final String data = bucket.get();

					if (data != null) {
						atomicLong.incrementAndGet();
					}
					end1 = System.currentTimeMillis();

				} while (end1 - now < 1000);
			};

			long nowOuter = System.currentTimeMillis();
			long endOuter = 0;

			int count = 0;

			//			System.out.println(new Timestamp(System.currentTimeMillis()));
			do {
				count++;
				//				if (atomicLong.get() == 1) {
				//					System.out.println("data length: " + (data.length() / 1024));
				//				}
				//				System.out.println(runnable);
				//								runnable.run();

				executor.execute(runnable);

				if (count % 2 == 0) {
					Thread.sleep(1);
				}

				endOuter = System.currentTimeMillis();

			} while (endOuter - nowOuter <= 1000 && count <= 1000);

			System.out.println("count: " + count);
			System.out.println(idx + " kb, ops, " + atomicLong.get());
			//			System.out.println(new Timestamp(System.currentTimeMillis()));

			System.out.println("----");
			Thread.sleep(1000);
		}

		executor.shutdown();
	}
}
