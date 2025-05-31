package it.unimol.assessment_feedback_service.datafaker;

import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import java.util.Locale;

@Configuration
@Profile({"dev", "local"}) // Attivo solo nei profili di sviluppo
public class DataLoaderConfig {

    @Value("${app.data.faker.enabled:true}")
    private boolean fakerEnabled;

    @Value("${app.data.faker.locale:it}")
    private String fakerLocale;

    @Bean
    public Faker faker() {
        return new Faker(new Locale(fakerLocale));
    }

    @Bean
    @Order(1) // Eseguito per primo
    public CommandLineRunner dataLoader(DataLoaderService dataLoaderService) {
        return args -> {
            if (fakerEnabled) {
                dataLoaderService.loadInitialData();
            }
        };
    }
}
