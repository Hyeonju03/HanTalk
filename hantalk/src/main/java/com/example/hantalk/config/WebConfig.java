package com.example.hantalk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 외부 폴더를 /images/** 경로로 노출
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/images/");

        // 프레임 이미지 경로
        registry.addResourceHandler("/frames/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/frames/");

        registry.addResourceHandler("/image/**")
                .addResourceLocations("classpath:/static/images/");
    }
}