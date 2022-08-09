package com.moyu.mall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/08/09
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter(){
        UrlBasedCorsConfigurationSource source1 = new UrlBasedCorsConfigurationSource();

        //CorsConfigurationSource source = new CorsConfigurationSource() {
        //    @Override
        //    public CorsConfiguration getCorsConfiguration(ServerWebExchange serverWebExchange) {
        //        return null;
        //    }
        //};

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.setAllowCredentials(true);

        source1.registerCorsConfiguration("/**", corsConfiguration);


        return new CorsWebFilter(source1);
    }
}
