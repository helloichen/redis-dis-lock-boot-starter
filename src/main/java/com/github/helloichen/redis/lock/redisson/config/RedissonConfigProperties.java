package com.github.helloichen.redis.lock.redisson.config;

import com.github.helloichen.redis.lock.redisson.enmus.RedisDeployType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.github.helloichen.redis.lock.redisson.config.RedissonConfigProperties.PREFIX;

/**
 * redisson Config 参数配置属性
 *
 * @author iChen
 */
@ConfigurationProperties(prefix = PREFIX)
public class RedissonConfigProperties {

    public static final String PREFIX = "redisson.lock.config";

    /**
     * redis主机地址，ip：port，有多个用半角逗号分隔
     */
    private String address;

    /**
     * redis 部署方式
     */
    private RedisDeployType deployType;

    /**
     * redis连接密码
     */
    private String password;

    /**
     * 选取那个数据库
     */
    private Integer database;

    public String getPassword() {
        return password;
    }

    public RedissonConfigProperties setPassword(String password) {
        this.password = password;
        return this;
    }

    public Integer getDatabase() {
        return database;
    }

    public RedissonConfigProperties setDatabase(Integer database) {
        this.database = database;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public RedisDeployType getDeployType() {
        return deployType;
    }

    public void setDeployType(RedisDeployType deployType) {
        this.deployType = deployType;
    }

}
