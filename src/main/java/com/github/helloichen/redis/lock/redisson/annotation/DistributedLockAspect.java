package com.github.helloichen.redis.lock.redisson.annotation;

import com.github.helloichen.redis.lock.redisson.lock.DistributedLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 使用 aop 的方式处理 {@link IChenDistributedLock} 注解
 *
 * @author iChen
 * @author @zzyang
 * @since 2021-04-05
 */
@Aspect
public class DistributedLockAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedLockAspect.class);

    private final DistributedLock distributedLock;

    public DistributedLockAspect(DistributedLock distributedLock) {
        this.distributedLock = distributedLock;
    }

    @Pointcut("@annotation(com.github.helloichen.redis.lock.redisson.annotation.IChenDistributedLock)")
    public void distributedLock() {
    }

    @Around("@annotation(iChenDistributedLock)")
    public void around(ProceedingJoinPoint joinPoint, IChenDistributedLock iChenDistributedLock) throws Throwable {
        LOGGER.trace("[开始]执行RedisLock环绕通知,获取Redis分布式锁开始");
        // 获取锁名称
        String lockName = iChenDistributedLock.value();
        // 获取超时时间，默认十秒
        long expireTime = iChenDistributedLock.expireTime();
        long waitTime = iChenDistributedLock.waitTime();
        if (distributedLock.lock(lockName, expireTime, waitTime)) {
            try {
                LOGGER.trace("获取Redis分布式锁[成功]，加锁完成，开始执行业务逻辑...");
                joinPoint.proceed();
            } finally {
                distributedLock.release(lockName);
                LOGGER.trace("释放Redis分布式锁[成功]，解锁完成，结束业务逻辑...");
            }
        } else {
            LOGGER.trace("获取Redis分布式锁[失败]");
        }
        LOGGER.trace("[结束]执行RedisLock环绕通知");
    }

}
