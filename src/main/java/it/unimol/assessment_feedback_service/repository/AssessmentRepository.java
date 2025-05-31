package it.unimol.assessment_feedback_service.repository;

import it.unimol.assessment_feedback_service.enums.ReferenceType;
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

    // Query Entità Singole
    List<Assessment> findByStudentId(Long studentId);
    List<Assessment> findByTeacherId(Long teacherId);
    List<Assessment> findByCourseId(Long courseId);

    // Query Combinate
    List<Assessment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    List<Assessment> findByStudentIdAndTeacherId(Long studentId, Long teacherId);
    List<Assessment> findByTeacherIdAndCourseId(Long teacherId, Long courseId);

    // Query per ReferenceType
    List<Assessment> findByReferenceIdAndReferenceType(Long referenceId, ReferenceType referenceType);
    List<Assessment> findByReferenceType(ReferenceType referenceType);

    // Query per studente e ReferenceType
    List<Assessment> findByStudentIdAndReferenceType(Long studentId, ReferenceType referenceType);

    // Query temporali
    @Query("SELECT a FROM Assessment a WHERE a.assessmentDate BETWEEN :startDate AND :endDate")
    List<Assessment> findByAssessmentDateBetween(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM Assessment a WHERE a.studentId = :studentId AND a.assessmentDate BETWEEN :startDate AND :endDate")
    List<Assessment> findByStudentIdAndAssessmentDateBetween(@Param("studentId") Long studentId,
                                                             @Param("startDate") LocalDateTime startDate,
                                                             @Param("endDate") LocalDateTime endDate);

    // Query Punteggi e Statistiche
    @Query("SELECT AVG(a.score) FROM Assessment a WHERE a.studentId = :studentId")
    Optional<Double> findAverageScoreByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT AVG(a.score) FROM Assessment a WHERE a.courseId = :courseId")
    Optional<Double> findAverageScoreByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT AVG(a.score) FROM Assessment a WHERE a.teacherId = :teacherId")
    Optional<Double> findAverageScoreByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT AVG(a.score) FROM Assessment a WHERE a.referenceId = :referenceId AND a.referenceType = :referenceType")
    Optional<Double> findAverageScoreByReferenceIdAndType(@Param("referenceId") Long referenceId,
                                                          @Param("referenceType") ReferenceType referenceType);

    // Query per range di punteggi
    List<Assessment> findByScoreBetween(Double minScore, Double maxScore);

    @Query("SELECT a FROM Assessment a WHERE a.studentId = :studentId AND a.score BETWEEN :minScore AND :maxScore")
    List<Assessment> findByStudentIdAndScoreBetween(@Param("studentId") Long studentId,
                                                    @Param("minScore") Double minScore,
                                                    @Param("maxScore") Double maxScore);

    // Query per conteggi
    @Query("SELECT COUNT(a) FROM Assessment a WHERE a.studentId = :studentId")
    Long countByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(a) FROM Assessment a WHERE a.courseId = :courseId")
    Long countByCourseId(@Param("courseId") Long courseId);

    // Query di esistenza
    boolean existsByStudentIdAndReferenceIdAndReferenceType(Long studentId, Long referenceId, ReferenceType referenceType);

    // Query ordinate
    List<Assessment> findByStudentIdOrderByAssessmentDateDesc(Long studentId);
    List<Assessment> findByCourseIdOrderByScoreDesc(Long courseId);

    @Query("SELECT a FROM Assessment a WHERE a.teacherId = :teacherId ORDER BY a.assessmentDate DESC")
    List<Assessment> findByTeacherIdOrderByAssessmentDateDesc(@Param("teacherId") Long teacherId);

    // Query per le valutazioni più recenti
    @Query("SELECT a FROM Assessment a WHERE a.studentId = :studentId ORDER BY a.assessmentDate DESC LIMIT 1")
    Optional<Assessment> findLatestAssessmentByStudentId(@Param("studentId") Long studentId);

    // Query per performance
    @Query("SELECT a FROM Assessment a WHERE a.courseId = :courseId ORDER BY a.score DESC")
    List<Assessment> findTopPerformersByCourse(@Param("courseId") Long courseId);

    @Query("SELECT a FROM Assessment a WHERE a.courseId = :courseId ORDER BY a.score ASC")
    List<Assessment> findBottomPerformersByCourse(@Param("courseId") Long courseId);

}
