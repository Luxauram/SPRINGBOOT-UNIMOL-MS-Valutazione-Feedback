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

    @NotNull(message = "CourseID è richiesto")
    private Long courseId;

    @NotNull(message = "TeacherID è richiesto")
    private Long teacherId;

    @NotBlank(message = "AcademicYear è richiesto")
    @Pattern(regexp = "\\d{4}-\\d{4}", message = "Il formato dell'anno accademico deve essere YYYY-YYYY")
    private String academicYear;

    @NotNull(message = "Semester è richiesto")
    @Positive(message = "Semester deve essere un numero positivo")
    private Integer semester;

    private SurveyStatus status;

    private LocalDateTime creationDate;

    private LocalDateTime closingDate;

    // Costruttore
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

    // Builder pattern
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

    // Getter e Setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getTeacherId() {
        return teacherId;
    }
    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getAcademicYear() {
        return academicYear;
    }
    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public Integer getSemester() {
        return semester;
    }
    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public SurveyStatus getStatus() {
        return status;
    }
    public void setStatus(SurveyStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getClosingDate() {
        return closingDate;
    }
    public void setClosingDate(LocalDateTime closingDate) {
        this.closingDate = closingDate;
    }

    // Equals
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

    // HashCode
    @Override
    public int hashCode() {
        return Objects.hash(id, courseId, teacherId, academicYear,
                semester, status, creationDate, closingDate);
    }

    // ToString
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