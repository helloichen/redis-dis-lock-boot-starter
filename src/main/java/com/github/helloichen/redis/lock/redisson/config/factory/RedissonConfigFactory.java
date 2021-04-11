package com.github.helloichen.redis.lock.redisson.config.factory;

import com.github.helloichen.redis.lock.redisson.config.RedissonConfigProperties;
import org.redisson.config.Config;

/**
 * 获取redisson Config 配置工厂
 *
 * @author @zzyang
 * @see org.redisson.config.Config
 * @since 2021/4/5 22:32
 */
public interface RedissonConfigFactory {

    /**
     * 创建 Config
     *
     * @param redissonConfigProperties 属性配置
     * @return redisson 属性配置 {@link org.redisson.config.Config}
     */
    Config createRedissonConfig(RedissonConfigProperties redissonConfigProperties);

}
