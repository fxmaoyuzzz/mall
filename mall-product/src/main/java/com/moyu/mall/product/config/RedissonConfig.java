package com.moyu.mall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/11/12
 */
@Configuration
public class RedissonConfig {

    /**
     * 所有对 redisson 的操作都通过RedissonClient对象
     * @return
     */
    @Bean(destroyMethod = "shutdown")
    RedissonClient redisson(){
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://47.117.142.234:6379");

        return Redisson.create(config);
    }
}
