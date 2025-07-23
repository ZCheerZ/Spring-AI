package com.zbj.ai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") // 允许所有域进行访问
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的HTTP方法
                .allowedHeaders("*");// 允许所有请求头
//                .allowCredentials(true) // 允许携带凭证
//                .maxAge(3600); // 预检请求的缓存时间，单位为秒
    }
}
