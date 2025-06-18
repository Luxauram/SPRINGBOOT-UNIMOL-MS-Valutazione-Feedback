package it.unimol.api_gateway.config.swagger;


import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class SwaggerAggregatorConfig {

    @Bean
    @Primary
    public SwaggerUiConfigProperties swaggerUiConfig() {
        SwaggerUiConfigProperties config = new SwaggerUiConfigProperties();
        Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> urls = new HashSet<>();

        // API Gateway
        urls.add(new AbstractSwaggerUiConfigProperties.SwaggerUrl(
                "API Gateway", "/v3/api-docs", "api-gateway"));

        // MS User Role
        urls.add(new AbstractSwaggerUiConfigProperties.SwaggerUrl(
                "User & Role Service", "/user-service/v3/api-docs", "microservice-user-role"));

        // MS Assessment Feedback
        urls.add(new AbstractSwaggerUiConfigProperties.SwaggerUrl(
                "Assessment & Feedback Service", "/assessment-service/v3/api-docs", "microservice-assessment-feedback"));

        config.setUrls(urls);
        config.setOperationsSorter("alpha");

        return config;
    }

}