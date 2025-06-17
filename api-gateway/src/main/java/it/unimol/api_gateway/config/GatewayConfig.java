package it.unimol.api_gateway.config;

import it.unimol.api_gateway.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class GatewayConfig {

    @Autowired
    private Environment environment;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        String userServiceUri;
        String assessmentServiceUri;

        if (isDockerProfile()) {
            userServiceUri = "http://unimol-microservice-user-role:8081";
            assessmentServiceUri = "http://unimol-microservice-assessment-feedback:8082";
        } else {
            userServiceUri = "http://localhost:8081";
            assessmentServiceUri = "http://localhost:8082";
        }

        return builder.routes()
                // ============== ROUTE PUBBLICHE (SENZA JWT) ==============

                // Auth endpoints (login, refresh, init) - PUBBLICI
                .route("auth-public", r -> r.path("/api/v1/auth/login", "/api/v1/auth/refresh-token", "/api/v1/users/superadmin/init")
                        .filters(f -> f.stripPrefix(0))
                        .uri(userServiceUri))

                // Health-Check endpoints - PUBBLICI
                .route("user-service-health", r -> r.path("/api/user-service/actuator/**")
                        .filters(f -> f.rewritePath("/api/user-service/actuator/(?<segment>.*)", "/actuator/${segment}"))
                        .uri(userServiceUri))

                .route("assessment-service-health", r -> r.path("/api/assessment-service/actuator/**")
                        .filters(f -> f.rewritePath("/api/assessment-service/actuator/(?<segment>.*)", "/actuator/${segment}"))
                        .uri(assessmentServiceUri))

                // Gateway actuator - PUBBLICO
                .route("gateway-actuator", r -> r.path("/actuator/**")
                        .filters(f -> f.stripPrefix(0))
                        .uri("http://localhost:8080"))

                // Swagger/OpenAPI endpoints - PUBBLICI
                .route("swagger-ui", r -> r.path("/swagger-ui/**", "/webjars/**")
                        .filters(f -> f.stripPrefix(0))
                        .uri("forward:/"))

                .route("openapi-docs", r -> r.path("/v3/api-docs/**")
                        .filters(f -> f.stripPrefix(0))
                        .uri("forward:/"))

                // ============== ROUTE PROTETTE (CON JWT) ==============

                // User-Role Service - PROTETTE
                .route("user-service-auth-protected", r -> r.path("/api/v1/auth/**")
                        .and().not(p -> p.path("/api/v1/auth/login", "/api/v1/auth/refresh-token"))
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(0))
                        .uri(userServiceUri))

                .route("user-service-users", r -> r.path("/api/v1/users/**")
                        .and().not(p -> p.path("/api/v1/users/superadmin/init"))
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(0))
                        .uri(userServiceUri))

                .route("user-service-roles", r -> r.path("/api/v1/roles/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(0))
                        .uri(userServiceUri))

                // Assessment-Feedback Service - PROTETTE
                .route("assessment-service-assessments", r -> r.path("/api/v1/assessments/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(0))
                        .uri(assessmentServiceUri))

                .route("assessment-service-feedback", r -> r.path("/api/v1/feedback/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(0))
                        .uri(assessmentServiceUri))

                .route("assessment-service-teacher-surveys", r -> r.path("/api/v1/teacher-surveys/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(0))
                        .uri(assessmentServiceUri))

                .route("assessment-service-surveys", r -> r.path("/api/v1/surveys/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(0))
                        .uri(assessmentServiceUri))

                .build();
    }

    private boolean isDockerProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if ("docker".equals(profile)) {
                return true;
            }
        }
        return false;
    }
}