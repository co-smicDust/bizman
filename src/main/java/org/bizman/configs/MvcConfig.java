package org.bizman.configs;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableJpaAuditing  // 날짜, 시간, 수정자 이벤트
public class MvcConfig implements WebMvcConfigurer {
    @Bean
    public MessageSource messageSource(){
        ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
        ms.setDefaultEncoding("UTF-8");
        ms.setBasenames("messages.commons", "messages.validations", "messages.errors");

        return ms;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // URL 패턴
        CorsConfiguration config = new CorsConfiguration();     // 설정 클래스

        // *은 나중에 도메인으로 대체하는 것이 좋음
        config.addAllowedOrigin("*");   // 도메인이 여러개일 경우 여러개 생성
        config.addAllowedHeader("*");   // 응답 전부 허용
        config.addAllowedMethod("*");   // 요청 방식 제한 X

        source.registerCorsConfiguration("/api/**", config);    // api

        return new CorsFilter(source);
    }
}
