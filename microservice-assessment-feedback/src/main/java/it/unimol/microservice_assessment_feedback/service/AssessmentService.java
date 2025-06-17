package it.unimol.microservice_assessment_feedback.service;

import it.unimol.microservice_assessment_feedback.dto.AssessmentDTO;
import it.unimol.microservice_assessment_feedback.enums.RoleType;
import it.unimol.microservice_assessment_feedback.messaging.publishers.AssessmentMessageService;
import it.unimol.microservice_assessment_feedback.model.Assessment;
import it.unimol.microservice_assessment_feedback.enums.ReferenceType;
import it.unimol.microservice_assessment_feedback.common.exception.ResourceNotFoundException;
import it.unimol.microservice_assessment_feedback.repository.AssessmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssessmentService {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentService.class);

    private final AssessmentRepository assessmentRepository;
    private final AssessmentMessageService assessmentMessageService;

    public AssessmentService(AssessmentRepository assessmentRepository, AssessmentMessageService assessmentMessageService) {
        this.assessmentRepository = assessmentRepository;
        this.assessmentMessageService = assessmentMessageService;
    }

    public List<AssessmentDTO> getAllAssessments() {
        logger.debug("Recupero di tutte le valutazioni");
        List<Assessment> assessments = assessmentRepository.findAll();
        logger.debug("Trovate {} valutazioni", assessments.size());

        return assessments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AssessmentDTO getAssessmentById(String id) {
        logger.debug("Recupero valutazione con ID: {}", id);

        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Valutazione non trovata con ID: {}", id);
                    return new ResourceNotFoundException("Valutazione non trovata con id: " + id);
                });

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Utente non autenticato");
        }

        String currentUsername = authentication.getName();

        String userRole = null;
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().startsWith("ROLE_")) {
                userRole = authority.getAuthority();
                break;
            }
        }

        if (RoleType.ROLE_STUDENT.equals(userRole)) {
            if (!assessment.getStudentId().equals(currentUsername)) {
                logger.warn("Studente {} ha tentato di accedere alla valutazione {} non sua",
                        currentUsername, id);
                throw new AccessDeniedException("Non autorizzato ad accedere a questa valutazione");
            }
            logger.debug("Accesso autorizzato per studente {} alla valutazione {}",
                    currentUsername, id);
        }

        logger.debug("Valutazione trovata con ID: {}", id);
        return convertToDTO(assessment);
    }

    public List<AssessmentDTO> getAssessmentsByStudentId(String studentId) {
        logger.debug("Recupero valutazioni per studente con ID: {}", studentId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Utente non autenticato");
        }

        String currentUsername = authentication.getName();

        String userRole = null;
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().startsWith("ROLE_")) {
                userRole = authority.getAuthority();
                break;
            }
        }

        if (RoleType.ROLE_STUDENT.equals(userRole)) {
            if (!studentId.equals(currentUsername)) {
                logger.warn("Studente {} ha tentato di accedere alle valutazioni dello studente {} non autorizzate",
                        currentUsername, studentId);
                throw new AccessDeniedException("Non autorizzato ad accedere alle valutazioni di questo studente");
            }
            logger.debug("Accesso autorizzato per studente {} alle proprie valutazioni", currentUsername);
        }

        List<Assessment> assessments = assessmentRepository.findByStudentId(studentId);
        logger.debug("Trovate {} valutazioni per studente {}", assessments.size(), studentId);

        return assessments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AssessmentDTO> getAssessmentsByAssignment(String assignmentId) {
        logger.debug("Recupero valutazioni per assignment con ID: {}", assignmentId);
        List<Assessment> assessments = assessmentRepository.findByReferenceIdAndReferenceType(assignmentId, ReferenceType.ASSIGNMENT);
        logger.debug("Trovate {} valutazioni per assignment {}", assessments.size(), assignmentId);

        return assessments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AssessmentDTO> getAssessmentsByExam(String examId) {
        logger.debug("Recupero valutazioni per exam con ID: {}", examId);
        List<Assessment> assessments = assessmentRepository.findByReferenceIdAndReferenceType(examId, ReferenceType.EXAM);
        logger.debug("Trovate {} valutazioni per exam {}", assessments.size(), examId);

        return assessments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public List<AssessmentDTO> getAssessmentsByCourse(String courseId) {
        logger.debug("Recupero valutazioni per corso con ID: {}", courseId);
        List<Assessment> assessments = assessmentRepository.findByCourseId(courseId);
        logger.debug("Trovate {} valutazioni per corso {}", assessments.size(), courseId);

        return assessments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AssessmentDTO createAssessment(AssessmentDTO assessmentDTO) {
        logger.info("Creazione nuova valutazione per studente: {} e corso: {}",
                assessmentDTO.getStudentId(), assessmentDTO.getCourseId());

        validateAssessmentData(assessmentDTO);

        Assessment assessment = convertToEntity(assessmentDTO);
        assessment.setAssessmentDate(LocalDateTime.now());

        Assessment savedAssessment = assessmentRepository.save(assessment);
        logger.info("Valutazione creata con successo con ID: {}", savedAssessment.getId());

        AssessmentDTO result = convertToDTO(savedAssessment);
        try {
            assessmentMessageService.publishAssessmentCreated(result);
            logger.debug("Evento di creazione valutazione pubblicato per ID: {}", result.getId());
        } catch (Exception e) {
            logger.warn("Errore nella pubblicazione dell'evento di creazione valutazione: {}", e.getMessage());
        }

        return result;
    }

    @Transactional
    public AssessmentDTO updateAssessment(String id, AssessmentDTO assessmentDTO) {
        logger.info("Aggiornamento valutazione con ID: {}", id);

        Assessment existingAssessment = assessmentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Tentativo di aggiornamento di valutazione inesistente con ID: {}", id);
                    return new ResourceNotFoundException("Valutazione non trovata con id: " + id);
                });

        if (assessmentDTO.getScore() != null) {
            existingAssessment.setScore(assessmentDTO.getScore());
        }
        if (assessmentDTO.getNotes() != null) {
            existingAssessment.setNotes(assessmentDTO.getNotes());
        }

        Assessment updatedAssessment = assessmentRepository.save(existingAssessment);
        logger.info("Valutazione aggiornata con successo con ID: {}", id);

        AssessmentDTO result = convertToDTO(updatedAssessment);
        try {
            assessmentMessageService.publishAssessmentUpdated(result);
            logger.debug("Evento di aggiornamento valutazione pubblicato per ID: {}", result.getId());
        } catch (Exception e) {
            logger.warn("Errore nella pubblicazione dell'evento di aggiornamento valutazione: {}", e.getMessage());
        }

        return result;
    }

    @Transactional
    public void deleteAssessment(String id) {
        logger.info("Eliminazione valutazione con ID: {}", id);

        if (!assessmentRepository.existsById(id)) {
            logger.warn("Tentativo di eliminazione di valutazione inesistente con ID: {}", id);
            throw new ResourceNotFoundException("Valutazione non trovata con id: " + id);
        }

        assessmentRepository.deleteById(id);
        logger.info("Valutazione eliminata con successo con ID: {}", id);

        try {
            assessmentMessageService.publishAssessmentDeleted(id);
            logger.debug("Evento di eliminazione valutazione pubblicato per ID: {}", id);
        } catch (Exception e) {
            logger.warn("Errore nella pubblicazione dell'evento di eliminazione valutazione: {}", e.getMessage());
        }
    }

    /**
     * Valida i dati essenziali dell'assessment prima della creazione
     */
    private void validateAssessmentData(AssessmentDTO assessmentDTO) {
        if (assessmentDTO.getStudentId() == null) {
            throw new IllegalArgumentException("StudentId è obbligatorio per creare una valutazione");
        }
        if (assessmentDTO.getTeacherId() == null) {
            throw new IllegalArgumentException("TeacherId è obbligatorio per creare una valutazione");
        }
        if (assessmentDTO.getCourseId() == null) {
            throw new IllegalArgumentException("CourseId è obbligatorio per creare una valutazione");
        }
        if (assessmentDTO.getReferenceId() == null) {
            throw new IllegalArgumentException("ReferenceId è obbligatorio per creare una valutazione");
        }
        if (assessmentDTO.getReferenceType() == null) {
            throw new IllegalArgumentException("ReferenceType è obbligatorio per creare una valutazione");
        }
        if (assessmentDTO.getScore() == null) {
            throw new IllegalArgumentException("Score è obbligatorio per creare una valutazione");
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