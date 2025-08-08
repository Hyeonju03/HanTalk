package com.example.hantalk.config;

import com.example.hantalk.config.interceptor.AdminInterceptor;
import com.example.hantalk.config.interceptor.UserInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    // url 정리되면 주석 해제
    private final UserInterceptor userInterceptor;
    private final AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**", "/images/**","/user/test","/user/findIDPW", "/");
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**","/**/admin/**", "/**/admin")
                .excludePathPatterns("/css/**", "/js/**", "/images/**");
    }

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