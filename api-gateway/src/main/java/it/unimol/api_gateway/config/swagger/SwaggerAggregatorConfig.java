package it.unimol.api_gateway.config.swagger;

import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class SwaggerAggregatorConfig {

    @Bean
    @Primary
    public SwaggerUiConfigProperties swaggerUiConfig() {
        SwaggerUiConfigProperties config = new SwaggerUiConfigProperties();
        Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> urls = new HashSet<>();

        // API Gateway stesso
        urls.add(new AbstractSwaggerUiConfigProperties.SwaggerUrl(
                "API Gateway", "/v3/api-docs", "gateway"));

        // User Service - endpoint diretti dei microservizi
        urls.add(new AbstractSwaggerUiConfigProperties.SwaggerUrl(
                "User & Role Service", "/api/user-service/v3/api-docs", "user-service"));

        // Assessment Service - endpoint diretto del microservizio
        urls.add(new AbstractSwaggerUiConfigProperties.SwaggerUrl(
                "Assessment & Feedback Service", "/api/assessment-service/v3/api-docs", "assessment-service"));

        config.setUrls(urls);

        // Configurazioni aggiuntive per evitare problemi
        config.setConfigUrl("/v3/api-docs/swagger-config");
        config.setValidatorUrl("");
        config.setTryItOutEnabled(true);
        config.setFilter("true");

        return config;
    }
}