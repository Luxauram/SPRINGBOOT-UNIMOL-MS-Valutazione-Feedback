package it.unimol.microservice_assessment_feedback.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "I tipi di categoria del feedback (e.g., CONTENT, PRESENTATION, CORRECTNESS, OTHER)")
public enum FeedbackCategory {
    CONTENT,
    PRESENTATION,
    CORRECTNESS,
    OTHER
}
