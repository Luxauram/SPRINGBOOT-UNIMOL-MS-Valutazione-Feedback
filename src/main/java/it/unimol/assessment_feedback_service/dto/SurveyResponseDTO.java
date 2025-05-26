package it.unimol.assessment_feedback_service.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Objects;

public class SurveyResponseDTO {

    private Long id;

    @NotNull(message = "Survey ID is required")
    private Long surveyId;

    private Long studentId;

    @NotNull(message = "Question ID is required")
    private Long questionId;

    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer numericRating;

    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String textComment;

    private LocalDateTime submissionDate;

    // Constructors
    public SurveyResponseDTO() {
    }

    public SurveyResponseDTO(Long id, Long surveyId, Long studentId, Long questionId,
                             Integer numericRating, String textComment, LocalDateTime submissionDate) {
        this.id = id;
        this.surveyId = surveyId;
        this.studentId = studentId;
        this.questionId = questionId;
        this.numericRating = numericRating;
        this.textComment = textComment;
        this.submissionDate = submissionDate;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public Integer getNumericRating() {
        return numericRating;
    }

    public String getTextComment() {
        return textComment;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public void setNumericRating(Integer numericRating) {
        this.numericRating = numericRating;
    }

    public void setTextComment(String textComment) {
        this.textComment = textComment;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    // Builder pattern implementation
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long surveyId;
        private Long studentId;
        private Long questionId;
        private Integer numericRating;
        private String textComment;
        private LocalDateTime submissionDate;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder surveyId(Long surveyId) {
            this.surveyId = surveyId;
            return this;
        }

        public Builder studentId(Long studentId) {
            this.studentId = studentId;
            return this;
        }

        public Builder questionId(Long questionId) {
            this.questionId = questionId;
            return this;
        }

        public Builder numericRating(Integer numericRating) {
            this.numericRating = numericRating;
            return this;
        }

        public Builder textComment(String textComment) {
            this.textComment = textComment;
            return this;
        }

        public Builder submissionDate(LocalDateTime submissionDate) {
            this.submissionDate = submissionDate;
            return this;
        }

        public SurveyResponseDTO build() {
            return new SurveyResponseDTO(id, surveyId, studentId, questionId,
                    numericRating, textComment, submissionDate);
        }
    }

    // equals, hashCode and toString methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SurveyResponseDTO that = (SurveyResponseDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(surveyId, that.surveyId) &&
                Objects.equals(studentId, that.studentId) &&
                Objects.equals(questionId, that.questionId) &&
                Objects.equals(numericRating, that.numericRating) &&
                Objects.equals(textComment, that.textComment) &&
                Objects.equals(submissionDate, that.submissionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, surveyId, studentId, questionId,
                numericRating, textComment, submissionDate);
    }

    @Override
    public String toString() {
        return "SurveyResponseDTO{" +
                "id=" + id +
                ", surveyId=" + surveyId +
                ", studentId=" + studentId +
                ", questionId=" + questionId +
                ", numericRating=" + numericRating +
                ", textComment='" + textComment + '\'' +
                ", submissionDate=" + submissionDate +
                '}';
    }
}