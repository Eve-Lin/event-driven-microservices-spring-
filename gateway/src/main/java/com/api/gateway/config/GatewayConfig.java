package com.api.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth_service", r -> r.path("/auth/**")
                        .filters(f -> f.addRequestHeader("X-Gateway", "api-gateway"))
                        .uri("http://localhost:8020"))
                .route("order_command", r -> r.path("/orders/**")
                        .filters(f -> f.addRequestHeader("X-Gateway", "api-gateway"))
                        .uri("http://localhost:8080")) // order command service
                .route("order_query", r -> r.path("/orders/read/**")
                        .uri("http://localhost:8000")) // order query service
                .route("notification", r -> r.path("/notifications/**")
                        .uri("http://localhost:8010")) // notification service
                .build();
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(5, 10); // 5 requests/sec, burst 10
    }
}