package it.unimol.assessment_feedback_service.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Objects;

public class SurveyResponseDTO {

    private Long id;

    @NotNull(message = "SurveyID richiesto")
    private Long surveyId;

    private Long studentId;

    @NotNull(message = "QuestionID richiesto")
    private Long questionId;

    @Min(value = 1, message = "La valutazione deve essere tra 1 e 5")
    @Max(value = 5, message = "La valutazione deve essere tra 1 e 5")
    private Integer numericRating;

    @Size(max = 1000, message = "I commenti non possono superare i 1000 caratteri")
    private String textComment;

    private LocalDateTime submissionDate;

    // Costruttore
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

    // Builder pattern
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

    // Getter e Setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getSurveyId() {
        return surveyId;
    }
    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public Long getStudentId() {
        return studentId;
    }
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getQuestionId() {
        return questionId;
    }
    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Integer getNumericRating() {
        return numericRating;
    }
    public void setNumericRating(Integer numericRating) {
        this.numericRating = numericRating;
    }

    public String getTextComment() {
        return textComment;
    }
    public void setTextComment(String textComment) {
        this.textComment = textComment;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }
    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    // Equals
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

    // HashCode
    @Override
    public int hashCode() {
        return Objects.hash(id, surveyId, studentId, questionId,
                numericRating, textComment, submissionDate);
    }

    // ToString
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