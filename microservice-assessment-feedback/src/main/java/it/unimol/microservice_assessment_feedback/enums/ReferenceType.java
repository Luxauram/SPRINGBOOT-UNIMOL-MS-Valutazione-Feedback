package it.unimol.microservice_assessment_feedback.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipo di riferimento (e.g., ASSIGNMENT, EXAM)")
public enum ReferenceType {
    ASSIGNMENT,
    EXAM
}
