package com.example.Gateway.configs;


import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Map;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Order(1)
    @Bean
   public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http){
        http
//                .cors(Customizer.withDefaults())
                .authorizeExchange(exchanges ->
                        exchanges
                                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .pathMatchers("/oauth2/**","/public/products/**","/login","/auth/**","/default-ui.css","/.well-known/**").permitAll()
                                .pathMatchers("/admin/**").hasRole("ADMIN")
                                .anyExchange().authenticated()
                ).oauth2ResourceServer(oauth2 -> oauth2.jwt(
                        jwt -> jwt.jwtDecoder(jwtDecoder())
                ))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
        ;
        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        String lbIssuerUri = "lb://authservice";
        WebClient webClient = webClientBuilder().build();

        String resolvedIssuerUri = webClient
                .get()
                .uri(lbIssuerUri + "/.well-known/openid-configuration")
                .retrieve()
                .bodyToMono(Map.class)
                .map(config -> config.get("issuer").toString())
                .block();

        assert resolvedIssuerUri != null;
        return ReactiveJwtDecoders.fromIssuerLocation(resolvedIssuerUri);
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
//        configuration.setAllowedHeaders(Arrays.asList("*","Authorization", "Content-Type"));
////        configuration.addAllowedHeader("*");
////        configuration.addAllowedMethod("*");
////        configuration.addAllowedOrigin("http://localhost:5173");
//        configuration.setAllowCredentials(true);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:5173","http://localhost:8080","null","https://taraksuthar1999.github.io"));
        corsConfig.addAllowedMethod("*"); // ✅ Allow all HTTP methods (GET, POST, etc.)
//        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        corsConfig.setAllowedHeaders(Arrays.asList("*","Authorization", "Content-Type"));
//        corsConfig.addAllowedHeader("*"); // ✅ Allow all headers
        corsConfig.setAllowCredentials(true); // ✅ Allow credentials like cookies
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
