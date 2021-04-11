package com.github.helloichen.redis.lock.redisson.lock;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author iChen
 * @since 2021-04-05
 */
public class IChenRedissonLock implements DistributedLock {

    private static final Logger LOGGER = LoggerFactory.getLogger(IChenRedissonLock.class);

    private final Redisson redisson;

    public IChenRedissonLock(Redisson redisson) {
        this.redisson = redisson;
    }

    /**
     * 加锁操作
     */
    @Override
    public boolean lock(String lockName, long expireTime, long waitTime) {
        RLock rLock = redisson.getLock(lockName);
        boolean getLock;
        try {
            getLock = rLock.tryLock(waitTime, expireTime, TimeUnit.MILLISECONDS);
            if (getLock) {
                LOGGER.info("获取Redisson分布式锁[成功],lockName={}", lockName);
            } else {
                LOGGER.info("获取Redisson分布式锁[失败],lockName={}", lockName);
            }
        } catch (InterruptedException e) {
            LOGGER.error("获取Redisson分布式锁[异常]，lockName=" + lockName, e);
            return false;
        }
        return getLock;
    }

    /**
     * 解锁
     * todo 解锁是否需要判断是凑当前线程获取到的锁
     */
    @Override
    public void release(String lockName) {
        redisson.getLock(lockName).unlock();
    }

}
