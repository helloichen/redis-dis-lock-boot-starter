package com.github.helloichen.redis.lock.redisson.config.factory;

import com.github.helloichen.redis.lock.redisson.config.RedissonConfigProperties;
import com.github.helloichen.redis.lock.redisson.constant.IChenConstant;
import com.github.helloichen.redis.lock.redisson.enmus.RedisDeployType;
import org.apache.commons.lang3.StringUtils;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据配置的Redis部署方式，创建对应的 Config
 *
 * @author @zzyang
 * @since 2021/4/5 22:45
 */
public class RedissonConfigFactoryRouter implements RedissonConfigFactory {

    @Override
    public Config createRedissonConfig(RedissonConfigProperties redissonConfigProperties) {
        RedisDeployType deployType = redissonConfigProperties.getDeployType();

        Config config = null;

        switch (deployType) {
            case SINGLE:
                config = SingleRedissonConfigFactory.getInstance().createRedissonConfig(redissonConfigProperties);
                break;
            case MASTER_SLAVE:
                config = MasterSlaveRedissonConfigFactory.getInstance().createRedissonConfig(redissonConfigProperties);
                break;
            case SENTINEL:
                config = SentinelRedissonConfigFactory.getInstance().createRedissonConfig(redissonConfigProperties);
                break;
            case CLUSTER:
                config = ClusterRedissonConfigFactory.getInstance().createRedissonConfig(redissonConfigProperties);
                break;
            default:
        }

        if (config == null) {
            throw new IllegalArgumentException("创建Config失败,deployType=" + deployType);
        }

        return config;
    }

    /**
     * @author iChen
     * @since 2021-04-05
     */
    private static class ClusterRedissonConfigFactory implements RedissonConfigFactory {

        private static final Logger LOGGER = LoggerFactory.getLogger(ClusterRedissonConfigFactory.class);

        private ClusterRedissonConfigFactory() {
            //不可反射创建多个实例
            if (ClusterRedissonConfigFactoryHolder.FACTORY != null) {
                throw new RuntimeException("ClusterRedissonConfigFactory已创建，不可多次创建实例");
            }
        }

        public static ClusterRedissonConfigFactory getInstance() {
            return ClusterRedissonConfigFactoryHolder.FACTORY;
        }

        private static class ClusterRedissonConfigFactoryHolder {
            static final ClusterRedissonConfigFactory FACTORY = new ClusterRedissonConfigFactory();
        }

        @Override
        public Config createRedissonConfig(RedissonConfigProperties redissonConfigProperties) {
            Config config = new Config();
            try {
                String address = redissonConfigProperties.getAddress();
                String password = redissonConfigProperties.getPassword();
                String[] addrTokens = address.split(",");
                //设置cluster节点的服务IP和端口
                for (String addrToken : addrTokens) {
                    config.useClusterServers()
                            .addNodeAddress(IChenConstant.REDIS_CONNECTION_PREFIX + addrToken);
                }
                config.useClusterServers().setPassword(password);
                LOGGER.info("初始化[cluster]方式Config,redisAddress:" + address);
            } catch (Exception e) {
                LOGGER.error("cluster Redisson init error", e);
            }
            return config;
        }

    }

    /**
     * @author iChen
     * @since 2021-04-05
     */
    private static class MasterSlaveRedissonConfigFactory implements RedissonConfigFactory {

        private static final Logger LOGGER = LoggerFactory.getLogger(ClusterRedissonConfigFactory.class);

        private MasterSlaveRedissonConfigFactory() {
            //不可反射创建多个实例
            if (MasterSlaveRedissonConfigFactoryHolder.FACTORY != null) {
                throw new RuntimeException("MasterSlaveRedissonConfigFactory已创建，不可多次创建实例");
            }
        }

        public static MasterSlaveRedissonConfigFactory getInstance() {
            return MasterSlaveRedissonConfigFactoryHolder.FACTORY;
        }

        private static class MasterSlaveRedissonConfigFactoryHolder {
            static final MasterSlaveRedissonConfigFactory FACTORY = new MasterSlaveRedissonConfigFactory();
        }

        @Override
        public Config createRedissonConfig(RedissonConfigProperties redissonConfigProperties) {
            Config config = new Config();
            try {
                String address = redissonConfigProperties.getAddress();
                String[] addrTokens = address.split(",");
                String masterNodeAddr = addrTokens[0];
                //设置主节点ip
                config.useMasterSlaveServers().setMasterAddress(masterNodeAddr);

                String password = redissonConfigProperties.getPassword();
                if (StringUtils.isNotBlank(password)) {
                    config.useMasterSlaveServers().setPassword(password);
                }

                Integer database = redissonConfigProperties.getDatabase();
                if (database != null) {
                    config.useMasterSlaveServers().setDatabase(database);
                }

                //设置从节点，移除第一个节点，默认第一个为主节点
                List<String> slaveList = new ArrayList<>();
                for (String addrToken : addrTokens) {
                    slaveList.add(IChenConstant.REDIS_CONNECTION_PREFIX + addrToken);
                }
                slaveList.remove(0);

                config.useMasterSlaveServers().addSlaveAddress(slaveList.toArray(new String[0]));
                LOGGER.info("初始化[MASTERSLAVE]方式Config,redisAddress:" + address);
            } catch (Exception e) {
                LOGGER.error("MASTERSLAVE Redisson init error", e);
            }
            return config;
        }

    }

    /**
     * @author iChen
     * @since 2021-04-05
     */
    private static class SentinelRedissonConfigFactory implements RedissonConfigFactory {

        private static final Logger LOGGER = LoggerFactory.getLogger(SentinelRedissonConfigFactory.class);

        private SentinelRedissonConfigFactory() {
            //不可反射创建多个实例
            if (SentinelRedissonConfigFactoryHolder.FACTORY != null) {
                throw new RuntimeException("SentinelRedissonConfigFactory已创建，不可多次创建实例");
            }
        }

        public static SentinelRedissonConfigFactory getInstance() {
            return SentinelRedissonConfigFactoryHolder.FACTORY;
        }

        private static class SentinelRedissonConfigFactoryHolder {
            static final SentinelRedissonConfigFactory FACTORY = new SentinelRedissonConfigFactory();
        }

        @Override
        public Config createRedissonConfig(RedissonConfigProperties redissonConfigProperties) {
            Config config = new Config();
            try {
                String address = redissonConfigProperties.getAddress();
                String password = redissonConfigProperties.getPassword();
                String[] addrTokens = address.split(",");
                String sentinelAliasName = addrTokens[0];
                // 设置redis配置文件sentinel.conf配置的sentinel别名
                config.useSentinelServers()
                        .setMasterName(sentinelAliasName);
                // 设置sentinel节点的服务IP和端口
                for (int i = 1; i < addrTokens.length; i++) {
                    config.useSentinelServers().addSentinelAddress(IChenConstant.REDIS_CONNECTION_PREFIX + addrTokens[i]);
                }

                Integer database = redissonConfigProperties.getDatabase();
                if (database != null) {
                    config.useSentinelServers().setDatabase(database);
                }

                if (StringUtils.isNotBlank(password)) {
                    config.useSentinelServers().setPassword(password);
                }
                LOGGER.info("初始化[sentinel]方式Config,redisAddress:" + address);
            } catch (Exception e) {
                LOGGER.error("sentinel Redisson init error", e);
            }
            return config;
        }
    }

    /**
     * @author iChen
     * @since 2021-04-05
     */
    private static class SingleRedissonConfigFactory implements RedissonConfigFactory {

        private static final Logger LOGGER = LoggerFactory.getLogger(SingleRedissonConfigFactory.class);

        private SingleRedissonConfigFactory() {
            //不可反射创建多个实例
            if (SingleRedissonConfigFactoryHolder.FACTORY != null) {
                throw new RuntimeException("SingleRedissonConfigFactory已创建，不可多次创建实例");
            }
        }

        public static SingleRedissonConfigFactory getInstance() {
            return SingleRedissonConfigFactoryHolder.FACTORY;
        }

        private static class SingleRedissonConfigFactoryHolder {
            static final SingleRedissonConfigFactory FACTORY = new SingleRedissonConfigFactory();
        }

        @Override
        public Config createRedissonConfig(RedissonConfigProperties redissonConfigProperties) {
            Config config = new Config();
            try {
                String address = redissonConfigProperties.getAddress();
                String password = redissonConfigProperties.getPassword();
                String redisAddr = IChenConstant.REDIS_CONNECTION_PREFIX + address;
                config.useSingleServer().setAddress(redisAddr);

                Integer database = redissonConfigProperties.getDatabase();
                if (database != null) {
                    config.useSingleServer().setDatabase(database);
                }

                if (StringUtils.isNotBlank(password)) {
                    config.useSingleServer().setPassword(password);
                }
                LOGGER.info("初始化[standalone]方式Config,redisAddress:" + address);
            } catch (Exception e) {
                LOGGER.error("standalone Redisson init error", e);
            }
            return config;
        }
    }
}
