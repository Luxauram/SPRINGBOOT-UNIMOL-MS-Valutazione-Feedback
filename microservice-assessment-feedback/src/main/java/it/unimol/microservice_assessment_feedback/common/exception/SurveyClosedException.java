package it.unimol.microservice_assessment_feedback.common.exception;

public class SurveyClosedException extends RuntimeException {

    public SurveyClosedException(String message) {
        super(message);
    }
}
