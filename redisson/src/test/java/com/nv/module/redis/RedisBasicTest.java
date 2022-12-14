package com.nv.module.redis;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import com.nv.module.redisson.AbstractRedissonBaseTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RBuckets;
import org.redisson.client.codec.StringCodec;

public class RedisBasicTest extends AbstractRedissonBaseTest {

    @AfterAll
    public static void afterAll() {
        AbstractRedissonBaseTest.afterAll();
    }

    @Test
    public void testEmptyKey() {

        final String key = "";
        final RBucket<String> bucket = getClient().getBucket(key);

        bucket.set("test empty key");
        bucket.expire(Duration.ofMinutes(10));
    }

    @Test
    public void testMGet() {

        final Account account = new Account(1L, "luke");

        final String key6 = "cps:unknown:account:42";
        getClient().<Account>getBucket(key6).set(account);

        final RBuckets buckets = getClient().getBuckets();

        final String key1 = "cps:redisson:luke.test:bucket:key:object1";
        final String key2 = "cps:unknown:player:42";
        final String key3 = "cps:test:atomicLongSync";
        final String key4 = "player:1000:wallet";

        /*
         */
        final String key5 = "cps:redisson:luke.test:binaryStream:key:1";

        try {
            final String key5Val1 = getClient().<String>getBucket(key5).get();
            System.out.println("key5Val1: " + key5Val1);

        } catch (Exception e) {
            //			e.printStackTrace();
            System.out.println("- - - -");
            System.out.println("catch exception");
            System.out.println(e.getMessage());
            System.out.println("- - - -");
        }

        final String key5Val2 = getClient().<String>getBucket(key5, StringCodec.INSTANCE).get();
        System.out.println("- - - -");
        System.out.println("key5Val2: " + key5Val2);

        /*
         */
        final Map<String, Object> map = buckets.get(key1, key2, key3, key4, key6);

        map.forEach((keyStr, value) -> {
            System.out.println("----");
            System.out.println(value.getClass().getSimpleName());
            System.out.println(keyStr + " : " + value);
        });
    }

    public static class Account {

        private long uid;
        private String name;

        //		public Account() {
        //			this(0L, "");
        //		}

        public Account(long uid, String name) {
            this.uid = uid;
            this.name = name;
        }

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Account [uid=" + uid + ", name=" + name + "]";
        }
    }

    @Test
    public void testUnlink() {

        final String key = "cps:luke:test:unlink:key";
        final RBucket<String> bucket = getClient().getBucket(key);

        bucket.set("test unlink key");

        final String data = bucket.get();
        System.out.println("data: " + data);

        final boolean unlink = bucket.unlink();
        System.out.println("unlink success: " + unlink);

        final String data2 = bucket.get();
        System.out.println("data2: " + data2);
    }

    @Test
    public void testTimestamp() {

        final String key = "cps:luke:test:timestamp:key";
        final RBucket<Timestamp> bucket = getClient().getBucket(key);

        bucket.set(new Timestamp(System.currentTimeMillis()));

        final Timestamp timestamp = bucket.get();
        System.out.println("timestamp: " + timestamp);

        System.out.println(timestamp.getTime());

        System.out.println(timestamp.getNanos());
    }

    @Test
    public void testExpire() throws ParseException {

        final String key = "cps:luke:test:expire:key";
        final RBucket<String> bucket = getClient().getBucket(key);

        bucket.expire(Instant.ofEpochMilli(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .parse("2020-12-31 23:59:59")
                        .getTime()));
    }
}
