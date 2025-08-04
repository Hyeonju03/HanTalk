package com.example.hantalk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 로컬 C:/aaa/HanTalk/hantalk/ 폴더를 /upload/** URL로 매핑
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:///C:/aaa/HanTalk/hantalk/upload/");
    }

}
