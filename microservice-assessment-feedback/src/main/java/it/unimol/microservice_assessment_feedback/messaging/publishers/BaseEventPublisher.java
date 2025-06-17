package it.unimol.microservice_assessment_feedback.messaging.publishers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.Map;

public abstract class BaseEventPublisher {

    protected static final Logger logger = LoggerFactory.getLogger(BaseEventPublisher.class);

    @Autowired
    protected RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.assessments}")
    protected String assessmentsExchange;

    @Value("${spring.application.name:microservice-assessment-feedback}")
    protected String serviceName;

    /**
     * Pubblica un messaggio su RabbitMQ con retry automatico
     */
    @Retryable(value = {AmqpException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    protected void publishMessage(String routingKey, Map<String, Object> message, String entityType, String entityId) {
        try {
            rabbitTemplate.convertAndSend(assessmentsExchange, routingKey, message);
            logger.info("{} event published successfully for {} ID: {}",
                    message.get("eventType"), entityType, entityId);
        } catch (Exception e) {
            logger.error("Error publishing {} event for {} ID: {}",
                    message.get("eventType"), entityType, entityId, e);
            throw e;
        }
    }

    /**
     * Crea i campi base comuni a tutti i messaggi
     */
    protected void addBaseMessageFields(Map<String, Object> message, String eventType) {
        message.put("eventType", eventType);
        message.put("serviceName", serviceName);
        message.put("timestamp", System.currentTimeMillis());
    }
}