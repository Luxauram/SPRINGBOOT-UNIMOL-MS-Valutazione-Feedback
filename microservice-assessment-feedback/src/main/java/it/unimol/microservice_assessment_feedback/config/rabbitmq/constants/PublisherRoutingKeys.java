package it.unimol.microservice_assessment_feedback.config.rabbitmq.constants;

public final class PublisherRoutingKeys {

    private PublisherRoutingKeys() {}

    // ===================================================================
    //  ASSESSMENT ROUTING KEYS
    // ===================================================================
    public static final String ASSESSMENT_CREATED = "assessment.created";
    public static final String ASSESSMENT_UPDATED = "assessment.updated";
    public static final String ASSESSMENT_DELETED = "assessment.deleted";

    // ===================================================================
    //  FEEDBACK ROUTING KEYS
    // ===================================================================
    public static final String FEEDBACK_CREATED = "feedback.created";
    public static final String FEEDBACK_UPDATED = "feedback.updated";
    public static final String FEEDBACK_DELETED = "feedback.deleted";

    // ===================================================================
    //  SURVEY ROUTING KEYS
    // ===================================================================
    public static final String SURVEY_COMPLETED = "survey.completed";
    public static final String SURVEY_RESPONSE_SUBMITTED = "survey.response.submitted";
    public static final String SURVEY_RESPONSES_BULK_SUBMITTED = "survey.responses.bulk.submitted";
    public static final String SURVEY_RESULTS_REQUESTED = "survey.results.requested";
    public static final String SURVEY_COMMENTS_REQUESTED = "survey.comments.requested";
}
