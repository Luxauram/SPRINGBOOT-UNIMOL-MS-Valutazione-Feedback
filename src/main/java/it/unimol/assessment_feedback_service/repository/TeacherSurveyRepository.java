package it.unimol.assessment_feedback_service.repository;

import it.unimol.assessment_feedback_service.model.TeacherSurvey;
import it.unimol.assessment_feedback_service.enums.SurveyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherSurveyRepository extends JpaRepository<TeacherSurvey, Long> {
    // Query Entità Singole
    List<TeacherSurvey> findByTeacherId(Long teacherId);
    List<TeacherSurvey> findByCourseId(Long courseId);
    List<TeacherSurvey> findByStatus(SurveyStatus status);
    List<TeacherSurvey> findByAcademicYear(String academicYear);
    List<TeacherSurvey> findBySemester(Integer semester);

    // Query Combinate
    List<TeacherSurvey> findByTeacherIdAndCourseId(Long teacherId, Long courseId);
    List<TeacherSurvey> findByAcademicYearAndSemester(String academicYear, Integer semester);
    List<TeacherSurvey> findByTeacherIdAndStatus(Long teacherId, SurveyStatus status);
    List<TeacherSurvey> findByCourseIdAndStatus(Long courseId, SurveyStatus status);
    List<TeacherSurvey> findByAcademicYearAndStatus(String academicYear, SurveyStatus status);

    // Query per controllo unicità
    Optional<TeacherSurvey> findByTeacherIdAndCourseIdAndAcademicYearAndSemester(
            Long teacherId, Long courseId, String academicYear, Integer semester);

    // Query temporali
    @Query("SELECT ts FROM TeacherSurvey ts WHERE ts.creationDate BETWEEN :startDate AND :endDate")
    List<TeacherSurvey> findByCreationDateBetween(@Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    @Query("SELECT ts FROM TeacherSurvey ts WHERE ts.closingDate BETWEEN :startDate AND :endDate")
    List<TeacherSurvey> findByClosingDateBetween(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT ts FROM TeacherSurvey ts WHERE ts.teacherId = :teacherId AND ts.creationDate BETWEEN :startDate AND :endDate")
    List<TeacherSurvey> findByTeacherIdAndCreationDateBetween(@Param("teacherId") Long teacherId,
                                                              @Param("startDate") LocalDateTime startDate,
                                                              @Param("endDate") LocalDateTime endDate);

    // Query per gestione scadenze
    @Query("SELECT ts FROM TeacherSurvey ts WHERE ts.status = :status AND ts.closingDate IS NOT NULL AND ts.closingDate < :currentDate")
    List<TeacherSurvey> findExpiredSurveys(@Param("status") SurveyStatus status,
                                           @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT ts FROM TeacherSurvey ts WHERE ts.status = 'ACTIVE' AND (ts.closingDate IS NULL OR ts.closingDate > :currentDate)")
    List<TeacherSurvey> findActiveSurveysNotExpired(@Param("currentDate") LocalDateTime currentDate);

    // Query per conteggi
    @Query("SELECT COUNT(ts) FROM TeacherSurvey ts WHERE ts.teacherId = :teacherId")
    Long countByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT COUNT(ts) FROM TeacherSurvey ts WHERE ts.courseId = :courseId")
    Long countByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(ts) FROM TeacherSurvey ts WHERE ts.teacherId = :teacherId AND ts.status = :status")
    Long countByTeacherIdAndStatus(@Param("teacherId") Long teacherId, @Param("status") SurveyStatus status);

    @Query("SELECT COUNT(ts) FROM TeacherSurvey ts WHERE ts.academicYear = :academicYear AND ts.status = :status")
    Long countByAcademicYearAndStatus(@Param("academicYear") String academicYear, @Param("status") SurveyStatus status);

    // Query di esistenza
    boolean existsByTeacherIdAndCourseIdAndAcademicYearAndSemester(
            Long teacherId, Long courseId, String academicYear, Integer semester);

    boolean existsByTeacherIdAndCourseIdAndStatus(Long teacherId, Long courseId, SurveyStatus status);

    // Query ordinate
    List<TeacherSurvey> findByTeacherIdOrderByCreationDateDesc(Long teacherId);
    List<TeacherSurvey> findByCourseIdOrderByCreationDateDesc(Long courseId);
    List<TeacherSurvey> findByStatusOrderByCreationDateAsc(SurveyStatus status);

    @Query("SELECT ts FROM TeacherSurvey ts WHERE ts.academicYear = :academicYear ORDER BY ts.creationDate DESC")
    List<TeacherSurvey> findByAcademicYearOrderByCreationDateDesc(@Param("academicYear") String academicYear);

    @Query("SELECT ts FROM TeacherSurvey ts WHERE ts.status = :status ORDER BY ts.closingDate ASC")
    List<TeacherSurvey> findByStatusOrderByClosingDateAsc(@Param("status") SurveyStatus status);

    // Query per i più recenti
    @Query("SELECT ts FROM TeacherSurvey ts WHERE ts.teacherId = :teacherId ORDER BY ts.creationDate DESC LIMIT 1")
    Optional<TeacherSurvey> findLatestSurveyByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT ts FROM TeacherSurvey ts WHERE ts.courseId = :courseId ORDER BY ts.creationDate DESC LIMIT 1")
    Optional<TeacherSurvey> findLatestSurveyByCourseId(@Param("courseId") Long courseId);

    // Query per performance e gestione
    @Query("SELECT ts FROM TeacherSurvey ts WHERE ts.status = 'ACTIVE' ORDER BY ts.creationDate ASC")
    List<TeacherSurvey> findOldestActiveSurveys();

    @Query("SELECT ts FROM TeacherSurvey ts WHERE ts.status = 'CLOSED' ORDER BY ts.closingDate DESC")
    List<TeacherSurvey> findRecentlyClosedSurveys();

    @Query("SELECT ts FROM TeacherSurvey ts WHERE ts.academicYear = :academicYear AND ts.semester = :semester ORDER BY ts.creationDate DESC")
    List<TeacherSurvey> findByAcademicYearAndSemesterOrderByCreationDateDesc(@Param("academicYear") String academicYear,
                                                                             @Param("semester") Integer semester);

    // Query per manutenzione (opzionale)
    @Modifying
    @Query("DELETE FROM TeacherSurvey ts WHERE ts.status = 'CLOSED' AND ts.closingDate < :cutoffDate")
    void deleteOldClosedSurveys(@Param("cutoffDate") LocalDateTime cutoffDate);

}