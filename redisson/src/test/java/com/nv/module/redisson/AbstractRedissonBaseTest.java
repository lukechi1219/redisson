package com.nv.module.redisson;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;

import com.nv.module.redis.RedisClientTest;
import org.redisson.Redisson;
import org.redisson.api.RExpirable;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public abstract class AbstractRedissonBaseTest {

	private static final String fileName = "redis.yaml";
	private static RedissonClient client = null;

	/**
	 *
	 */
	public static void afterAll() {

		if (client != null) {
			client.shutdown();
		}
	}

	/**
	 *
	 */
	public static RedissonClient getClient() throws RuntimeException {
		try {
			if (client == null) {

				final Config config;

				final URL url = RedisClientTest.class.getClassLoader().getResource("properties/" + fileName);

				if (url == null) {
					//	throw new RuntimeException("File not found: " + fileName);

					config = new Config();

					config.useSingleServer()
						.setAddress("redis://127.0.0.1:6379")
						.setPassword("luke1217");

				} else {
					final File file = new File(url.getPath());
					//	System.out.println(file.getAbsolutePath());
					config = Config.fromYAML(file);
				}

				client = Redisson.create(config);
			}

			return client;

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected void testRExpirable(RExpirable expirable) {

		final Instant oneMinuteLater = Instant.ofEpochSecond(Instant.now().getEpochSecond() + 60);
		//		System.out.println(Instant.now());
		//		System.out.println(oneMinuteLater);

		final Duration oneMinuteDuration = Duration.ofSeconds(60L);

		final boolean result = expirable.clearExpire();
		System.out.println("clearExpire: " + result);

		final boolean expire1 = expirable.expire(oneMinuteLater);
		System.out.println("expire1: " + expire1);

		// XX
		//		expirable.expireIfSet(oneMinuteLater);
		// NX
		//		expirable.expireIfNotSet(oneMinuteLater);
		// GT
		//		final boolean expireIfGreater = expirable.expireIfGreater(oneMinuteLater.minusSeconds(10L));
		//		System.out.println(expireIfGreater);
		// LT
		//		expirable.expireIfLess(oneMinuteLater.plusSeconds(10L));

		final boolean expire2 = expirable.expire(oneMinuteDuration);
		System.out.println("expire2: " + expire2);

		// XX
		//		expirable.expireIfSet(oneMinuteDuration);
		// NX
		//		expirable.expireIfNotSet(oneMinuteDuration);
		// GT
		//		final boolean expireIfGreater = expirable.expireIfGreater(oneMinuteDuration.minusSeconds(10L));
		//		System.out.println(expireIfGreater);
		// LT
		//		expirable.expireIfLess(oneMinuteDuration.plusSeconds(10L));

		// PEXPIRETIME, Available since: 7.0.0
		//				final long expireTime = expirable.getExpireTime();
		//				System.out.println(new Timestamp(expireTime));

		System.out.println("----");

		final long remainTimeToLive = expirable.remainTimeToLive();
		System.out.println("remainTimeToLive: " + remainTimeToLive);
	}
}
