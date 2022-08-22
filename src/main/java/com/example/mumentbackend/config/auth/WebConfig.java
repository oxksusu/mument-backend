package com.example.mumentbackend.config.auth;

import com.example.mumentbackend.config.auth.jwt.JwtProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /* 프론트 서버와의 CORS 이슈를 해결하기 위한 메소드입니다.*/
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedOriginPatterns("*")
                .allowedHeaders("*")
                .exposedHeaders(JwtProperties.HEADER_STRING)
                .allowedMethods("*")
                .allowCredentials(true);
    }
}
