package it.unimol.assessment_feedback_service.repository;

import it.unimol.assessment_feedback_service.model.Assessment;
import it.unimol.assessment_feedback_service.enums.ReferenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    List<Assessment> findByStudentId(Long studentId);

    List<Assessment> findByTeacherId(Long teacherId);

    List<Assessment> findByReferenceIdAndReferenceType(Long referenceId, ReferenceType referenceType);

    List<Assessment> findByCourseId(Long courseId);

    List<Assessment> findByTeacherIdAndCourseId(Long teacherId, Long courseId);
}
