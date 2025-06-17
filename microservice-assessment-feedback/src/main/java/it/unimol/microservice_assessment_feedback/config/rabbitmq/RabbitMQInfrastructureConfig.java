package it.unimol.microservice_assessment_feedback.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static it.unimol.microservice_assessment_feedback.config.rabbitmq.constants.PublisherRoutingKeys.*;
import static it.unimol.microservice_assessment_feedback.config.rabbitmq.constants.ConsumerRoutingKeys.*;
import static it.unimol.microservice_assessment_feedback.config.rabbitmq.constants.QueueConfigurationConstants.*;

@Configuration
public class RabbitMQInfrastructureConfig {

    private final RabbitMQProperties properties;

    public RabbitMQInfrastructureConfig(RabbitMQProperties properties) {
        this.properties = properties;
    }

    // ===================================================================
    //  EXCHANGES
    // ===================================================================
    @Bean
    public TopicExchange assessmentsExchange() {
        return ExchangeBuilder
                .topicExchange(properties.getExchange().getAssessments())
                .durable(true)
                .build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder
                .directExchange(properties.getExchange().getDlx())
                .durable(true)
                .build();
    }

    // ===================================================================
    //  DEAD LETTER QUEUE
    // ===================================================================
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder
                .durable(properties.getQueue().getDlq())
                .build();
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder
                .bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(DEAD_LETTER_ROUTING_KEY);
    }

    // ===================================================================
    //  PUBLISHER QUEUES - ASSESSMENT
    // ===================================================================
    @Bean
    public Queue assessmentCreatedQueue() {
        return createDurableQueueWithDLX(properties.getQueue().getAssessment().getCreated());
    }

    @Bean
    public Queue assessmentUpdatedQueue() {
        return createDurableQueueWithDLX(properties.getQueue().getAssessment().getUpdated());
    }

    @Bean
    public Queue assessmentDeletedQueue() {
        return createDurableQueueWithDLX(properties.getQueue().getAssessment().getDeleted());
    }

    @Bean
    public Binding assessmentCreatedBinding() {
        return BindingBuilder
                .bind(assessmentCreatedQueue())
                .to(assessmentsExchange())
                .with(ASSESSMENT_CREATED);
    }

    @Bean
    public Binding assessmentUpdatedBinding() {
        return BindingBuilder
                .bind(assessmentUpdatedQueue())
                .to(assessmentsExchange())
                .with(ASSESSMENT_UPDATED);
    }

    @Bean
    public Binding assessmentDeletedBinding() {
        return BindingBuilder
                .bind(assessmentDeletedQueue())
                .to(assessmentsExchange())
                .with(ASSESSMENT_DELETED);
    }

    // ===================================================================
    //  PUBLISHER QUEUES - FEEDBACK
    // ===================================================================
    @Bean
    public Queue feedbackCreatedQueue() {
        return createDurableQueueWithDLX(properties.getQueue().getFeedback().getCreated());
    }

    @Bean
    public Queue feedbackUpdatedQueue() {
        return createDurableQueueWithDLX(properties.getQueue().getFeedback().getUpdated());
    }

    @Bean
    public Queue feedbackDeletedQueue() {
        return createDurableQueueWithDLX(properties.getQueue().getFeedback().getDeleted());
    }

    @Bean
    public Binding feedbackCreatedBinding() {
        return BindingBuilder
                .bind(feedbackCreatedQueue())
                .to(assessmentsExchange())
                .with(FEEDBACK_CREATED);
    }

    @Bean
    public Binding feedbackUpdatedBinding() {
        return BindingBuilder
                .bind(feedbackUpdatedQueue())
                .to(assessmentsExchange())
                .with(FEEDBACK_UPDATED);
    }

    @Bean
    public Binding feedbackDeletedBinding() {
        return BindingBuilder
                .bind(feedbackDeletedQueue())
                .to(assessmentsExchange())
                .with(FEEDBACK_DELETED);
    }

    // ===================================================================
    //  PUBLISHER QUEUES - SURVEY
    // ===================================================================
    @Bean
    public Queue surveyCompletedQueue() {
        return createDurableQueueWithDLX(properties.getQueue().getSurvey().getCompleted());
    }

    @Bean
    public Queue surveyResponseSubmittedQueue() {
        return createDurableQueueWithDLX(properties.getQueue().getSurvey().getResponse().getSubmitted());
    }

    @Bean
    public Queue surveyResponsesBulkSubmittedQueue() {
        return createDurableQueueWithDLX(properties.getQueue().getSurvey().getResponse().getBulkSubmitted());
    }

    @Bean
    public Queue surveyResultsRequestedQueue() {
        return createDurableQueueWithDLX(properties.getQueue().getSurvey().getResultsRequested());
    }

    @Bean
    public Queue surveyCommentsRequestedQueue() {
        return createDurableQueueWithDLX(properties.getQueue().getSurvey().getCommentsRequested());
    }

    @Bean
    public Binding surveyCompletedBinding() {
        return BindingBuilder
                .bind(surveyCompletedQueue())
                .to(assessmentsExchange())
                .with(SURVEY_COMPLETED);
    }

    @Bean
    public Binding surveyResponseSubmittedBinding() {
        return BindingBuilder
                .bind(surveyResponseSubmittedQueue())
                .to(assessmentsExchange())
                .with(SURVEY_RESPONSE_SUBMITTED);
    }

    @Bean
    public Binding surveyResponsesBulkSubmittedBinding() {
        return BindingBuilder
                .bind(surveyResponsesBulkSubmittedQueue())
                .to(assessmentsExchange())
                .with(SURVEY_RESPONSES_BULK_SUBMITTED);
    }

    @Bean
    public Binding surveyResultsRequestedBinding() {
        return BindingBuilder
                .bind(surveyResultsRequestedQueue())
                .to(assessmentsExchange())
                .with(SURVEY_RESULTS_REQUESTED);
    }

    @Bean
    public Binding surveyCommentsRequestedBinding() {
        return BindingBuilder
                .bind(surveyCommentsRequestedQueue())
                .to(assessmentsExchange())
                .with(SURVEY_COMMENTS_REQUESTED);
    }

    @Bean
    public Binding userCreatedBinding() {
        return BindingBuilder
                .bind(userCreatedQueue())
                .to(assessmentsExchange())
                .with(USER_CREATED);
    }

    @Bean
    public Binding userUpdatedBinding() {
        return BindingBuilder
                .bind(userUpdatedQueue())
                .to(assessmentsExchange())
                .with(USER_UPDATED);
    }

    @Bean
    public Binding userDeletedBinding() {
        return BindingBuilder
                .bind(userDeletedQueue())
                .to(assessmentsExchange())
                .with(USER_DELETED);
    }

    @Bean
    public Binding roleAssignedBinding() {
        return BindingBuilder
                .bind(roleAssignedQueue())
                .to(assessmentsExchange())
                .with(ROLE_ASSIGNED);
    }

    // ===================================================================
    //  CONSUMERS QUEUES
    // ===================================================================
    @Bean
    public Queue assignmentSubmittedQueue() {
        return createDurableQueueWithDLX(properties.getQueue().getAssignmentSubmitted());
    }

    @Bean
    public Queue assignmentCreatedQueue() {
        return createDurableQueueWithDLX(properties.getQueue().getAssignmentCreated());
    }

    @Bean
    public Queue assignmentUpdatedQueue() {
        return createDurableQueueWithDLX(properties.getQueue().getAssignmentUpdated());
    }

    @Bean
    public Queue examCompletedQueue() {
        return createDurableQueueWithDLX(properties.getQueue().getExamCompleted());
    }

    @Bean
    public Queue examGradeRegisteredQueue() {
        return createDurableQueueWithDLX(properties.getQueue().getExamGradeRegistered());
    }

    @Bean
    public Queue courseCreatedQueue() {
        return createDurableQueueWithDLX(properties.getQueue().getCourseCreated());
    }

    @Bean
    public Queue courseDeletedQueue() {
        return createDurableQueueWithDLX(properties.getQueue().getCourseDeleted());
    }

    @Bean
    public Queue teacherCreatedQueue() {
        return createDurableQueueWithDLX(properties.getQueue().getTeacherCreated());
    }

    @Bean
    public Queue studentCreatedQueue() {
        return createDurableQueueWithDLX(properties.getQueue().getStudentCreated());
    }

    @Bean
    public Binding assignmentSubmittedBinding() {
        return BindingBuilder
                .bind(assignmentSubmittedQueue())
                .to(assessmentsExchange())
                .with(ASSIGNMENT_SUBMITTED);
    }

    @Bean
    public Binding assignmentCreatedBinding() {
        return BindingBuilder
                .bind(assignmentCreatedQueue())
                .to(assessmentsExchange())
                .with(ASSIGNMENT_CREATED);
    }

    @Bean
    public Binding assignmentUpdatedBinding() {
        return BindingBuilder
                .bind(assignmentUpdatedQueue())
                .to(assessmentsExchange())
                .with(ASSIGNMENT_UPDATED);
    }

    @Bean
    public Binding examCompletedBinding() {
        return BindingBuilder
                .bind(examCompletedQueue())
                .to(assessmentsExchange())
                .with(EXAM_COMPLETED);
    }

    @Bean
    public Binding examGradeRegisteredBinding() {
        return BindingBuilder
                .bind(examGradeRegisteredQueue())
                .to(assessmentsExchange())
                .with(EXAM_GRADE_REGISTERED);
    }

    @Bean
    public Binding courseCreatedBinding() {
        return BindingBuilder
                .bind(courseCreatedQueue())
                .to(assessmentsExchange())
                .with(COURSE_CREATED);
    }

    @Bean
    public Binding courseDeletedBinding() {
        return BindingBuilder
                .bind(courseDeletedQueue())
                .to(assessmentsExchange())
                .with(COURSE_DELETED);
    }

    @Bean
    public Binding teacherCreatedBinding() {
        return BindingBuilder
                .bind(teacherCreatedQueue())
                .to(assessmentsExchange())
                .with(TEACHER_CREATED);
    }

    @Bean
    public Binding studentCreatedBinding() {
        return BindingBuilder
                .bind(studentCreatedQueue())
                .to(assessmentsExchange())
                .with(STUDENT_CREATED);
    }

    @Bean
    public Queue userCreatedQueue() {
        return createDurableQueueWithDLX(properties.getQueue().getUserCreated());
    }

    @Bean
    public Queue userUpdatedQueue() {
        return createDurableQueueWithDLX(properties.getQueue().getUserUpdated());
    }

    @Bean
    public Queue userDeletedQueue() {
        return createDurableQueueWithDLX(properties.getQueue().getUserDeleted());
    }

    @Bean
    public Queue roleAssignedQueue() {
        return createDurableQueueWithDLX(properties.getQueue().getRoleAssigned());
    }

    // ===================================================================
    //  UTILITY METHODS
    // ===================================================================
    private Queue createDurableQueueWithDLX(String queueName) {
        return QueueBuilder
                .durable(queueName)
                .withArgument(X_DEAD_LETTER_EXCHANGE, properties.getExchange().getDlx())
                .withArgument(X_DEAD_LETTER_ROUTING_KEY, DEAD_LETTER_ROUTING_KEY)
                .withArgument(X_MESSAGE_TTL, properties.getMessage().getTtl())
                .build();
    }
}