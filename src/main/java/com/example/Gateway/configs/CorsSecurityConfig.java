package com.example.Gateway.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsSecurityConfig {
//    @Bean
//    public CorsWebFilter corsWebFilter() {
//        CorsConfiguration corsConfig = new CorsConfiguration();
//        corsConfig.addAllowedOrigin("http://localhost:5173"); // ✅ Allow frontend origin
//        corsConfig.addAllowedMethod("*"); // ✅ Allow all HTTP methods (GET, POST, etc.)
//        corsConfig.addAllowedHeader("*"); // ✅ Allow all headers
//        corsConfig.setAllowCredentials(true); // ✅ Allow credentials like cookies
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", corsConfig);
//
//        return new CorsWebFilter(source);
//    }

}
