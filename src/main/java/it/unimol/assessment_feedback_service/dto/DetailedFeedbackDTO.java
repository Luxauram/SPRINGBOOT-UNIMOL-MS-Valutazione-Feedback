package it.unimol.assessment_feedback_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import it.unimol.assessment_feedback_service.enums.FeedbackCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Schema(description = "Data Transfer Object for detailed feedback information")
public class DetailedFeedbackDTO {

    @Schema(description = "Unique identifier of the feedback", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ID of the assessment this feedback belongs to", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Assessment ID is required")
    private Long assessmentId;

    @Schema(description = "Main feedback text content", example = "The student demonstrated excellent understanding of the core concepts...", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 2000)
    @NotBlank(message = "Feedback text is required")
    @Size(max = 2000, message = "Feedback text cannot exceed 2000 characters")
    private String feedbackText;

    @Schema(description = "Category of the feedback", example = "POSITIVE", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Category is required")
    private FeedbackCategory category;

    @Schema(description = "Areas where the student showed strengths", example = "Strong analytical skills, clear presentation, good use of examples", maxLength = 1000)
    @Size(max = 1000, message = "Strengths cannot exceed 1000 characters")
    private String strengths;

    @Schema(description = "Areas that need improvement", example = "Could improve time management and provide more detailed explanations", maxLength = 1000)
    @Size(max = 1000, message = "Improvement areas cannot exceed 1000 characters")
    private String improvementAreas;

    // Costruttori
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

    // toString
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

    // equals e hashCode
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

    @Override
    public int hashCode() {
        return Objects.hash(id, assessmentId, feedbackText, category, strengths, improvementAreas);
    }
}