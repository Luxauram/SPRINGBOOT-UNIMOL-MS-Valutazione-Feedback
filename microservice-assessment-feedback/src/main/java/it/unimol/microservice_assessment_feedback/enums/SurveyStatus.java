package it.unimol.microservice_assessment_feedback.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Lo stato del questionario (e.g., DRAFT, ACTIVE, CLOSED)")
public enum SurveyStatus {
    DRAFT,
    ACTIVE,
    CLOSED
}
