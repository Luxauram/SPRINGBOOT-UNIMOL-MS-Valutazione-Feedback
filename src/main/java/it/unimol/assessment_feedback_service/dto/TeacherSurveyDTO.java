package it.unimol.assessment_feedback_service.dto;

import it.unimol.assessment_feedback_service.enums.SurveyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.Objects;

public class TeacherSurveyDTO {

    private Long id;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotNull(message = "Teacher ID is required")
    private Long teacherId;

    @NotBlank(message = "Academic year is required")
    @Pattern(regexp = "\\d{4}-\\d{4}", message = "Academic year format should be YYYY-YYYY")
    private String academicYear;

    @NotNull(message = "Semester is required")
    @Positive(message = "Semester must be positive")
    private Integer semester;

    private SurveyStatus status;

    private LocalDateTime creationDate;

    private LocalDateTime closingDate;

    // Constructors
    public TeacherSurveyDTO() {
    }

    public TeacherSurveyDTO(Long id, Long courseId, Long teacherId, String academicYear,
                            Integer semester, SurveyStatus status, LocalDateTime creationDate,
                            LocalDateTime closingDate) {
        this.id = id;
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.academicYear = academicYear;
        this.semester = semester;
        this.status = status;
        this.creationDate = creationDate;
        this.closingDate = closingDate;
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

        public TeacherSurveyDTO build() {
            return new TeacherSurveyDTO(id, courseId, teacherId, academicYear,
                    semester, status, creationDate, closingDate);
        }
    }

    // equals, hashCode and toString methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeacherSurveyDTO that = (TeacherSurveyDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(courseId, that.courseId) &&
                Objects.equals(teacherId, that.teacherId) &&
                Objects.equals(academicYear, that.academicYear) &&
                Objects.equals(semester, that.semester) &&
                status == that.status &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(closingDate, that.closingDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, courseId, teacherId, academicYear,
                semester, status, creationDate, closingDate);
    }

    @Override
    public String toString() {
        return "TeacherSurveyDTO{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", teacherId=" + teacherId +
                ", academicYear='" + academicYear + '\'' +
                ", semester=" + semester +
                ", status=" + status +
                ", creationDate=" + creationDate +
                ", closingDate=" + closingDate +
                '}';
    }
}