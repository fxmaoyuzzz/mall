package com.moyu.mall.product.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/08/23
 */
@Configuration
@EnableTransactionManagement
@MapperScan("com.moyu.mall.product.dao")
public class MyBatisConfig {


    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();

        paginationInterceptor.setOverflow(true);
        paginationInterceptor.setLimit(1000);

        return paginationInterceptor;
    }
}
