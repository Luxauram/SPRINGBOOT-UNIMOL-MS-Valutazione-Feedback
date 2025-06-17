package it.unimol.microservice_assessment_feedback.config.rabbitmq;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class RabbitMQTemplateConfig {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQTemplateConfig.class);

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setCreateMessageIds(true);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);

        template.setMessageConverter(messageConverter);

        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                logger.error("Message not delivered to exchange. Correlation ID: {}, Cause: {}",
                        correlationData != null ? correlationData.getId() : "unknown", cause);
            } else {
                logger.debug("Message successfully delivered to exchange. Correlation ID: {}",
                        correlationData != null ? correlationData.getId() : "unknown");
            }
        });

        template.setReturnsCallback(returned -> {
            logger.error("Message returned from exchange. Reply Code: {}, Reply Text: {}, Exchange: {}, Routing Key: {}, Message: {}",
                    returned.getReplyCode(),
                    returned.getReplyText(),
                    returned.getExchange(),
                    returned.getRoutingKey(),
                    returned.getMessage().toString());
        });

        template.setMandatory(true);

        return template;
    }
}
