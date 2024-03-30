package com.yupaoBackend.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "spring.redis")
public class RedissionConfig {
    private String port;
    private String host;
    @Bean
    public RedissonClient redissonClient(){
        //1、创建配置
        Config config = new Config();
        String redisUrl = String.format("redis://%s:%s",host,port);
        config.useSingleServer().setAddress(redisUrl).setDatabase(1);
        //2、创建实例
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
