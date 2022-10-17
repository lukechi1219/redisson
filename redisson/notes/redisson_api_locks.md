# Redisson API

.

## RLock

.

## RSemaphore

.

## CountDownLatch

final RCountDownLatch countDownLatch = client.getCountDownLatch(key + ":1");

countDownLatch.countDown();

// block
final boolean await = countDownLatch.await(60, TimeUnit.SECONDS);


.

