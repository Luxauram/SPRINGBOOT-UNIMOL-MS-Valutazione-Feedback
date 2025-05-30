package it.unimol.assessment_feedback_service.repository;

import it.unimol.assessment_feedback_service.model.TeacherSurvey;
import it.unimol.assessment_feedback_service.enums.SurveyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherSurveyRepository extends JpaRepository<TeacherSurvey, Long> {

    List<TeacherSurvey> findByCourseId(Long courseId);

    List<TeacherSurvey> findByTeacherId(Long teacherId);

    List<TeacherSurvey> findByStatus(SurveyStatus status);

    List<TeacherSurvey> findByTeacherIdAndStatus(Long teacherId, SurveyStatus status);

    List<TeacherSurvey> findByCourseIdAndStatus(Long courseId, SurveyStatus status);

    List<TeacherSurvey> findByAcademicYearAndSemester(String academicYear, Integer semester);
}
