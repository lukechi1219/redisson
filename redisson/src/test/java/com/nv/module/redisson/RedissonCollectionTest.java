package com.nv.module.redisson;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.nv.util.RedisUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RTimeSeries;
import org.redisson.api.RedissonClient;
import org.redisson.api.mapreduce.RMapReduce;

public class RedissonCollectionTest extends AbstractRedissonBaseTest {

	private final RedissonClient client = getClient();

	/**
	 *
	 */
	private void testRMap(RMap<String, Integer> map) {

		final String key1 = "key1";
		final int value1 = 1;

		HashMap<String, Integer> mapTemp = new HashMap<>();

		for (int i = 1; i <= 1000; i++) {
			mapTemp.put("key" + i, i);
		}

		// split 1000 entries into 10 batches
		map.putAll(mapTemp, 100);

		final Integer value1Int = map.get(key1);
		Assertions.assertEquals(value1, value1Int);

		// HSCAN entries with each batch size equals to 100
		final Set<Map.Entry<String, Integer>> entries1 = map.entrySet(100);

		for (Map.Entry<String, Integer> entry : entries1) {
			System.out.println(entry.getKey() + " = " + entry.getValue());
		}
		// size() -> HLEN
		Assertions.assertEquals(1000, entries1.size());

		final Set<Map.Entry<String, Integer>> entries = map.readAllEntrySet();
		Assertions.assertEquals(1000, entries.size());

		for (Map.Entry<String, Integer> entry : entries) {
			System.out.println(entry.getKey() + " = " + entry.getValue());
		}

		final Set<String> keys1 = map.keySet();
		final Set<String> keys = map.readAllKeySet();

		final Collection<Integer> values1 = map.values();
		final Collection<Integer> values = map.readAllValues();

		final Map<String, Integer> maps = map.readAllMap();

		final Integer value2 = map.addAndGet(key1, 1);
		Assertions.assertEquals(value1 + 1, value2);

		final boolean containsKey = map.containsKey(key1);
		Assertions.assertTrue(containsKey);

		final boolean containsValue = map.containsValue(value1 + 1);
		Assertions.assertTrue(containsValue);

		map.fastPutIfAbsent("key2", 2);

		map.fastPutIfExists("key2", 3);

		map.putIfAbsent("key4", 4);

		map.putIfExists("key5", 5);

		map.compute(key1, (key, value) -> {
			return value;
		});

		map.computeIfAbsent(key1, (key) -> {
			return 1;
		});

		map.computeIfPresent(key1, (key, value) -> {
			return value;
		});

		final RCountDownLatch countDownLatch = map.getCountDownLatch(key1);
		final long count = countDownLatch.getCount();
		System.out.println("count = " + count);

		map.getLock(key1);

		map.getFairLock(key1);

		map.getReadWriteLock(key1);

		map.getSemaphore(key1);

		map.getPermitExpirableSemaphore(key1);

		final int valueSize = map.valueSize(key1);
		System.out.println("valueSize = " + valueSize);

		map.remove(key1);

		map.replace(key1, 1);

		map.replace(key1, 1, 2);

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

		final RTimeSeries<Object, Object> timeSeries = client.getTimeSeries(key + ":1");

	}

	/**
	 *
	 */
	@Test
	public void testSortedSet() {

		final String key = RedisUtil.getKey("luke.test", "scoredSortedSet", "key");

		final RScoredSortedSet<String> scoredSortedSet = client.getScoredSortedSet(key + ":1");

		final Double score = scoredSortedSet.getScore("member");

		final Integer rank = scoredSortedSet.rank("member");

		final Collection<String> strings = scoredSortedSet.valueRange(1, 2);
	}

	/**
	 *
	 */
	@Test
	public void testList() {

		final String key = RedisUtil.getKey("luke.test", "timeSeries", "key");

		client.getTimeSeries(key + ":1");
	}

	/**
	 *
	 */
	@Test
	public void testQueue() {

		final String key = RedisUtil.getKey("luke.test", "timeSeries", "key");

		client.getTimeSeries(key + ":1");
	}

	/**
	 *
	 */
	@Test
	public void testStream() {

		final String key = RedisUtil.getKey("luke.test", "timeSeries", "key");

		client.getTimeSeries(key + ":1");
	}

	/**
	 *
	 */
	@Test
	public void testTimeSeries() throws InterruptedException {

		final String key = RedisUtil.getKey("luke.test", "timeSeries", "key");

		final RTimeSeries<String, String> timeSeries = client.getTimeSeries(key + ":1");

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
