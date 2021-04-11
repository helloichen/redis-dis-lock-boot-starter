package com.github.helloichen.redis.lock.redisson.config;

import org.redisson.config.Config;

/**
 * 增强 {@link Config}
 *
 * @author @zzyang
 * @since 2021/4/5 22:49
 */
public interface ConfigPostProcessor {

    /**
     * 增强配置 实现自定义配置
     *
     * @param config 属性配置 {@link org.redisson.config.Config}
     */
    void process(Config config);

}
