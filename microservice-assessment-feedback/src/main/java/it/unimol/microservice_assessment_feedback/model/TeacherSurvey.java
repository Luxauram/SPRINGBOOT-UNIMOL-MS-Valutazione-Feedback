package it.unimol.microservice_assessment_feedback.model;

import it.unimol.microservice_assessment_feedback.enums.SurveyStatus;
import it.unimol.microservice_assessment_feedback.dto.TeacherSurveyDTO;
import it.unimol.microservice_assessment_feedback.common.util.ListToJsonConverter;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "teacher_surveys")
public class TeacherSurvey {

    @Id
    private String id;

    @Column(name = "course_id", nullable = false)
    private String courseId;

    @Column(name = "teacher_id", nullable = false)
    private String teacherId;

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

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "questions", columnDefinition = "TEXT")
    @Convert(converter = ListToJsonConverter.class)
    private List<TeacherSurveyDTO.SurveyQuestionDTO> questions;

    // Costruttore
    public TeacherSurvey() {
    }

    public TeacherSurvey(String id, String courseId, String teacherId, String academicYear,
                         Integer semester, SurveyStatus status, LocalDateTime creationDate,
                         LocalDateTime closingDate, LocalDateTime createdAt, LocalDateTime updatedAt,
                         String title, String description, List<TeacherSurveyDTO.SurveyQuestionDTO> questions) {
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
        this.title = title;
        this.description = description;
        this.questions = questions;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String courseId;
        private String teacherId;
        private String academicYear;
        private Integer semester;
        private SurveyStatus status;
        private LocalDateTime creationDate;
        private LocalDateTime closingDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String title;
        private String description;
        private List<TeacherSurveyDTO.SurveyQuestionDTO> questions;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder courseId(String courseId) {
            this.courseId = courseId;
            return this;
        }

        public Builder teacherId(String teacherId) {
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

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder questions(List<TeacherSurveyDTO.SurveyQuestionDTO> questions) {
            this.questions = questions;
            return this;
        }

        public TeacherSurvey build() {
            return new TeacherSurvey(id, courseId, teacherId, academicYear, semester,
                    status, creationDate, closingDate, createdAt, updatedAt,
                    title, description, questions);
        }
    }

    // Getter e Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }

    public SurveyStatus getStatus() { return status; }
    public void setStatus(SurveyStatus status) { this.status = status; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    public LocalDateTime getClosingDate() { return closingDate; }
    public void setClosingDate(LocalDateTime closingDate) { this.closingDate = closingDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<TeacherSurveyDTO.SurveyQuestionDTO> getQuestions() { return questions; }
    public void setQuestions(List<TeacherSurveyDTO.SurveyQuestionDTO> questions) { this.questions = questions; }

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
                Objects.equals(updatedAt, that.updatedAt) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(questions, that.questions);
    }

    // hashCode
    @Override
    public int hashCode() {
        return Objects.hash(id, courseId, teacherId, academicYear, semester,
                status, creationDate, closingDate, createdAt, updatedAt,
                title, description, questions);
    }

    // ToString
    @Override
    public String toString() {
        return "TeacherSurvey{" +
                "id='" + id + '\'' +
                ", courseId='" + courseId + '\'' +
                ", teacherId='" + teacherId + '\'' +
                ", academicYear='" + academicYear + '\'' +
                ", semester=" + semester +
                ", status=" + status +
                ", creationDate=" + creationDate +
                ", closingDate=" + closingDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", questions=" + questions +
                '}';
    }
}