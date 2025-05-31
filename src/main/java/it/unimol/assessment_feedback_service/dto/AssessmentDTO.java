package it.unimol.assessment_feedback_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import it.unimol.assessment_feedback_service.enums.ReferenceType;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Schema(description = "DTO sulle Valutazioni")
public class AssessmentDTO {

    @Schema(description = "ID Valutazione", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ID Riferimento (valutazione o esame)", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "ReferenceId richiesto")
    private Long referenceId;

    @Schema(description = "Tipo di Riferimento (può essere solo ASSIGNMENT o EXAM)", example = "ASSIGNMENT", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Tipo di Riferimento (referenceType) richiesto")
    private ReferenceType referenceType;

    @Schema(description = "ID dello studente assegnato alla valutazione", example = "456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "StudentId richiesto")
    private Long studentId;

    @Schema(description = "ID del Docente assegnato alla valutazione", example = "789", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "TeacherId richiesto")
    private Long teacherId;

    @Schema(description = "Punteggio/Voto della valutazione", example = "27.5", minimum = "0", maximum = "30", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Voto richiesto")
    @DecimalMin(value = "0.0", message = "Il voto deve essere positivo")
    @DecimalMax(value = "30.0", message = "Il voto non può superare 30")
    private Double score;

    @Schema(description = "Data della creazione della valutazione", example = "2024-03-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime assessmentDate;

    @Schema(description = "Note opzionali della valutazione", example = "Ottimo lavoro, ma necessario qualche piccolo miglioramento", maxLength = 1000)
    @Size(max = 1000, message = "La nota non può superare i 1000 caratteri")
    private String notes;

    @Schema(description = "ID del corso al quale appartiene la valutazione", example = "101")
    private Long courseId;

    // Costruttore
    public AssessmentDTO() {}

    public AssessmentDTO(Long id, Long referenceId, ReferenceType referenceType, Long studentId,
                         Long teacherId, Double score, LocalDateTime assessmentDate, String notes, Long courseId) {
        this.id = id;
        this.referenceId = referenceId;
        this.referenceType = referenceType;
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.score = score;
        this.assessmentDate = assessmentDate;
        this.notes = notes;
        this.courseId = courseId;
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

        public AssessmentDTO build() {
            return new AssessmentDTO(id, referenceId, referenceType, studentId, teacherId, score, assessmentDate, notes, courseId);
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

    // toString
    @Override
    public String toString() {
        return "AssessmentDTO{" +
                "id=" + id +
                ", referenceId=" + referenceId +
                ", referenceType=" + referenceType +
                ", studentId=" + studentId +
                ", teacherId=" + teacherId +
                ", score=" + score +
                ", assessmentDate=" + assessmentDate +
                ", notes='" + notes + '\'' +
                ", courseId=" + courseId +
                '}';
    }

    // Equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssessmentDTO that = (AssessmentDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(referenceId, that.referenceId) &&
                referenceType == that.referenceType &&
                Objects.equals(studentId, that.studentId) &&
                Objects.equals(teacherId, that.teacherId) &&
                Objects.equals(score, that.score) &&
                Objects.equals(assessmentDate, that.assessmentDate) &&
                Objects.equals(notes, that.notes) &&
                Objects.equals(courseId, that.courseId);
    }

    // HashCode
    @Override
    public int hashCode() {
        return Objects.hash(id, referenceId, referenceType, studentId, teacherId, score, assessmentDate, notes, courseId);
    }
}