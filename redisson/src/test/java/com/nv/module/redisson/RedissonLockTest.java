package com.nv.module.redisson;

import java.util.concurrent.TimeUnit;

import com.nv.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RedissonClient;

public class RedissonLockTest extends AbstractRedissonBaseTest {

	private final RedissonClient client = getClient();




	/**
	 *
	 */
	@Test
	public void testCountDownLatch() throws InterruptedException {

		final String key = RedisUtil.getKey("luke.test", "countDownLatch", "key");

		final RCountDownLatch countDownLatch = client.getCountDownLatch(key + ":1");

		final boolean result = countDownLatch.trySetCount(1);
		System.out.println("trySetCount: " + result);

		final long count = countDownLatch.getCount();
		System.out.println("count: " + count);

		countDownLatch.countDown();

		// block
		final boolean await = countDownLatch.await(60, TimeUnit.SECONDS);
		System.out.println("await: " + await);

		final boolean delete = countDownLatch.delete();
		System.out.println("delete: " + delete);

		/*
		 *
		 */
		//		testRExpirable(countDownLatch);
	}

	/*
			final RLock lock = client.getLock("lock");

		final RSemaphore semaphore = client.getSemaphore("semaphore");

	 */
}
