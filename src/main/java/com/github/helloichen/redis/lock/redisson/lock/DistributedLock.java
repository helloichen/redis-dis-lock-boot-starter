package com.github.helloichen.redis.lock.redisson.lock;

/**
 * 分布式锁
 *
 * @author @zzyang
 * @since 2021/4/5 23:32
 */
public interface DistributedLock {

    /**
     * 获取锁
     *
     * @param lockName   锁名称
     * @param expireTime 过期时间 单位：千分之一秒（毫秒）
     * @param waitTime   获取锁等待时间 单位毫秒
     * @return true or false  true 代表成功 false代表失败
     */
    boolean lock(String lockName, long expireTime, long waitTime);

    /**
     * 释放锁
     *
     * @param lockName 锁名称
     */
    void release(String lockName);

}
