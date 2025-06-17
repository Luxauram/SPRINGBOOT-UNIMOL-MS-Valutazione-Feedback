package it.unimol.api_gateway.filter;

import it.unimol.api_gateway.util.JWTValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Autowired
    private JWTValidationService jwtValidationService;

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            System.out.println("=== JWT FILTER DEBUG ===");
            System.out.println("Request path: " + path);
            System.out.println("Request method: " + request.getMethod());
            System.out.println("========================");

            String authHeader = request.getHeaders().getFirst("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("Missing or invalid Authorization header for protected path: " + path);
                return handleUnauthorized(exchange);
            }

            try {
                String token = jwtValidationService.extractTokenFromHeader(authHeader);

                if (jwtValidationService.isTokenValid(token)) {
                    JWTValidationService.UserInfo userInfo = jwtValidationService.validateTokenAndGetUserInfo(token);

                    ServerHttpRequest modifiedRequest = request.mutate()
                            .header("X-User-ID", userInfo.userId())
                            .header("X-Username", userInfo.username())
                            .header("X-Roles", userInfo.role())
                            .build();

                    System.out.println("JWT validation successful for user: " + userInfo.username());
                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                } else {
                    System.out.println("Invalid JWT token for path: " + path);
                    return handleUnauthorized(exchange);
                }
            } catch (Exception e) {
                System.err.println("JWT validation error for path " + path + ": " + e.getMessage());
                e.printStackTrace();
                return handleUnauthorized(exchange);
            }
        };
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);

        response.getHeaders().add("Access-Control-Allow-Origin", "*");
        response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.getHeaders().add("Access-Control-Allow-Headers", "*");

        return response.setComplete();
    }

    public static class Config {}
}