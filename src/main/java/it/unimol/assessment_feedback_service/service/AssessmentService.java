package it.unimol.assessment_feedback_service.service;

import it.unimol.assessment_feedback_service.dto.AssessmentDTO;
import it.unimol.assessment_feedback_service.model.Assessment;
import it.unimol.assessment_feedback_service.enums.ReferenceType;
import it.unimol.assessment_feedback_service.exception.ResourceNotFoundException;
import it.unimol.assessment_feedback_service.repository.AssessmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;

    public AssessmentService(AssessmentRepository assessmentRepository) {
        this.assessmentRepository = assessmentRepository;
    }

    public List<AssessmentDTO> getAllAssessments() {
        return assessmentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AssessmentDTO getAssessmentById(Long id) {
         Assessment assessment = assessmentRepository.findById(id)
                 .orElseThrow(() -> new ResourceNotFoundException("Valutazione non trovata con id: " + id));
        return assessment != null ? convertToDTO(assessment) : null;
    }

    public List<AssessmentDTO> getAssessmentsByStudentId(Long studentId) {
        return assessmentRepository.findByStudentId(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AssessmentDTO> getAssessmentsByAssignment(Long assignmentId) {
        return assessmentRepository.findByReferenceIdAndReferenceType(assignmentId, ReferenceType.ASSIGNMENT).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AssessmentDTO> getAssessmentsByExam(Long examId) {
        return assessmentRepository.findByReferenceIdAndReferenceType(examId, ReferenceType.EXAM).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AssessmentDTO> getAssessmentsByCourse(Long courseId) {
        return assessmentRepository.findByCourseId(courseId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AssessmentDTO createAssessment(AssessmentDTO assessmentDTO) {
        Assessment assessment = convertToEntity(assessmentDTO);
        assessment.setAssessmentDate(LocalDateTime.now());
        Assessment savedAssessment = assessmentRepository.save(assessment);
        return convertToDTO(savedAssessment);
    }

    @Transactional
    public AssessmentDTO updateAssessment(Long id, AssessmentDTO assessmentDTO) {
         Assessment existingAssessment = assessmentRepository.findById(id)
                 .orElseThrow(() -> new ResourceNotFoundException("Valutazione non trovata con id: " + id));

        if (existingAssessment != null) {
            existingAssessment.setScore(assessmentDTO.getScore());
            existingAssessment.setNotes(assessmentDTO.getNotes());
            Assessment updatedAssessment = assessmentRepository.save(existingAssessment);
            return convertToDTO(updatedAssessment);
        }
        return null;
    }

    @Transactional
    public void deleteAssessment(Long id) {
         if (!assessmentRepository.existsById(id)) {
             throw new ResourceNotFoundException("Valutazione non trovata con id: " + id);
         }
    }

    private AssessmentDTO convertToDTO(Assessment assessment) {
        AssessmentDTO dto = new AssessmentDTO();
        dto.setId(assessment.getId());
        dto.setReferenceId(assessment.getReferenceId());
        dto.setReferenceType(assessment.getReferenceType());
        dto.setStudentId(assessment.getStudentId());
        dto.setTeacherId(assessment.getTeacherId());
        dto.setScore(assessment.getScore());
        dto.setAssessmentDate(assessment.getAssessmentDate());
        dto.setNotes(assessment.getNotes());
        dto.setCourseId(assessment.getCourseId());
        return dto;
    }

    private Assessment convertToEntity(AssessmentDTO dto) {
        Assessment assessment = new Assessment();
        assessment.setId(dto.getId());
        assessment.setReferenceId(dto.getReferenceId());
        assessment.setReferenceType(dto.getReferenceType());
        assessment.setStudentId(dto.getStudentId());
        assessment.setTeacherId(dto.getTeacherId());
        assessment.setScore(dto.getScore());
        assessment.setAssessmentDate(dto.getAssessmentDate() != null ? dto.getAssessmentDate() : LocalDateTime.now());
        assessment.setNotes(dto.getNotes());
        assessment.setCourseId(dto.getCourseId());
        return assessment;
    }
}