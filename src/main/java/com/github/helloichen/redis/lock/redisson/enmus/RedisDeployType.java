package com.github.helloichen.redis.lock.redisson.enmus;

/**
 * redis 部署方式
 *
 * @author iChen
 * @author @zzyang
 * @since 2021-04-05
 */
public enum RedisDeployType {

    /**
     * 单机
     */
    SINGLE,

    /**
     * 主从
     */
    MASTER_SLAVE,

    /**
     * 哨兵
     */
    SENTINEL,

    /**
     * cluster
     */
    CLUSTER,

    ;

}
