package com.example.Gateway.filters;

import org.apache.logging.log4j.util.Strings;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class UserHeaderFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .filter(c -> c.getAuthentication() != null)
                .flatMap(c ->{
                    JwtAuthenticationToken jwt = (JwtAuthenticationToken) c.getAuthentication();
                    Long userId = jwt.getToken().getClaim("id");
                    if(userId == null){
                        return Mono.error(
                                new AccessDeniedException("Invalid token. User not present in token.")
                        );
                    }
                    ServerHttpRequest request = exchange.getRequest().mutate()
                            .header("x-user", userId.toString()).build();
                    return chain.filter(exchange.mutate().request(request).build());
                })
                .switchIfEmpty(chain.filter(exchange));
    }
}
