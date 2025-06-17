package it.unimol.microservice_assessment_feedback.repository;

import it.unimol.microservice_assessment_feedback.model.DetailedFeedback;
import it.unimol.microservice_assessment_feedback.enums.FeedbackCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DetailedFeedbackRepository extends JpaRepository<DetailedFeedback, String> {

    // Find ID
    List<DetailedFeedback> findByAssessmentId(String assessmentId);

    @Query("SELECT df FROM DetailedFeedback df JOIN df.assessment a WHERE a.studentId = :studentId")
    List<DetailedFeedback> findByStudentId(@Param("studentId") String studentId);

    /*
    // Query Entità Singole
    List<DetailedFeedback> findByAssessmentId(String assessmentId);
    List<DetailedFeedback> findByCategory(FeedbackCategory category);

    @Query("SELECT df FROM DetailedFeedback df JOIN df.assessment a WHERE a.studentId = :studentId")
    List<DetailedFeedback> findByStudentId(@Param("studentId") String studentId);

    @Query("SELECT df FROM DetailedFeedback df JOIN df.assessment a WHERE a.teacherId = :teacherId")
    List<DetailedFeedback> findByTeacherId(@Param("teacherId") String teacherId);

    @Query("SELECT df FROM DetailedFeedback df JOIN df.assessment a WHERE a.courseId = :courseId")
    List<DetailedFeedback> findByCourseId(@Param("courseId") String courseId);

    // Query Combinate
    @Query("SELECT df FROM DetailedFeedback df JOIN df.assessment a WHERE a.studentId = :studentId AND df.category = :category")
    List<DetailedFeedback> findByStudentIdAndCategory(@Param("studentId") String studentId,
                                                      @Param("category") FeedbackCategory category);

    @Query("SELECT df FROM DetailedFeedback df JOIN df.assessment a WHERE a.courseId = :courseId AND df.category = :category")
    List<DetailedFeedback> findByCourseIdAndCategory(@Param("courseId") String courseId,
                                                     @Param("category") FeedbackCategory category);

    @Query("SELECT df FROM DetailedFeedback df JOIN df.assessment a WHERE a.teacherId = :teacherId AND df.category = :category")
    List<DetailedFeedback> findByTeacherIdAndCategory(@Param("teacherId") String teacherId,
                                                      @Param("category") FeedbackCategory category);

    @Query("SELECT df FROM DetailedFeedback df JOIN df.assessment a WHERE a.studentId = :studentId AND a.courseId = :courseId")
    List<DetailedFeedback> findByStudentIdAndCourseId(@Param("studentId") String studentId,
                                                      @Param("courseId") String courseId);

    // Query Temporali
    @Query("SELECT df FROM DetailedFeedback df WHERE df.createdAt BETWEEN :startDate AND :endDate")
    List<DetailedFeedback> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    @Query("SELECT df FROM DetailedFeedback df WHERE df.updatedAt BETWEEN :startDate AND :endDate")
    List<DetailedFeedback> findByUpdatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    @Query("SELECT df FROM DetailedFeedback df JOIN df.assessment a WHERE a.studentId = :studentId AND df.createdAt BETWEEN :startDate AND :endDate")
    List<DetailedFeedback> findByStudentIdAndCreatedAtBetween(@Param("studentId") String studentId,
                                                              @Param("startDate") LocalDateTime startDate,
                                                              @Param("endDate") LocalDateTime endDate);

    @Query("SELECT df FROM DetailedFeedback df JOIN df.assessment a WHERE a.courseId = :courseId AND df.createdAt BETWEEN :startDate AND :endDate")
    List<DetailedFeedback> findByCourseIdAndCreatedAtBetween(@Param("courseId") String courseId,
                                                             @Param("startDate") LocalDateTime startDate,
                                                             @Param("endDate") LocalDateTime endDate);

    // Query per Conteggi
    @Query("SELECT COUNT(df) FROM DetailedFeedback df WHERE df.assessment.id = :assessmentId")
    Long countByAssessmentId(@Param("assessmentId") String assessmentId);

    @Query("SELECT COUNT(df) FROM DetailedFeedback df JOIN df.assessment a WHERE a.studentId = :studentId")
    Long countByStudentId(@Param("studentId") String studentId);

    @Query("SELECT COUNT(df) FROM DetailedFeedback df JOIN df.assessment a WHERE a.courseId = :courseId")
    Long countByCourseId(@Param("courseId") String courseId);

    @Query("SELECT COUNT(df) FROM DetailedFeedback df WHERE df.category = :category")
    Long countByCategory(@Param("category") FeedbackCategory category);

    @Query("SELECT COUNT(df) FROM DetailedFeedback df JOIN df.assessment a WHERE a.studentId = :studentId AND df.category = :category")
    Long countByStudentIdAndCategory(@Param("studentId") String studentId,
                                     @Param("category") FeedbackCategory category);

    // Query di Esistenza
    boolean existsByAssessmentId(String assessmentId);

    @Query("SELECT CASE WHEN COUNT(df) > 0 THEN true ELSE false END FROM DetailedFeedback df JOIN df.assessment a WHERE a.studentId = :studentId AND df.category = :category")
    boolean existsByStudentIdAndCategory(@Param("studentId") String studentId,
                                         @Param("category") FeedbackCategory category);

    // Query Ordinate
    List<DetailedFeedback> findByAssessmentIdOrderByCreatedAtDesc(String assessmentId);

    @Query("SELECT df FROM DetailedFeedback df JOIN df.assessment a WHERE a.studentId = :studentId ORDER BY df.createdAt DESC")
    List<DetailedFeedback> findByStudentIdOrderByCreatedAtDesc(@Param("studentId") String studentId);

    @Query("SELECT df FROM DetailedFeedback df JOIN df.assessment a WHERE a.courseId = :courseId ORDER BY df.createdAt DESC")
    List<DetailedFeedback> findByCourseIdOrderByCreatedAtDesc(@Param("courseId") String courseId);

    @Query("SELECT df FROM DetailedFeedback df JOIN df.assessment a WHERE a.teacherId = :teacherId ORDER BY df.createdAt DESC")
    List<DetailedFeedback> findByTeacherIdOrderByCreatedAtDesc(@Param("teacherId") String teacherId);

    List<DetailedFeedback> findByCategoryOrderByCreatedAtDesc(FeedbackCategory category);

    // Query per Feedback Recenti/Specifici
    @Query("SELECT df FROM DetailedFeedback df JOIN df.assessment a WHERE a.studentId = :studentId ORDER BY df.createdAt DESC LIMIT 1")
    Optional<DetailedFeedback> findLatestFeedbackByStudentId(@Param("studentId") String studentId);

    @Query("SELECT df FROM DetailedFeedback df WHERE df.assessment.id = :assessmentId ORDER BY df.createdAt DESC LIMIT 1")
    Optional<DetailedFeedback> findLatestFeedbackByAssessmentId(@Param("assessmentId") String assessmentId);

    @Query("SELECT df FROM DetailedFeedback df WHERE df.createdAt >= :date ORDER BY df.createdAt DESC")
    List<DetailedFeedback> findRecentFeedback(@Param("date") LocalDateTime date);

    @Query("SELECT df FROM DetailedFeedback df JOIN df.assessment a WHERE a.studentId = :studentId AND df.createdAt >= :date ORDER BY df.createdAt DESC")
    List<DetailedFeedback> findRecentFeedbackByStudentId(@Param("studentId") String studentId,
                                                         @Param("date") LocalDateTime date);

    // Query per Statistiche/Analisi
    @Query("SELECT df.category, COUNT(df) FROM DetailedFeedback df GROUP BY df.category")
    List<Object[]> countFeedbackByCategory();

    @Query("SELECT df.category, COUNT(df) FROM DetailedFeedback df JOIN df.assessment a WHERE a.studentId = :studentId GROUP BY df.category")
    List<Object[]> countFeedbackByCategoryForStudent(@Param("studentId") String studentId);

    @Query("SELECT df.category, COUNT(df) FROM DetailedFeedback df JOIN df.assessment a WHERE a.courseId = :courseId GROUP BY df.category")
    List<Object[]> countFeedbackByCategoryForCourse(@Param("courseId") String courseId);

    // Query di Ricerca Testuale
    @Query("SELECT df FROM DetailedFeedback df WHERE LOWER(df.feedbackText) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<DetailedFeedback> findByFeedbackTextContaining(@Param("keyword") String keyword);

    @Query("SELECT df FROM DetailedFeedback df WHERE LOWER(df.strengths) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<DetailedFeedback> findByStrengthsContaining(@Param("keyword") String keyword);

    @Query("SELECT df FROM DetailedFeedback df WHERE LOWER(df.improvementAreas) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<DetailedFeedback> findByImprovementAreasContaining(@Param("keyword") String keyword);

    // Query di Eliminazione
    @Modifying
    @Query("DELETE FROM DetailedFeedback df WHERE df.assessment.id = :assessmentId")
    void deleteByAssessmentId(@Param("assessmentId") String assessmentId);

    @Modifying
    @Query("DELETE FROM DetailedFeedback df WHERE df.assessment.id IN (SELECT a.id FROM Assessment a WHERE a.studentId = :studentId)")
    void deleteByStudentId(@Param("studentId") String studentId);*/
}
