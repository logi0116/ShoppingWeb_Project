package com.busanit501.shoppingweb_project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /images/** 요청을 로컬 C:/upload_images 폴더에 매핑
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:///C:/upload_images/");
    }
}
