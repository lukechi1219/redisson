package com.nv.module.redisson;

import com.nv.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.redisson.api.RScheduledExecutorService;
import org.redisson.api.RedissonClient;

public class RedissonServiceTest extends AbstractRedissonBaseTest {

	private final RedissonClient client = getClient();



	/**
	 *
	 */
	@Test
	public void testExecutorService() {

		final String key = RedisUtil.getKey("luke.test", "executorService", "key");

		final RScheduledExecutorService executorService = client.getExecutorService(key + ":1");

		final int taskCount = executorService.getTaskCount();
		System.out.println("taskCount: " + taskCount);

	}

	/*
			final RRemoteService remoteService = client.getRemoteService();
	 */
}
