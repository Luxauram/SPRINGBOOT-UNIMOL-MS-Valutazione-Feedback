package it.unimol.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:*}")
    private String allowedOrigins;

    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    @Value("${cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${cors.allow-credentials:true}")
    private Boolean allowCredentials;

    @Value("${cors.max-age:3600}")
    private Long maxAge;

    private final Environment environment;

    public CorsConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        if (isProductionProfile()) {
            corsConfig.setAllowedOriginPatterns(Arrays.asList(
                    "https://big-unimol-durello.it",
                    "https://*.big-unimol-durello.it"
            ));
        } else {
            if ("*".equals(allowedOrigins.trim())) {
                corsConfig.setAllowedOriginPatterns(Arrays.asList("*"));
            } else {
                List<String> originsList = Arrays.asList(allowedOrigins.split(","));
                originsList.replaceAll(String::trim);
                corsConfig.setAllowedOrigins(originsList);
            }
        }

        List<String> methodsList = Arrays.asList(allowedMethods.split(","));
        methodsList.replaceAll(String::trim);
        corsConfig.setAllowedMethods(methodsList);

        if ("*".equals(allowedHeaders.trim())) {
            corsConfig.setAllowedHeaders(Arrays.asList("*"));
        } else {
            List<String> headersList = Arrays.asList(allowedHeaders.split(","));
            headersList.replaceAll(String::trim);
            corsConfig.setAllowedHeaders(headersList);
        }

        corsConfig.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials",
                "Authorization",
                "X-Total-Count",
                "X-User-ID",
                "X-Roles"
        ));

        corsConfig.setAllowCredentials(allowCredentials);
        corsConfig.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

    private boolean isProductionProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        return Arrays.asList(activeProfiles).contains("prod") ||
                Arrays.asList(activeProfiles).contains("production");
    }
}
