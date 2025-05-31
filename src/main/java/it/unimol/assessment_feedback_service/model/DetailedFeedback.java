package it.unimol.assessment_feedback_service.model;

import it.unimol.assessment_feedback_service.enums.FeedbackCategory;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "detailed_feedback")
public class DetailedFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @Column(name = "feedback_text", length = 2000, nullable = false)
    private String feedbackText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackCategory category;

    @Column(length = 1000)
    private String strengths;

    @Column(name = "improvement_areas", length = 1000)
    private String improvementAreas;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Costruttore
    public DetailedFeedback() {
    }

    public DetailedFeedback(Long id, Assessment assessment, String feedbackText,
                            FeedbackCategory category, String strengths, String improvementAreas,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.assessment = assessment;
        this.feedbackText = feedbackText;
        this.category = category;
        this.strengths = strengths;
        this.improvementAreas = improvementAreas;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Assessment assessment;
        private String feedbackText;
        private FeedbackCategory category;
        private String strengths;
        private String improvementAreas;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder assessment(Assessment assessment) {
            this.assessment = assessment;
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

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public DetailedFeedback build() {
            return new DetailedFeedback(id, assessment, feedbackText, category,
                    strengths, improvementAreas, createdAt, updatedAt);
        }
    }

    // Getter e Setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Assessment getAssessment() {
        return assessment;
    }
    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
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
        DetailedFeedback that = (DetailedFeedback) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(assessment, that.assessment) &&
                Objects.equals(feedbackText, that.feedbackText) &&
                category == that.category &&
                Objects.equals(strengths, that.strengths) &&
                Objects.equals(improvementAreas, that.improvementAreas) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt);
    }

    // HashCode
    @Override
    public int hashCode() {
        return Objects.hash(id, assessment, feedbackText, category,
                strengths, improvementAreas, createdAt, updatedAt);
    }

    // ToString
    @Override
    public String toString() {
        return "DetailedFeedback{" +
                "id=" + id +
                ", assessment=" + assessment +
                ", feedbackText='" + feedbackText + '\'' +
                ", category=" + category +
                ", strengths='" + strengths + '\'' +
                ", improvementAreas='" + improvementAreas + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}