package com.example.selfduizoo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/img/profile/**")
                .addResourceLocations("file:/C:/springboot/selfduizoo/src/main/resources/static/img/profile/");
        registry
                .addResourceHandler("/img/flag/**")
                .addResourceLocations("file:/C:/springboot/selfduizoo/src/main/resources/static/img/flag/");
    }
}
