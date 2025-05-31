package it.unimol.assessment_feedback_service.repository;

import it.unimol.assessment_feedback_service.model.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    List<Assessment> findByStudentId(Long studentId);

    List<Assessment> findByTeacherId(Long teacherId);

    List<Assessment> findByCourseId(Long courseId);

    List<Assessment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Assessment> findByReferenceIdAndReferenceType(Long referenceId,
                                                       it.unimol.assessment_feedback_service.enums.ReferenceType referenceType);

    @Query("SELECT a FROM Assessment a WHERE a.assessmentDate BETWEEN :startDate AND :endDate")
    List<Assessment> findByAssessmentDateBetween(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT AVG(a.score) FROM Assessment a WHERE a.studentId = :studentId")
    Optional<Double> findAverageScoreByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT AVG(a.score) FROM Assessment a WHERE a.courseId = :courseId")
    Optional<Double> findAverageScoreByCourseId(@Param("courseId") Long courseId);
}
