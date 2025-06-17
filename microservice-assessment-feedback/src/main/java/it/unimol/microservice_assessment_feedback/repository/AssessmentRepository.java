package it.unimol.microservice_assessment_feedback.repository;

import it.unimol.microservice_assessment_feedback.enums.ReferenceType;
import it.unimol.microservice_assessment_feedback.model.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, String> {

    // Find ID
    List<Assessment> findByStudentId(String studentId);
    List<Assessment> findByTeacherId(String teacherId);
    List<Assessment> findByCourseId(String courseId);

    // Find ReferenceType
    List<Assessment> findByReferenceIdAndReferenceType(String referenceId, ReferenceType referenceType);

    /*// Query Entità Singole
    List<Assessment> findByStudentId(String studentId);
    List<Assessment> findByTeacherId(String teacherId);
    List<Assessment> findByCourseId(String courseId);

    // Query Combinate
    List<Assessment> findByStudentIdAndCourseId(String studentId, String courseId);
    List<Assessment> findByStudentIdAndTeacherId(String studentId, String teacherId);
    List<Assessment> findByTeacherIdAndCourseId(String teacherId, String courseId);

    // Query per ReferenceType
    List<Assessment> findByReferenceIdAndReferenceType(String referenceId, ReferenceType referenceType);
    List<Assessment> findByReferenceType(ReferenceType referenceType);

    // Query per studente e ReferenceType
    List<Assessment> findByStudentIdAndReferenceType(String studentId, ReferenceType referenceType);

    // Query temporali
    @Query("SELECT a FROM Assessment a WHERE a.assessmentDate BETWEEN :startDate AND :endDate")
    List<Assessment> findByAssessmentDateBetween(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM Assessment a WHERE a.studentId = :studentId AND a.assessmentDate BETWEEN :startDate AND :endDate")
    List<Assessment> findByStudentIdAndAssessmentDateBetween(@Param("studentId") String studentId,
                                                             @Param("startDate") LocalDateTime startDate,
                                                             @Param("endDate") LocalDateTime endDate);

    // Query Punteggi e Statistiche
    @Query("SELECT AVG(a.score) FROM Assessment a WHERE a.studentId = :studentId")
    Optional<Double> findAverageScoreByStudentId(@Param("studentId") String studentId);

    @Query("SELECT AVG(a.score) FROM Assessment a WHERE a.courseId = :courseId")
    Optional<Double> findAverageScoreByCourseId(@Param("courseId") String courseId);

    @Query("SELECT AVG(a.score) FROM Assessment a WHERE a.teacherId = :teacherId")
    Optional<Double> findAverageScoreByTeacherId(@Param("teacherId") String teacherId);

    @Query("SELECT AVG(a.score) FROM Assessment a WHERE a.referenceId = :referenceId AND a.referenceType = :referenceType")
    Optional<Double> findAverageScoreByReferenceIdAndType(@Param("referenceId") String referenceId,
                                                          @Param("referenceType") ReferenceType referenceType);

    // Query per range di punteggi
    List<Assessment> findByScoreBetween(Double minScore, Double maxScore);

    @Query("SELECT a FROM Assessment a WHERE a.studentId = :studentId AND a.score BETWEEN :minScore AND :maxScore")
    List<Assessment> findByStudentIdAndScoreBetween(@Param("studentId") String studentId,
                                                    @Param("minScore") Double minScore,
                                                    @Param("maxScore") Double maxScore);

    // Query per conteggi
    @Query("SELECT COUNT(a) FROM Assessment a WHERE a.studentId = :studentId")
    Long countByStudentId(@Param("studentId") String studentId);

    @Query("SELECT COUNT(a) FROM Assessment a WHERE a.courseId = :courseId")
    Long countByCourseId(@Param("courseId") String courseId);

    // Query di esistenza
    boolean existsByStudentIdAndReferenceIdAndReferenceType(String studentId, String referenceId, ReferenceType referenceType);

    // Query ordinate
    List<Assessment> findByStudentIdOrderByAssessmentDateDesc(String studentId);
    List<Assessment> findByCourseIdOrderByScoreDesc(String courseId);

    @Query("SELECT a FROM Assessment a WHERE a.teacherId = :teacherId ORDER BY a.assessmentDate DESC")
    List<Assessment> findByTeacherIdOrderByAssessmentDateDesc(@Param("teacherId") String teacherId);

    // Query per le valutazioni più recenti
    @Query("SELECT a FROM Assessment a WHERE a.studentId = :studentId ORDER BY a.assessmentDate DESC LIMIT 1")
    Optional<Assessment> findLatestAssessmentByStudentId(@Param("studentId") String studentId);

    // Query per performance
    @Query("SELECT a FROM Assessment a WHERE a.courseId = :courseId ORDER BY a.score DESC")
    List<Assessment> findTopPerformersByCourse(@Param("courseId") String courseId);

    @Query("SELECT a FROM Assessment a WHERE a.courseId = :courseId ORDER BY a.score ASC")
    List<Assessment> findBottomPerformersByCourse(@Param("courseId") String courseId);
*/
}
