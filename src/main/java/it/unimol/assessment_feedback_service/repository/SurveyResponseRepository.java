package it.unimol.assessment_feedback_service.repository;

import it.unimol.assessment_feedback_service.model.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {

    List<SurveyResponse> findBySurveyId(Long surveyId);

    List<SurveyResponse> findBySurveyIdAndStudentId(Long surveyId, Long studentId);

    @Query("SELECT COUNT(sr) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId")
    Long countBySurveyId(@Param("surveyId") Long surveyId);

    @Query("SELECT AVG(sr.numericRating) FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.questionId = :questionId")
    Double getAverageRatingBySurveyIdAndQuestionId(
            @Param("surveyId") Long surveyId,
            @Param("questionId") Long questionId);

    @Query("SELECT sr FROM SurveyResponse sr WHERE sr.survey.id = :surveyId AND sr.textComment IS NOT NULL AND sr.textComment <> ''")
    List<SurveyResponse> findAllWithCommentsForSurvey(@Param("surveyId") Long surveyId);
}
