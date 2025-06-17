package it.unimol.microservice_assessment_feedback.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipo di domanda del questionario (e.g., RATING, TEXT)")
public enum QuestionType {
    RATING,
    TEXT
}
