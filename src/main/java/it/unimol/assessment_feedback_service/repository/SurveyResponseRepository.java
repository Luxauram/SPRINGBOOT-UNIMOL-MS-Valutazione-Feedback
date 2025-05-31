package it.unimol.assessment_feedback_service.repository;

import it.unimol.assessment_feedback_service.model.SurveyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {

    // Query Entità Singole
    List<SurveyResponse> findBySurveyId(Long surveyId);
    List<SurveyResponse> findByStudentId(Long studentId);
    List<SurveyResponse> findByQuestionId(Long questionId);

    // Query Combinate
    List<SurveyResponse> findBySurveyIdAndStudentId(Long surveyId, Long studentId);
    List<SurveyResponse> findBySurveyIdAndQuestionId(Long surveyId, Long questionId);
    List<SurveyResponse> findByStudentIdAndQuestionId(Long studentId, Long questionId);

    // Query per Survey associati
    @Query("SELECT sr FROM SurveyResponse sr JOIN sr.survey s WHERE s.teacherId = :teacherId")
    List<SurveyResponse> findByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT sr FROM SurveyResponse sr JOIN sr.survey s WHERE s.courseId = :courseId")
    List<SurveyResponse> findByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT sr FROM SurveyResponse sr JOIN sr.survey s WHERE s.teacherId = :teacherId AND s.courseId = :courseId")
    List<SurveyResponse> findByTeacherIdAndCourseId(@Param("teacherId") Long teacherId, @Param("courseId") Long courseId);

    // Query temporali
    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.submissionDate BETWEEN :startDate AND :endDate")
    List<SurveyResponse> findBySubmissionDateBetween(@Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.studentId = :studentId AND sr.submissionDate BETWEEN :startDate AND :endDate")
    List<SurveyResponse> findByStudentIdAndSubmissionDateBetween(@Param("studentId") Long studentId,
                                                                 @Param("startDate") LocalDateTime startDate,
                                                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.submissionDate BETWEEN :startDate AND :endDate")
    List<SurveyResponse> findBySurveyIdAndSubmissionDateBetween(@Param("surveyId") Long surveyId,
                                                                @Param("startDate") LocalDateTime startDate,
                                                                @Param("endDate") LocalDateTime endDate);

    // Query Rating e Statistiche
    @Query("SELECT AVG(sr.numericRating) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId")
    Optional<Double> findAverageRatingBySurveyId(@Param("surveyId") Long surveyId);

    @Query("SELECT AVG(sr.numericRating) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.questionId = :questionId")
    Optional<Double> findAverageRatingBySurveyIdAndQuestionId(@Param("surveyId") Long surveyId,
                                                              @Param("questionId") Long questionId);

    @Query("SELECT AVG(sr.numericRating) FROM SurveyResponse sr WHERE sr.studentId = :studentId")
    Optional<Double> findAverageRatingByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT AVG(sr.numericRating) FROM SurveyResponse sr WHERE sr.questionId = :questionId")
    Optional<Double> findAverageRatingByQuestionId(@Param("questionId") Long questionId);

    @Query("SELECT AVG(sr.numericRating) FROM SurveyResponse sr JOIN sr.survey s WHERE s.teacherId = :teacherId")
    Optional<Double> findAverageRatingByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT AVG(sr.numericRating) FROM SurveyResponse sr JOIN sr.survey s WHERE s.courseId = :courseId")
    Optional<Double> findAverageRatingByCourseId(@Param("courseId") Long courseId);

    // Query per range di rating
    List<SurveyResponse> findByNumericRatingBetween(Integer minRating, Integer maxRating);

    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.numericRating BETWEEN :minRating AND :maxRating")
    List<SurveyResponse> findBySurveyIdAndNumericRatingBetween(@Param("surveyId") Long surveyId,
                                                               @Param("minRating") Integer minRating,
                                                               @Param("maxRating") Integer maxRating);

    // Query per conteggi
    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId")
    Long countBySurveyId(@Param("surveyId") Long surveyId);

    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.studentId = :studentId")
    Long countByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.questionId = :questionId")
    Long countByQuestionId(@Param("questionId") Long questionId);

    @Query("SELECT COUNT(DISTINCT sr.studentId) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId")
    Long countDistinctStudentsBySurveyId(@Param("surveyId") Long surveyId);

    // Query di esistenza
    boolean existsBySurveyIdAndStudentId(Long surveyId, Long studentId);
    boolean existsBySurveyIdAndStudentIdAndQuestionId(Long surveyId, Long studentId, Long questionId);

    // Query per commenti
    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.textComment IS NOT NULL AND sr.textComment <> ''")
    List<SurveyResponse> findAllWithCommentsForSurvey(@Param("surveyId") Long surveyId);

    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.textComment IS NOT NULL AND sr.textComment <> ''")
    Long countCommentsForSurvey(@Param("surveyId") Long surveyId);

    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.textComment IS NOT NULL AND sr.textComment <> ''")
    List<SurveyResponse> findAllWithComments();

    // Query ordinate
    List<SurveyResponse> findByStudentIdOrderBySubmissionDateDesc(Long studentId);
    List<SurveyResponse> findBySurveyIdOrderBySubmissionDateDesc(Long surveyId);
    List<SurveyResponse> findBySurveyIdOrderByNumericRatingDesc(Long surveyId);

    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.survey.id = :surveyId ORDER BY sr.numericRating ASC")
    List<SurveyResponse> findBySurveyIdOrderByNumericRatingAsc(@Param("surveyId") Long surveyId);

    @Query("SELECT sr FROM SurveyResponse sr JOIN sr.survey s WHERE s.teacherId = :teacherId ORDER BY sr.submissionDate DESC")
    List<SurveyResponse> findByTeacherIdOrderBySubmissionDateDesc(@Param("teacherId") Long teacherId);

    // Query per le risposte più recenti
    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.studentId = :studentId ORDER BY sr.submissionDate DESC LIMIT 1")
    Optional<SurveyResponse> findLatestResponseByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.survey.id = :surveyId ORDER BY sr.submissionDate DESC LIMIT 1")
    Optional<SurveyResponse> findLatestResponseBySurveyId(@Param("surveyId") Long surveyId);

    // Query per performance e rating
    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.survey.id = :surveyId ORDER BY sr.numericRating DESC")
    List<SurveyResponse> findHighestRatingsBySurvey(@Param("surveyId") Long surveyId);

    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.survey.id = :surveyId ORDER BY sr.numericRating ASC")
    List<SurveyResponse> findLowestRatingsBySurvey(@Param("surveyId") Long surveyId);

    // Query per statistiche dettagliate
    @Query("SELECT sr.questionId, AVG(sr.numericRating), COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.numericRating IS NOT NULL GROUP BY sr.questionId")
    List<Object[]> findStatisticsBySurveyId(@Param("surveyId") Long surveyId);

    @Query("SELECT sr.survey.id, AVG(sr.numericRating), COUNT(sr) FROM SurveyResponse sr WHERE sr.studentId = :studentId AND sr.numericRating IS NOT NULL GROUP BY sr.survey.id")
    List<Object[]> findStatisticsByStudentId(@Param("studentId") Long studentId);

    // Query con paginazione
    Page<SurveyResponse> findBySurveyId(Long surveyId, Pageable pageable);
    Page<SurveyResponse> findByStudentId(Long studentId, Pageable pageable);
    Page<SurveyResponse> findByQuestionId(Long questionId, Pageable pageable);

}
