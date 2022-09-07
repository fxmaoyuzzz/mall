package com.moyu.mall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ElasticSearch 配置类
 * 给容器中注入 RestHighLevelClient
 *
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/09/07
 */

@Configuration
public class MallElasticSearchConfig {

    @Bean
    public RestHighLevelClient esRestClient() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("47.117.142.234", 9200, "http")));
        return client;
    }
}
