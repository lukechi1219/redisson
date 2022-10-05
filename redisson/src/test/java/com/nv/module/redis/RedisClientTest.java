package com.nv.module.redis;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
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

	@Test
	public void test() {

//		System.out.println("test");

		final Config config = client.getConfig();

		final Codec codec = config.getCodec();

		System.out.println(codec);
	}
}
