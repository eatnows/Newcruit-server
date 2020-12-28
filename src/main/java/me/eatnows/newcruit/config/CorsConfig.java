package me.eatnows.newcruit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 요청에 대해서
                .allowedOrigins(System.getenv("CLIENT_URL")); // 허용할 오리진 복수는 ,로 추가
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 내 서버가 응답을 할 때 json을 자바스크립트에서 처리할 수 있게 할지를 설정하는 것
//        config.addAllowedOrigin(System.getenv("CLIENT_URL"));   // 해당 ip에 응답만 허용
        config.addAllowedOrigin(System.getenv("CLIENT_URL"));
        config.addAllowedHeader("*");   // * : 모든 header에 응답을 허용
        config.addAllowedMethod("*");   // * : 모든 http 메소드를 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // pattern에 들어오는 주소는 config 설정을 따르라
        return new CorsFilter(source);
    }
}
