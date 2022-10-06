package com.nv.module.redisson;

import com.nv.util.RedisUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMap;

class RedissonMapTest extends AbstractRedissonBaseTest {

	//	@BeforeEach
	//	void setUp() {
	//	}
	//
	//	@AfterEach
	//	void tearDown() {
	//	}

	@AfterAll
	public static void afterAll() {
		AbstractRedissonBaseTest.afterAll();
	}

	private static final String key = RedisUtil.getKey("luke.test", "map", "key");

	@Test
	public void testAllMethods() {

		final RMap<String, String> map = getClient().getMap(key);

		if (map.isEmpty()) {

			for (int i = 0; i < 10; i++) {
				map.put("key" + i, "value" + i);
			}
		}

		//		final Method[] methods = map.getClass().getMethods();
		//
		//		for (final Method method : methods) {
		//
		//			final boolean isNotDeprecated = method.getAnnotation(Deprecated.class) == null;
		//
		//			if (!Modifier.isPrivate(method.getModifiers())
		//				&& !Modifier.isStatic(method.getModifiers())
		//				&& isNotDeprecated
		//				&& !method.getName().endsWith("Async")) {
		//
		//				System.out.println(method.getName());
		//			}
		//		}

		/*
		 * ConcurrentMap
		 */

		/*
		 * RExpirable
		 */
		testRExpirable(map);

		System.out.println("-- end --");
	}

	/*

remove
get
put
values
replace
entrySet
putAll
putIfAbsent
containsKey
containsValue
keySet
addAndGet
fastRemove
getAll
getSemaphore
getLock
getFairLock
getReadWriteLock
getCountDownLatch
getPermitExpirableSemaphore
loadAll
fastPutIfExists
mapReduce
valueSize
fastReplace
putIfExists
fastPutIfAbsent
readAllEntrySet
randomEntries
fastPut
readAllMap
randomKeys
readAllKeySet
readAllValues

replaceAll
merge
compute
forEach
computeIfAbsent
getOrDefault
computeIfPresent
equals
hashCode
clear
isEmpty
size
getName
delete
copy
rename
unlink
move
addListener
removeListener
getCodec
dump
touch
sizeInMemory
getIdleTime
restoreAndReplace
renamenx
isExists
restore
migrate

destroy

	 */
}
