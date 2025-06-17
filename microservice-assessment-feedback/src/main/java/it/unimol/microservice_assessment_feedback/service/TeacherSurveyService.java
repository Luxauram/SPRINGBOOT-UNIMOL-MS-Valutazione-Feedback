package it.unimol.microservice_assessment_feedback.service;

import it.unimol.microservice_assessment_feedback.dto.TeacherSurveyDTO;
import it.unimol.microservice_assessment_feedback.enums.QuestionType;
import it.unimol.microservice_assessment_feedback.messaging.publishers.TeacherSurveyMessageService;
import it.unimol.microservice_assessment_feedback.model.TeacherSurvey;
import it.unimol.microservice_assessment_feedback.enums.SurveyStatus;
import it.unimol.microservice_assessment_feedback.common.exception.ResourceNotFoundException;
import it.unimol.microservice_assessment_feedback.repository.TeacherSurveyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherSurveyService {

    private static final Logger logger = LoggerFactory.getLogger(TeacherSurveyService.class);

    private final TeacherSurveyRepository surveyRepository;
    private final TeacherSurveyMessageService teacherSurveyMessageService;

    @Autowired
    public TeacherSurveyService(TeacherSurveyRepository surveyRepository, TeacherSurveyMessageService teacherSurveyMessageService) {
        this.surveyRepository = surveyRepository;
        this.teacherSurveyMessageService = teacherSurveyMessageService;
    }

    public List<TeacherSurveyDTO> getAllSurveys() {
        logger.info("Recupero di tutti i questionari");
        return surveyRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TeacherSurveyDTO getSurveyById(String id) {
        logger.info("Recupero questionario con id: {}", id);
        TeacherSurvey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Questionario non trovato con id: " + id));
        return convertToDTO(survey);
    }

    public List<TeacherSurveyDTO> getSurveysByCourse(String courseId) {
        logger.info("Recupero questionari per corso: {}", courseId);
        return surveyRepository.findByCourseId(courseId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TeacherSurveyDTO> getSurveysByTeacher(String teacherId) {
        logger.info("Recupero questionari per docente: {}", teacherId);
        return surveyRepository.findByTeacherId(teacherId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TeacherSurveyDTO> getActiveSurveys() {
        logger.info("Recupero questionari attivi");
        return surveyRepository.findByStatus(SurveyStatus.ACTIVE).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Object getSurveyStatistics(String surveyId) {
        logger.info("Richiesta statistiche per questionario: {}", surveyId);
        String requestedBy = getCurrentUser();
        teacherSurveyMessageService.publishSurveyResultsRequested(surveyId, requestedBy);

        TeacherSurvey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Questionario non trovato con id: " + surveyId));

        return new Object() {
            public final String surveyId = survey.getId();
            public final String title = survey.getTitle();
            public final String description = survey.getDescription();
            public final List<TeacherSurveyDTO.SurveyQuestionDTO> questions = survey.getQuestions();
            public final String status = survey.getStatus().toString();
            public final String message = "Statistiche placeholder per questionario " + surveyId;
        };
    }

    public Object getGeneralStatistics() {
        logger.info("Richiesta statistiche generali questionari");

        return new Object() {
            public final long totalSurveys = surveyRepository.count();
            public final long draftSurveys = surveyRepository.countByStatus(SurveyStatus.DRAFT);
            public final long activeSurveys = surveyRepository.countByStatus(SurveyStatus.ACTIVE);
            public final long closedSurveys = surveyRepository.countByStatus(SurveyStatus.CLOSED);
            public final String generatedAt = LocalDateTime.now().toString();
        };
    }

    @Transactional
    public TeacherSurveyDTO createSurvey(TeacherSurveyDTO surveyDTO) {
        logger.info("Creazione nuovo questionario per docente: {} e corso: {}",
                surveyDTO.getTeacherId(), surveyDTO.getCourseId());

        if (surveyRepository.existsByTeacherIdAndCourseIdAndAcademicYearAndSemester(
                surveyDTO.getTeacherId(), surveyDTO.getCourseId(),
                surveyDTO.getAcademicYear(), surveyDTO.getSemester())) {
            throw new IllegalArgumentException("Esiste già un questionario per questo docente, corso e periodo");
        }

        if (surveyDTO.getTitle() == null || surveyDTO.getTitle().isBlank()) {
            throw new IllegalArgumentException("Il titolo del questionario è obbligatorio.");
        }
        if (surveyDTO.getQuestions() == null || surveyDTO.getQuestions().isEmpty()) {
            throw new IllegalArgumentException("Il questionario deve contenere almeno una domanda.");
        }

        for (TeacherSurveyDTO.SurveyQuestionDTO question : surveyDTO.getQuestions()) {
            if (question.getQuestionText() == null || question.getQuestionText().isBlank()) {
                throw new IllegalArgumentException("Il testo di una domanda non può essere vuoto.");
            }
            if (question.getQuestionType() == null) {
                throw new IllegalArgumentException("Il tipo di domanda è obbligatorio.");
            }
            if (question.getQuestionType() == QuestionType.RATING) {
                if (question.getMinRating() == null || question.getMaxRating() == null) {
                    throw new IllegalArgumentException("Per le domande RATING, minRating e maxRating sono obbligatori.");
                }
                if (question.getMinRating() < 1 || question.getMaxRating() > 5 || question.getMinRating() > question.getMaxRating()) {
                    throw new IllegalArgumentException("Il range di rating non è valido (es. 1-5).");
                }
            } else if (question.getQuestionType() == QuestionType.TEXT) {
                if (question.getMaxLengthText() == null || question.getMaxLengthText() <= 0) {
                    throw new IllegalArgumentException("Per le domande TEXT, maxLengthText è obbligatorio e deve essere positivo.");
                }
            }
        }

        TeacherSurvey survey = convertToEntity(surveyDTO);
        survey.setStatus(SurveyStatus.DRAFT);
        survey.setCreationDate(LocalDateTime.now());

        TeacherSurvey savedSurvey = surveyRepository.save(survey);
        TeacherSurveyDTO result = convertToDTO(savedSurvey);

        try {
            teacherSurveyMessageService.publishSurveyCompleted(result);
            logger.info("Evento di questionario creato pubblicato per id: {}", result.getId());
        } catch (Exception e) {
            logger.error("Errore nella pubblicazione dell'evento per questionario id: {}", result.getId(), e);
        }

        return result;
    }

    @Transactional
    public TeacherSurveyDTO updateSurvey(String id, TeacherSurveyDTO surveyDTO) {
        logger.info("Aggiornamento questionario con id: {}", id);

        TeacherSurvey existingSurvey = surveyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Questionario non trovato con id: " + id));

        if (existingSurvey.getStatus() != SurveyStatus.DRAFT) {
            throw new IllegalStateException("Impossibile modificare un questionario che non sia in stato DRAFT. Utilizzare l'endpoint di cambio stato se necessario.");
        }

        if (surveyDTO.getTitle() != null) {
            existingSurvey.setTitle(surveyDTO.getTitle());
        }
        if (surveyDTO.getDescription() != null) {
            existingSurvey.setDescription(surveyDTO.getDescription());
        }
        if (surveyDTO.getQuestions() != null) {
            existingSurvey.setQuestions(surveyDTO.getQuestions());
        }
        if (surveyDTO.getAcademicYear() != null) {
            existingSurvey.setAcademicYear(surveyDTO.getAcademicYear());
        }
        if (surveyDTO.getSemester() != null) {
            existingSurvey.setSemester(surveyDTO.getSemester());
        }

        TeacherSurvey updatedSurvey = surveyRepository.save(existingSurvey);
        TeacherSurveyDTO result = convertToDTO(updatedSurvey);

        return result;
    }

    @Transactional
    public TeacherSurveyDTO changeSurveyStatus(String id, SurveyStatus newStatus) {
        logger.info("Cambio stato questionario id: {} a: {}", id, newStatus);

        TeacherSurvey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Questionario non trovato con id: " + id));

        validateStatusTransition(survey.getStatus(), newStatus);

        survey.setStatus(newStatus);

        if (newStatus == SurveyStatus.CLOSED) {
            survey.setClosingDate(LocalDateTime.now());
        } else if (newStatus == SurveyStatus.ACTIVE && survey.getClosingDate() != null) {
            survey.setClosingDate(null);
        }

        TeacherSurvey updatedSurvey = surveyRepository.save(survey);
        TeacherSurveyDTO result = convertToDTO(updatedSurvey);

        if (newStatus == SurveyStatus.CLOSED) {
            try {
                teacherSurveyMessageService.publishSurveyCompleted(result);
                logger.info("Evento di questionario completato pubblicato per id: {}", result.getId());
            } catch (Exception e) {
                logger.error("Errore nella pubblicazione dell'evento per questionario id: {}", result.getId(), e);
            }
        }

        return result;
    }


    @Transactional
    public void deleteSurvey(String id) {
        logger.info("Eliminazione questionario con id: {}", id);

        TeacherSurvey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Questionario non trovato con id: " + id));

        surveyRepository.deleteById(id);
        logger.info("Questionario eliminato con successo: {}", id);
    }

    private void validateStatusTransition(SurveyStatus currentStatus, SurveyStatus newStatus) {
        switch (currentStatus) {
            case DRAFT:
                if (newStatus != SurveyStatus.ACTIVE && newStatus != SurveyStatus.CLOSED) {
                    throw new IllegalArgumentException("Da DRAFT si può passare solo a ACTIVE o CLOSED");
                }
                break;
            case ACTIVE:
                if (newStatus != SurveyStatus.CLOSED) {
                    throw new IllegalArgumentException("Da ACTIVE si può passare solo a CLOSED");
                }
                break;
            case CLOSED:
                if (newStatus != SurveyStatus.ACTIVE && newStatus != SurveyStatus.DRAFT) {
                    throw new IllegalArgumentException("Da CLOSED si può passare solo ad ACTIVE o DRAFT");
                }
                break;
            default:
                throw new IllegalArgumentException("Transizione di stato non valida da " + currentStatus + " a " + newStatus);
        }
    }

    private String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "anonymous";
    }

    private TeacherSurveyDTO convertToDTO(TeacherSurvey survey) {
        return TeacherSurveyDTO.builder()
                .id(survey.getId())
                .courseId(survey.getCourseId())
                .teacherId(survey.getTeacherId())
                .academicYear(survey.getAcademicYear())
                .semester(survey.getSemester())
                .status(survey.getStatus())
                .creationDate(survey.getCreationDate())
                .closingDate(survey.getClosingDate())
                .title(survey.getTitle())
                .description(survey.getDescription())
                .questions(survey.getQuestions())
                .build();
    }


    private TeacherSurvey convertToEntity(TeacherSurveyDTO dto) {
        TeacherSurvey survey = new TeacherSurvey();
        if (dto.getId() != null) {
            survey.setId(dto.getId());
        }
        survey.setCourseId(dto.getCourseId());
        survey.setTeacherId(dto.getTeacherId());
        survey.setAcademicYear(dto.getAcademicYear());
        survey.setSemester(dto.getSemester());
        survey.setStatus(dto.getStatus() != null ? dto.getStatus() : SurveyStatus.DRAFT);
        survey.setCreationDate(dto.getCreationDate() != null ? dto.getCreationDate() : LocalDateTime.now());
        survey.setClosingDate(dto.getClosingDate());
        survey.setTitle(dto.getTitle());
        survey.setDescription(dto.getDescription());
        survey.setQuestions(dto.getQuestions());
        return survey;
    }
}