package it.unimol.assessment_feedback_service.repository;

import it.unimol.assessment_feedback_service.model.TeacherSurvey;
import it.unimol.assessment_feedback_service.enums.SurveyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TeacherSurveyRepository extends JpaRepository<TeacherSurvey, Long> {

    List<TeacherSurvey> findByTeacherId(Long teacherId);

    List<TeacherSurvey> findByCourseId(Long courseId);

    List<TeacherSurvey> findByStatus(SurveyStatus status);

    List<TeacherSurvey> findByAcademicYear(String academicYear);

    List<TeacherSurvey> findByAcademicYearAndSemester(String academicYear, Integer semester);

    List<TeacherSurvey> findByTeacherIdAndCourseId(Long teacherId, Long courseId);

    @Query("SELECT ts FROM TeacherSurvey ts WHERE ts.creationDate BETWEEN :startDate AND :endDate")
    List<TeacherSurvey> findByCreationDateBetween(@Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    @Query("SELECT ts FROM TeacherSurvey ts WHERE ts.status = :status AND ts.closingDate < :currentDate")
    List<TeacherSurvey> findExpiredSurveys(@Param("status") SurveyStatus status,
                                           @Param("currentDate") LocalDateTime currentDate);
}