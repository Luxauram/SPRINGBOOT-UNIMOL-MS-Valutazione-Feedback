package it.unimol.assessment_feedback_service.model;

import it.unimol.assessment_feedback_service.enums.ReferenceType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "assessments")
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_id", nullable = false)
    private Long referenceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type", nullable = false)
    private ReferenceType referenceType;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @Column(nullable = false)
    private Double score;

    @Column(name = "assessment_date", nullable = false)
    private LocalDateTime assessmentDate;

    @Column(length = 1000)
    private String notes;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Costruttore
    public Assessment() {
    }

    public Assessment(Long id, Long referenceId, ReferenceType referenceType, Long studentId,
                      Long teacherId, Double score, LocalDateTime assessmentDate, String notes,
                      Long courseId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.referenceId = referenceId;
        this.referenceType = referenceType;
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.score = score;
        this.assessmentDate = assessmentDate;
        this.notes = notes;
        this.courseId = courseId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long referenceId;
        private ReferenceType referenceType;
        private Long studentId;
        private Long teacherId;
        private Double score;
        private LocalDateTime assessmentDate;
        private String notes;
        private Long courseId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder referenceId(Long referenceId) {
            this.referenceId = referenceId;
            return this;
        }

        public Builder referenceType(ReferenceType referenceType) {
            this.referenceType = referenceType;
            return this;
        }

        public Builder studentId(Long studentId) {
            this.studentId = studentId;
            return this;
        }

        public Builder teacherId(Long teacherId) {
            this.teacherId = teacherId;
            return this;
        }

        public Builder score(Double score) {
            this.score = score;
            return this;
        }

        public Builder assessmentDate(LocalDateTime assessmentDate) {
            this.assessmentDate = assessmentDate;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder courseId(Long courseId) {
            this.courseId = courseId;
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

        public Assessment build() {
            return new Assessment(id, referenceId, referenceType, studentId, teacherId,
                    score, assessmentDate, notes, courseId, createdAt, updatedAt);
        }
    }

    // Getter e Setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getReferenceId() {
        return referenceId;
    }
    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public ReferenceType getReferenceType() {
        return referenceType;
    }
    public void setReferenceType(ReferenceType referenceType) {
        this.referenceType = referenceType;
    }

    public Long getStudentId() {
        return studentId;
    }
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getTeacherId() {
        return teacherId;
    }
    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Double getScore() {
        return score;
    }
    public void setScore(Double score) {
        this.score = score;
    }

    public LocalDateTime getAssessmentDate() {
        return assessmentDate;
    }
    public void setAssessmentDate(LocalDateTime assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getCourseId() {
        return courseId;
    }
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
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
        Assessment that = (Assessment) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(referenceId, that.referenceId) &&
                referenceType == that.referenceType &&
                Objects.equals(studentId, that.studentId) &&
                Objects.equals(teacherId, that.teacherId) &&
                Objects.equals(score, that.score) &&
                Objects.equals(assessmentDate, that.assessmentDate) &&
                Objects.equals(notes, that.notes) &&
                Objects.equals(courseId, that.courseId) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt);
    }

    // HashCode
    @Override
    public int hashCode() {
        return Objects.hash(id, referenceId, referenceType, studentId, teacherId,
                score, assessmentDate, notes, courseId, createdAt, updatedAt);
    }

    // ToString
    @Override
    public String toString() {
        return "Assessment{" +
                "id=" + id +
                ", referenceId=" + referenceId +
                ", referenceType=" + referenceType +
                ", studentId=" + studentId +
                ", teacherId=" + teacherId +
                ", score=" + score +
                ", assessmentDate=" + assessmentDate +
                ", notes='" + notes + '\'' +
                ", courseId=" + courseId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}