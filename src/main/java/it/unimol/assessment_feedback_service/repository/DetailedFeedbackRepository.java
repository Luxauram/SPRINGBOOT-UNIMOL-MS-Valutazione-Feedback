package it.unimol.assessment_feedback_service.repository;

import it.unimol.assessment_feedback_service.model.DetailedFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetailedFeedbackRepository extends JpaRepository<DetailedFeedback, Long> {

    List<DetailedFeedback> findByAssessmentId(Long assessmentId);

    void deleteByAssessmentId(Long assessmentId);
}
