package it.unimol.assessment_feedback_service.exception;

public class SurveyClosedException extends RuntimeException {

    public SurveyClosedException(String message) {
        super(message);
    }
}
