package it.unimol.microservice_assessment_feedback.common.exception;

public class DuplicateResponseException extends RuntimeException {
    public DuplicateResponseException(String message) {
        super(message);
    }
}
