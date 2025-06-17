package it.unimol.microservice_assessment_feedback.repository;

import it.unimol.microservice_assessment_feedback.model.SurveyResponse;
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
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, String> {

    // Find ID
    List<SurveyResponse> findBySurveyId(String surveyId);
    List<SurveyResponse> findByStudentId(String studentId);
    List<SurveyResponse> findByQuestionId(String questionId);

    // Bool
    boolean existsBySurveyIdAndStudentId(String surveyId, String studentId);

    // Questionari con ommenti
    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.textComment IS NOT NULL AND sr.textComment != ''")
    List<SurveyResponse> findAllWithCommentsForSurvey(@Param("surveyId") String surveyId);

   /* // Query Entità Singole
    List<SurveyResponse> findBySurveyId(String surveyId);
    List<SurveyResponse> findByStudentId(String studentId);
    List<SurveyResponse> findByQuestionId(String questionId);

    // Query Combinate
    List<SurveyResponse> findBySurveyIdAndStudentId(String surveyId, String studentId);
    List<SurveyResponse> findBySurveyIdAndQuestionId(String surveyId, String questionId);
    List<SurveyResponse> findByStudentIdAndQuestionId(String studentId, String questionId);

    // Query per Survey associati
    @Query("SELECT sr FROM SurveyResponse sr JOIN sr.survey s WHERE s.teacherId = :teacherId")
    List<SurveyResponse> findByTeacherId(@Param("teacherId") String teacherId);

    @Query("SELECT sr FROM SurveyResponse sr JOIN sr.survey s WHERE s.courseId = :courseId")
    List<SurveyResponse> findByCourseId(@Param("courseId") String courseId);

    @Query("SELECT sr FROM SurveyResponse sr JOIN sr.survey s WHERE s.teacherId = :teacherId AND s.courseId = :courseId")
    List<SurveyResponse> findByTeacherIdAndCourseId(@Param("teacherId") String teacherId, @Param("courseId") String courseId);

    // Query temporali
    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.submissionDate BETWEEN :startDate AND :endDate")
    List<SurveyResponse> findBySubmissionDateBetween(@Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.studentId = :studentId AND sr.submissionDate BETWEEN :startDate AND :endDate")
    List<SurveyResponse> findByStudentIdAndSubmissionDateBetween(@Param("studentId") String studentId,
                                                                 @Param("startDate") LocalDateTime startDate,
                                                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.submissionDate BETWEEN :startDate AND :endDate")
    List<SurveyResponse> findBySurveyIdAndSubmissionDateBetween(@Param("surveyId") String surveyId,
                                                                @Param("startDate") LocalDateTime startDate,
                                                                @Param("endDate") LocalDateTime endDate);

    // Query Rating e Statistiche
    @Query("SELECT AVG(sr.numericRating) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId")
    Optional<Double> findAverageRatingBySurveyId(@Param("surveyId") String surveyId);

    @Query("SELECT AVG(sr.numericRating) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.questionId = :questionId")
    Optional<Double> findAverageRatingBySurveyIdAndQuestionId(@Param("surveyId") String surveyId,
                                                              @Param("questionId") String questionId);

    @Query("SELECT AVG(sr.numericRating) FROM SurveyResponse sr WHERE sr.studentId = :studentId")
    Optional<Double> findAverageRatingByStudentId(@Param("studentId") String studentId);

    @Query("SELECT AVG(sr.numericRating) FROM SurveyResponse sr WHERE sr.questionId = :questionId")
    Optional<Double> findAverageRatingByQuestionId(@Param("questionId") String questionId);

    @Query("SELECT AVG(sr.numericRating) FROM SurveyResponse sr JOIN sr.survey s WHERE s.teacherId = :teacherId")
    Optional<Double> findAverageRatingByTeacherId(@Param("teacherId") String teacherId);

    @Query("SELECT AVG(sr.numericRating) FROM SurveyResponse sr JOIN sr.survey s WHERE s.courseId = :courseId")
    Optional<Double> findAverageRatingByCourseId(@Param("courseId") String courseId);

    // Query per range di rating
    List<SurveyResponse> findByNumericRatingBetween(Integer minRating, Integer maxRating);

    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.numericRating BETWEEN :minRating AND :maxRating")
    List<SurveyResponse> findBySurveyIdAndNumericRatingBetween(@Param("surveyId") String surveyId,
                                                               @Param("minRating") Integer minRating,
                                                               @Param("maxRating") Integer maxRating);

    // Query per conteggi
    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId")
    Long countBySurveyId(@Param("surveyId") String surveyId);

    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.studentId = :studentId")
    Long countByStudentId(@Param("studentId") String studentId);

    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.questionId = :questionId")
    Long countByQuestionId(@Param("questionId") String questionId);

    @Query("SELECT COUNT(DISTINCT sr.studentId) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId")
    Long countDistinctStudentsBySurveyId(@Param("surveyId") String surveyId);

    // Query di esistenza
    boolean existsBySurveyIdAndStudentId(String surveyId, String studentId);
    boolean existsBySurveyIdAndStudentIdAndQuestionId(String surveyId, String studentId, String questionId);

    // Query per commenti
    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.textComment IS NOT NULL AND sr.textComment <> ''")
    List<SurveyResponse> findAllWithCommentsForSurvey(@Param("surveyId") String surveyId);

    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.textComment IS NOT NULL AND sr.textComment <> ''")
    Long countCommentsForSurvey(@Param("surveyId") String surveyId);

    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.textComment IS NOT NULL AND sr.textComment <> ''")
    List<SurveyResponse> findAllWithComments();

    // Query ordinate
    List<SurveyResponse> findByStudentIdOrderBySubmissionDateDesc(String studentId);
    List<SurveyResponse> findBySurveyIdOrderBySubmissionDateDesc(String surveyId);
    List<SurveyResponse> findBySurveyIdOrderByNumericRatingDesc(String surveyId);

    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.survey.id = :surveyId ORDER BY sr.numericRating ASC")
    List<SurveyResponse> findBySurveyIdOrderByNumericRatingAsc(@Param("surveyId") String surveyId);

    @Query("SELECT sr FROM SurveyResponse sr JOIN sr.survey s WHERE s.teacherId = :teacherId ORDER BY sr.submissionDate DESC")
    List<SurveyResponse> findByTeacherIdOrderBySubmissionDateDesc(@Param("teacherId") String teacherId);

    // Query per le risposte più recenti
    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.studentId = :studentId ORDER BY sr.submissionDate DESC LIMIT 1")
    Optional<SurveyResponse> findLatestResponseByStudentId(@Param("studentId") String studentId);

    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.survey.id = :surveyId ORDER BY sr.submissionDate DESC LIMIT 1")
    Optional<SurveyResponse> findLatestResponseBySurveyId(@Param("surveyId") String surveyId);

    // Query per performance e rating
    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.survey.id = :surveyId ORDER BY sr.numericRating DESC")
    List<SurveyResponse> findHighestRatingsBySurvey(@Param("surveyId") String surveyId);

    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.survey.id = :surveyId ORDER BY sr.numericRating ASC")
    List<SurveyResponse> findLowestRatingsBySurvey(@Param("surveyId") String surveyId);

    // Query per statistiche dettagliate
    @Query("SELECT sr.questionId, AVG(sr.numericRating), COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.numericRating IS NOT NULL GROUP BY sr.questionId")
    List<Object[]> findStatisticsBySurveyId(@Param("surveyId") String surveyId);

    @Query("SELECT sr.survey.id, AVG(sr.numericRating), COUNT(sr) FROM SurveyResponse sr WHERE sr.studentId = :studentId AND sr.numericRating IS NOT NULL GROUP BY sr.survey.id")
    List<Object[]> findStatisticsByStudentId(@Param("studentId") String studentId);

    // Query con paginazione
    Page<SurveyResponse> findBySurveyId(String surveyId, Pageable pageable);
    Page<SurveyResponse> findByStudentId(String studentId, Pageable pageable);
    Page<SurveyResponse> findByQuestionId(String questionId, Pageable pageable);*/

}
