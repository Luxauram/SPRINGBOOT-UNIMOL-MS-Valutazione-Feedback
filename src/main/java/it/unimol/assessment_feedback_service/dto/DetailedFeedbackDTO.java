package it.unimol.assessment_feedback_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import it.unimol.assessment_feedback_service.enums.FeedbackCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Schema(description = "DTO sul Feedback")
public class DetailedFeedbackDTO {

    @Schema(description = "ID Feedback", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ID della valutazione a cui appartiene il Feedback", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "AssessmentId richiesto")
    private Long assessmentId;

    @Schema(description = "Contenuto principale del testo di feedback", example = "Lo studente ha dimostrato un'eccellente comprensione dei concetti fondamentali...", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 2000)
    @NotBlank(message = "FeedbackText richiesto")
    @Size(max = 2000, message = "Il testo del Feedback (feedbackText) non può superare i 2000 caratteri")
    private String feedbackText;

    @Schema(description = "La categoria del Feedback", example = "POSITIVE", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Category richiesto")
    private FeedbackCategory category;

    @Schema(description = "Aree in cui lo studente ha mostrato punti di forza", example = "Forti capacità analitiche, presentazione chiara, buon uso di esempi", maxLength = 1000)
    @Size(max = 1000, message = "I punti di forza (strengths) non possono superare i 1000 caratteri")
    private String strengths;

    @Schema(description = "Aree da migliorare", example = "Potrebbe migliorare la gestione del tempo e fornire spiegazioni più dettagliate", maxLength = 1000)
    @Size(max = 1000, message = "Le aree di miglioramento (improvementAreas) non può superare i 1000 caratteri")
    private String improvementAreas;

    // Costruttore
    public DetailedFeedbackDTO() {}

    public DetailedFeedbackDTO(Long id, Long assessmentId, String feedbackText, FeedbackCategory category,
                               String strengths, String improvementAreas) {
        this.id = id;
        this.assessmentId = assessmentId;
        this.feedbackText = feedbackText;
        this.category = category;
        this.strengths = strengths;
        this.improvementAreas = improvementAreas;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long assessmentId;
        private String feedbackText;
        private FeedbackCategory category;
        private String strengths;
        private String improvementAreas;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder assessmentId(Long assessmentId) {
            this.assessmentId = assessmentId;
            return this;
        }

        public Builder feedbackText(String feedbackText) {
            this.feedbackText = feedbackText;
            return this;
        }

        public Builder category(FeedbackCategory category) {
            this.category = category;
            return this;
        }

        public Builder strengths(String strengths) {
            this.strengths = strengths;
            return this;
        }

        public Builder improvementAreas(String improvementAreas) {
            this.improvementAreas = improvementAreas;
            return this;
        }

        public DetailedFeedbackDTO build() {
            return new DetailedFeedbackDTO(id, assessmentId, feedbackText, category, strengths, improvementAreas);
        }
    }

    // Getter e Setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }
    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getFeedbackText() {
        return feedbackText;
    }
    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public FeedbackCategory getCategory() {
        return category;
    }
    public void setCategory(FeedbackCategory category) {
        this.category = category;
    }

    public String getStrengths() {
        return strengths;
    }
    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public String getImprovementAreas() {
        return improvementAreas;
    }
    public void setImprovementAreas(String improvementAreas) {
        this.improvementAreas = improvementAreas;
    }


    // Equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetailedFeedbackDTO that = (DetailedFeedbackDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(assessmentId, that.assessmentId) &&
                Objects.equals(feedbackText, that.feedbackText) &&
                category == that.category &&
                Objects.equals(strengths, that.strengths) &&
                Objects.equals(improvementAreas, that.improvementAreas);
    }

    // HashCode
    @Override
    public int hashCode() {
        return Objects.hash(id, assessmentId, feedbackText, category, strengths, improvementAreas);
    }

    // ToString
    @Override
    public String toString() {
        return "DetailedFeedbackDTO{" +
                "id=" + id +
                ", assessmentId=" + assessmentId +
                ", feedbackText='" + feedbackText + '\'' +
                ", category=" + category +
                ", strengths='" + strengths + '\'' +
                ", improvementAreas='" + improvementAreas + '\'' +
                '}';
    }
}