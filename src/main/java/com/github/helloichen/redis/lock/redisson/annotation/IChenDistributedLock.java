package com.github.helloichen.redis.lock.redisson.annotation;

import java.lang.annotation.*;

/**
 * iChen redis 分布式锁启用注解，注解标记的方法开启锁同步
 *
 * @author iChen
 * @since 2021-04-05
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface IChenDistributedLock {

    /**
     * 分布式锁名称
     */
    String value() default "iChen-RDS-DIS-LOCK";

    /**
     * 锁占据的时间 默认10000，单位毫秒
     */
    long expireTime() default 10000;

    /**
     * 获取锁等待时间 默认1000，单位毫秒
     */
    long waitTime() default 1000;
}
