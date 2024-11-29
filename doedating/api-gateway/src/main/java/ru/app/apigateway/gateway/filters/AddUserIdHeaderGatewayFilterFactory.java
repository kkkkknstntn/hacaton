package ru.app.apigateway.gateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AddUserIdHeaderGatewayFilterFactory implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            String header = exchange.getResponse().getHeaders().getFirst("X-User-Name");
            log.info("X-User-Name: {}", header);
            if (header != null) {
                exchange.getRequest().mutate()
                        .header("X-User-Name", header)
                        .build();
            }
            return chain.filter(exchange);
        }
}