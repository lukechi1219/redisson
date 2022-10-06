package com.nv.module.redisson;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.nv.module.redis.RedisClientTest;
import org.redisson.Redisson;
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

				URL url = RedisClientTest.class.getClassLoader().getResource("properties/" + fileName);

				if (url == null) {
					throw new RuntimeException("File not found: " + fileName);
				}

				final File file = new File(url.getPath());

				//		System.out.println(file.getAbsolutePath());

				client = Redisson.create(Config.fromYAML(file));
			}

			return client;

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
