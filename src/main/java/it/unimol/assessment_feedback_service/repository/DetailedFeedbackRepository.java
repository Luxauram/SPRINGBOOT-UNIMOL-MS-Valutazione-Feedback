package it.unimol.assessment_feedback_service.repository;

import it.unimol.assessment_feedback_service.model.DetailedFeedback;
import it.unimol.assessment_feedback_service.enums.FeedbackCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetailedFeedbackRepository extends JpaRepository<DetailedFeedback, Long> {

    List<DetailedFeedback> findByAssessmentId(Long assessmentId);

    List<DetailedFeedback> findByCategory(FeedbackCategory category);

    @Query("SELECT df FROM DetailedFeedback df JOIN df.assessment a WHERE a.studentId = :studentId")
    List<DetailedFeedback> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT df FROM DetailedFeedback df JOIN df.assessment a WHERE a.teacherId = :teacherId")
    List<DetailedFeedback> findByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT df FROM DetailedFeedback df JOIN df.assessment a WHERE a.courseId = :courseId")
    List<DetailedFeedback> findByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT df FROM DetailedFeedback df JOIN df.assessment a WHERE a.studentId = :studentId AND df.category = :category")
    List<DetailedFeedback> findByStudentIdAndCategory(@Param("studentId") Long studentId,
                                                      @Param("category") FeedbackCategory category);
}
