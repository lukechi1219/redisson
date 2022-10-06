package com.nv.util;

public class RedisUtil {

	public static final String REDIS_TAG = ":";
	private static final String SERVER_TYPE = "redisson";
	public static final String REDIS_PROJECT_ENV_PREFIX = "cps" + REDIS_TAG + SERVER_TYPE + REDIS_TAG;

	public static String getKey(String... values) {

		return REDIS_PROJECT_ENV_PREFIX + String.join(REDIS_TAG, values);
	}
}
