package it.unimol.assessment_feedback_service.model;

import it.unimol.assessment_feedback_service.enums.SurveyStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "teacher_surveys")
public class TeacherSurvey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @Column(nullable = false)
    private Integer semester;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SurveyStatus status;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "closing_date")
    private LocalDateTime closingDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public TeacherSurvey() {
    }

    public TeacherSurvey(Long id, Long courseId, Long teacherId, String academicYear,
                         Integer semester, SurveyStatus status, LocalDateTime creationDate,
                         LocalDateTime closingDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.academicYear = academicYear;
        this.semester = semester;
        this.status = status;
        this.creationDate = creationDate;
        this.closingDate = closingDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public Integer getSemester() {
        return semester;
    }

    public SurveyStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getClosingDate() {
        return closingDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public void setStatus(SurveyStatus status) {
        this.status = status;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setClosingDate(LocalDateTime closingDate) {
        this.closingDate = closingDate;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Builder pattern implementation
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long courseId;
        private Long teacherId;
        private String academicYear;
        private Integer semester;
        private SurveyStatus status;
        private LocalDateTime creationDate;
        private LocalDateTime closingDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder courseId(Long courseId) {
            this.courseId = courseId;
            return this;
        }

        public Builder teacherId(Long teacherId) {
            this.teacherId = teacherId;
            return this;
        }

        public Builder academicYear(String academicYear) {
            this.academicYear = academicYear;
            return this;
        }

        public Builder semester(Integer semester) {
            this.semester = semester;
            return this;
        }

        public Builder status(SurveyStatus status) {
            this.status = status;
            return this;
        }

        public Builder creationDate(LocalDateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Builder closingDate(LocalDateTime closingDate) {
            this.closingDate = closingDate;
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

        public TeacherSurvey build() {
            return new TeacherSurvey(id, courseId, teacherId, academicYear, semester,
                    status, creationDate, closingDate, createdAt, updatedAt);
        }
    }

    // JPA lifecycle methods
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // equals, hashCode and toString methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeacherSurvey that = (TeacherSurvey) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(courseId, that.courseId) &&
                Objects.equals(teacherId, that.teacherId) &&
                Objects.equals(academicYear, that.academicYear) &&
                Objects.equals(semester, that.semester) &&
                status == that.status &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(closingDate, that.closingDate) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, courseId, teacherId, academicYear, semester,
                status, creationDate, closingDate, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "TeacherSurvey{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", teacherId=" + teacherId +
                ", academicYear='" + academicYear + '\'' +
                ", semester=" + semester +
                ", status=" + status +
                ", creationDate=" + creationDate +
                ", closingDate=" + closingDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}