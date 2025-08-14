package com.example.hantalk.config;

import org.springframework.beans.factory.annotation.Value;
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
                .excludePathPatterns("/css/**", "/js/**", "/images/**", "/user/test", "/user/findIDPW", "/",
                        "/user/isIdAvail", "/user/isEmailAvail",
                        "/user/findID", "/user/findPW");
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**", "/**/admin/**", "/**/admin")
                .excludePathPatterns("/css/**", "/js/**", "/images/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 외부 폴더를 /images/** 경로로 노출

        // 프로필 이미지 경로
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/images/");

        // 프레임 이미지 경로
        registry.addResourceHandler("/uploads/frames/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/frames/");

        // 자료실 첨부자료 경로
        registry.addResourceHandler("/uploads/ResourceFile/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/ResourceFile/");

        // 게시판 첨부자료 경로
        registry.addResourceHandler("/uploads/fileUpload/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/fileUpload/");

        // 학습 영상 경로
        String uploadPath = System.getProperty("user.dir") + "/uploads/videos/";
        registry.addResourceHandler("/videos/**")
                .addResourceLocations("file:" + uploadPath);

        // 프레임 이미지 경로
        registry.addResourceHandler("/frames/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/frames/");

        registry.addResourceHandler("/image/**")
                .addResourceLocations("classpath:/static/images/");

    }
}