package it.unimol.assessment_feedback_service.repository;

import it.unimol.assessment_feedback_service.model.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {

    List<SurveyResponse> findBySurveyId(Long surveyId);

    List<SurveyResponse> findByStudentId(Long studentId);

    List<SurveyResponse> findByQuestionId(Long questionId);

    List<SurveyResponse> findBySurveyIdAndStudentId(Long surveyId, Long studentId);

    List<SurveyResponse> findBySurveyIdAndQuestionId(Long surveyId, Long questionId);

    @Query("SELECT AVG(sr.numericRating) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId")
    Optional<Double> findAverageRatingBySurveyId(@Param("surveyId") Long surveyId);

    @Query("SELECT AVG(sr.numericRating) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.questionId = :questionId")
    Optional<Double> findAverageRatingBySurveyIdAndQuestionId(@Param("surveyId") Long surveyId,
                                                              @Param("questionId") Long questionId);

    @Query("SELECT sr FROM SurveyResponse sr JOIN sr.survey s WHERE s.teacherId = :teacherId")
    List<SurveyResponse> findByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId")
    Long countBySurveyId(@Param("surveyId") Long surveyId);

    @Query("SELECT sr FROM SurveyResponse sr JOIN sr.survey s WHERE s.courseId = :courseId")
    List<SurveyResponse> findByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT AVG(sr.numericRating) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.questionId = :questionId")
    Double getAverageRatingBySurveyIdAndQuestionId(
            @Param("surveyId") Long surveyId,
            @Param("questionId") Long questionId);

    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.textComment IS NOT NULL AND sr.textComment <> ''")
    List<SurveyResponse> findAllWithCommentsForSurvey(@Param("surveyId") Long surveyId);
}
