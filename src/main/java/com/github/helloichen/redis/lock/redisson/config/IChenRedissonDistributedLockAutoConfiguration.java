package com.github.helloichen.redis.lock.redisson.config;

import com.github.helloichen.redis.lock.redisson.annotation.DistributedLockAspect;
import com.github.helloichen.redis.lock.redisson.config.factory.RedissonConfigFactory;
import com.github.helloichen.redis.lock.redisson.config.factory.RedissonConfigFactoryRouter;
import com.github.helloichen.redis.lock.redisson.lock.DistributedLock;
import com.github.helloichen.redis.lock.redisson.lock.IChenRedissonLock;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Redission 分布式锁自动配置类
 *
 * @author iChen
 * @author @zzyang
 * @since 2021-04-05
 */
@Configuration
@ConditionalOnClass(Redisson.class)
@ConditionalOnProperty(prefix = RedissonConfigProperties.PREFIX, value = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(RedissonConfigProperties.class)
public class IChenRedissonDistributedLockAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(IChenRedissonDistributedLockAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public RedissonConfigFactory redissonConfigFactory() {
        return new RedissonConfigFactoryRouter();
    }

    @Autowired(required = false)
    private List<ConfigPostProcessor> configPostProcessors;

    @Bean
    @ConditionalOnMissingBean
    public DistributedLock distributedLock(RedissonConfigFactory redissonConfigFactory, RedissonConfigProperties redissonProperties) {
        final Config config = redissonConfigFactory.createRedissonConfig(redissonProperties);
        if (!CollectionUtils.isEmpty(configPostProcessors)) {
            configPostProcessors.forEach(configPostProcessor -> configPostProcessor.process(config));
        }
        return new IChenRedissonLock((Redisson) Redisson.create(config));
    }

    @Bean
    public DistributedLockAspect distributedLockAspect(DistributedLock distributedLock) {
        DistributedLockAspect distributedLockAspect = new DistributedLockAspect(distributedLock);
        LOGGER.info("[RedissonLock]组装完毕");
        return distributedLockAspect;
    }

}
