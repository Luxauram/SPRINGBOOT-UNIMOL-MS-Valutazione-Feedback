package it.unimol.microservice_assessment_feedback.config.rabbitmq.constants;

public final class QueueConfigurationConstants {

    private QueueConfigurationConstants() {}

    // ===================================================================
    //  QUEUE ARGUMENTS
    // ===================================================================
    public static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    public static final String X_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";
    public static final String X_MESSAGE_TTL = "x-message-ttl";
    public static final String X_MAX_RETRIES = "x-max-retries";
    public static final String X_RETRY_DELAY = "x-retry-delay";

    // ===================================================================
    //  DEAD LETTER CONFIGURATION
    // ===================================================================
    public static final String DEAD_LETTER_ROUTING_KEY = "dlq";

    // ===================================================================
    //  DEFAULT VALUES
    // ===================================================================
    public static final int DEFAULT_MESSAGE_TTL = 86400000;
    public static final int DEFAULT_MAX_RETRIES = 3;
    public static final int DEFAULT_RETRY_DELAY = 5000;
}
