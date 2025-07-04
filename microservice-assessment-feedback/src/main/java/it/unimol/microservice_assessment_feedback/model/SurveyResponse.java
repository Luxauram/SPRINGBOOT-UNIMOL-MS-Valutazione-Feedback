package it.unimol.microservice_assessment_feedback.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "survey_responses")
public class SurveyResponse {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private TeacherSurvey survey;

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "question_id", nullable = false)
    private String questionId;

    @Column(name = "numeric_rating")
    private Integer numericRating;

    @Column(name = "text_comment", length = 1000)
    private String textComment;

    @Column(name = "submission_date", nullable = false)
    private LocalDateTime submissionDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Costruttore
    public SurveyResponse() {
    }

    public SurveyResponse(String id, TeacherSurvey survey, String studentId, String questionId,
                          Integer numericRating, String textComment, LocalDateTime submissionDate,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.survey = survey;
        this.studentId = studentId;
        this.questionId = questionId;
        this.numericRating = numericRating;
        this.textComment = textComment;
        this.submissionDate = submissionDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private TeacherSurvey survey;
        private String studentId;
        private String questionId;
        private Integer numericRating;
        private String textComment;
        private LocalDateTime submissionDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder survey(TeacherSurvey survey) {
            this.survey = survey;
            return this;
        }

        public Builder studentId(String studentId) {
            this.studentId = studentId;
            return this;
        }

        public Builder questionId(String questionId) {
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

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public SurveyResponse build() {
            return new SurveyResponse(id, survey, studentId, questionId, numericRating,
                    textComment, submissionDate, createdAt, updatedAt);
        }
    }

    // Getter e Setter
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public TeacherSurvey getSurvey() {
        return survey;
    }
    public void setSurvey(TeacherSurvey survey) {
        this.survey = survey;
    }

    public String getStudentId() {
        return studentId;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getQuestionId() {
        return questionId;
    }
    public void setQuestionId(String questionId) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // JPA
    @PrePersist
    protected void onCreate() {
        if (id == null || id.isEmpty()) {
            id = UUID.randomUUID().toString();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SurveyResponse that = (SurveyResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(survey, that.survey) &&
                Objects.equals(studentId, that.studentId) &&
                Objects.equals(questionId, that.questionId) &&
                Objects.equals(numericRating, that.numericRating) &&
                Objects.equals(textComment, that.textComment) &&
                Objects.equals(submissionDate, that.submissionDate) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt);
    }

    // HashCode
    @Override
    public int hashCode() {
        return Objects.hash(id, survey, studentId, questionId, numericRating,
                textComment, submissionDate, createdAt, updatedAt);
    }

    // ToString
    @Override
    public String toString() {
        return "SurveyResponse{" +
                "id='" + id + '\'' +
                ", survey=" + survey +
                ", studentId='" + studentId + '\'' +
                ", questionId='" + questionId + '\'' +
                ", numericRating=" + numericRating +
                ", textComment='" + textComment + '\'' +
                ", submissionDate=" + submissionDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}