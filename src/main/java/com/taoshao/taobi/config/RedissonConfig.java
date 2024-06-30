package com.taoshao.taobi.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author taoshao
 * @Date 2024/6/26
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

    private String host;
    private String post;

    @Bean
    public RedissonClient RedissonClient(){
        Config config = new Config();
        config.useSingleServer()
                .setDatabase(2)
                .setAddress(String.format("redis://%s:%s",host,post));
        RedissonClient redisson = Redisson.create();
        return redisson;

    }
}
