package com.nv.module.redisson;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.nv.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RTimeSeries;
import org.redisson.api.RedissonClient;
import org.redisson.api.mapreduce.RMapReduce;

public class RedissonCollectionTest extends AbstractRedissonBaseTest {

	private final RedissonClient client = getClient();

	/**
	 *
	 */
	private void testRMap(RMap<String, Integer> map) {

		map.put("key1", 1);

		final Integer value1Str = map.get("key1");
		System.out.println("value1Str: " + value1Str);

		final Set<Map.Entry<String, Integer>> entries = map.readAllEntrySet();
		final Set<Map.Entry<String, Integer>> entries1 = map.entrySet();

		final Set<String> keys = map.readAllKeySet();
		final Set<String> keys1 = map.keySet();

		final Collection<Integer> values = map.readAllValues();
		final Collection<Integer> values1 = map.values();

		final Map<String, Integer> maps = map.readAllMap();

		map.addAndGet("key1", 1);

		final boolean containsKey = map.containsKey("key1");
		System.out.println("containsKey: " + containsKey);

		final boolean containsValue = map.containsValue("value1");
		System.out.println("containsValue: " + containsValue);

		map.fastPutIfAbsent("key2", 2);

		map.fastPutIfExists("key2", 3);

		map.putIfAbsent("", 4);

		map.putIfExists("", 5);

		map.compute("key1", (key, value) -> {
			return value;
		});

		map.computeIfAbsent("key1", (key) -> {
			return 1;
		});

		map.computeIfPresent("key1", (key, value) -> {
			return value;
		});

		map.getCountDownLatch("key1");

		map.getLock("key1");

		map.getFairLock("key1");

		map.getReadWriteLock("key1");

		map.getSemaphore("key1");

		map.getPermitExpirableSemaphore("key1");

		map.valueSize("key1");

		map.remove("key1");

		map.replace("key1", 1);

		map.replace("key1", 1, 2);

		final RMapReduce<String, Integer, String, Integer> mapReduce = map.mapReduce();

		// ????
	}

	@Test
	public void testMap() {

		final String key = RedisUtil.getKey("luke.test", "map", "key");

		final RMap<String, Integer> map = client.getMap(key + ":1");

		System.out.println("---");

		/*
		 */
		testRMap(map);

		/*
		 */
		testRExpirable(map);

		System.out.println("-- end --");
	}

	/**
	 *
	 */
	@Test
	public void testMapCache() {

		final String key = RedisUtil.getKey("luke.test", "mapCache", "key");

		final RMapCache<String, Integer> mapCache = client.getMapCache(key + ":1");


		System.out.println("---");

		/*
		 */
		testRMap(mapCache);

		/*
		 */
		testRExpirable(mapCache);

		System.out.println("-- end --");
	}

	/**
	 *
	 */
	@Test
	public void testLocalCachedMap() {

		final String key = RedisUtil.getKey("luke.test", "localCachedMap", "key");

		final RLocalCachedMap<String, Integer> localCachedMap = client.getLocalCachedMap(key + ":1",
			LocalCachedMapOptions.defaults());



		System.out.println("---");

		/*
		 */
		testRMap(localCachedMap);

		/*
		 */
		testRExpirable(localCachedMap);

		System.out.println("-- end --");
	}

	/**
	 *
	 */
	@Test
	public void testSet() {

		final String key = RedisUtil.getKey("luke.test", "timeSeries", "key");

		final RTimeSeries<String> timeSeries = client.getTimeSeries(key + ":1");
	}

	/**
	 *
	 */
	@Test
	public void testList() {

		final String key = RedisUtil.getKey("luke.test", "timeSeries", "key");

		final RTimeSeries<String> timeSeries = client.getTimeSeries(key + ":1");
	}

	/**
	 *
	 */
	@Test
	public void testQueue() {

		final String key = RedisUtil.getKey("luke.test", "timeSeries", "key");

		final RTimeSeries<String> timeSeries = client.getTimeSeries(key + ":1");
	}

	/**
	 *
	 */
	@Test
	public void testStream() {

		final String key = RedisUtil.getKey("luke.test", "timeSeries", "key");

		final RTimeSeries<String> timeSeries = client.getTimeSeries(key + ":1");
	}

	/**
	 *
	 */
	@Test
	public void testTimeSeries() throws InterruptedException {

		final String key = RedisUtil.getKey("luke.test", "timeSeries", "key");

		final RTimeSeries<String> timeSeries = client.getTimeSeries(key + ":1");

		timeSeries.add(System.currentTimeMillis(), "1 value");

		Thread.sleep(1000);

		timeSeries.add(System.currentTimeMillis(), "2 value");

		timeSeries.stream().forEach(value -> {
			System.out.println(value);
		});

		final long end = System.currentTimeMillis();

		timeSeries.entryRange(end - 60000, end).forEach(entry -> {
			System.out.println(entry);
		});

		System.out.println("---");

		/*
		 */
		testRExpirable(timeSeries);

		System.out.println("-- end --");
	}
}
