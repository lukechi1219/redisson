package com.nv.module.redisson;

import com.nv.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.redisson.api.RTimeSeries;
import org.redisson.api.RedissonClient;

public class RedissonCollectionTest extends AbstractRedissonBaseTest {

	private final RedissonClient client = getClient();




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
		 *
		 */
		testRExpirable(timeSeries);

		System.out.println("-- end --");
	}
}
