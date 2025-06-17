package it.unimol.microservice_assessment_feedback.service;

import it.unimol.microservice_assessment_feedback.dto.SurveyResponseDTO;
import it.unimol.microservice_assessment_feedback.messaging.publishers.SurveyResponseMessageService;
import it.unimol.microservice_assessment_feedback.messaging.publishers.TeacherSurveyMessageService;
import it.unimol.microservice_assessment_feedback.model.SurveyResponse;
import it.unimol.microservice_assessment_feedback.model.TeacherSurvey;
import it.unimol.microservice_assessment_feedback.enums.SurveyStatus;
import it.unimol.microservice_assessment_feedback.common.exception.ResourceNotFoundException;
import it.unimol.microservice_assessment_feedback.common.exception.SurveyClosedException;
import it.unimol.microservice_assessment_feedback.common.exception.DuplicateResponseException;
import it.unimol.microservice_assessment_feedback.repository.SurveyResponseRepository;
import it.unimol.microservice_assessment_feedback.repository.TeacherSurveyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SurveyResponseService {

    private static final Logger logger = LoggerFactory.getLogger(DetailedFeedbackService.class);

    private final SurveyResponseRepository responseRepository;
    private final TeacherSurveyRepository surveyRepository;
    private final SurveyResponseMessageService surveyResponseMessageService;
    private final TeacherSurveyMessageService teacherSurveyMessageService;

    public SurveyResponseService(SurveyResponseRepository responseRepository,
                                 TeacherSurveyRepository surveyRepository,
                                 SurveyResponseMessageService surveyResponseMessageService,
                                 TeacherSurveyMessageService teacherSurveyMessageService) {
        this.responseRepository = responseRepository;
        this.surveyRepository = surveyRepository;
        this.surveyResponseMessageService = surveyResponseMessageService;
        this.teacherSurveyMessageService = teacherSurveyMessageService;
    }

    public List<SurveyResponseDTO> getResponsesBySurveyId(String surveyId, String userId) {
        TeacherSurvey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Questionario non trovato con id: " + surveyId));

        return responseRepository.findBySurveyId(surveyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<SurveyResponseDTO> getSurveyComments(String surveyId, String userId) {
        TeacherSurvey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Questionario non trovato con id: " + surveyId));

        teacherSurveyMessageService.publishSurveyCommentsRequested(surveyId, userId);

        return responseRepository.findBySurveyId(surveyId).stream()
                .filter(response -> response.getTextComment() != null && !response.getTextComment().trim().isEmpty())
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Map<String, Double> getSurveyResults(String surveyId, String userId) {
        TeacherSurvey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Questionario non trovato con id: " + surveyId));

        teacherSurveyMessageService.publishSurveyResultsRequested(surveyId, userId);

        List<SurveyResponse> responses = responseRepository.findBySurveyId(surveyId);

        return responses.stream()
                .filter(response -> response.getNumericRating() != null)
                .collect(Collectors.groupingBy(
                        SurveyResponse::getQuestionId,
                        Collectors.averagingDouble(SurveyResponse::getNumericRating)
                ));
    }


    @Transactional
    public List<SurveyResponseDTO> submitSurveyResponses(String surveyId, List<SurveyResponseDTO> responseDTOs, String authenticatedUserId) {

        TeacherSurvey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Questionario non trovato con id: " + surveyId));

        if (survey.getStatus() != SurveyStatus.ACTIVE) {
            throw new SurveyClosedException("Non è possibile inviare risposte ad un Questionario chiuso");
        }

        boolean hasAlreadyResponded = responseRepository.existsBySurveyIdAndStudentId(surveyId, authenticatedUserId);
        if (hasAlreadyResponded) {
            throw new DuplicateResponseException("Hai già compilato questo questionario");
        }

        validateSurveyResponses(responseDTOs, survey);

        Set<String> questionIds = responseDTOs.stream()
                .map(SurveyResponseDTO::getQuestionId)
                .collect(Collectors.toSet());

        if (questionIds.size() != responseDTOs.size()) {
            throw new IllegalArgumentException("Non è possibile inviare più risposte per la stessa domanda");
        }

        LocalDateTime submissionTime = LocalDateTime.now();
        List<SurveyResponse> responses = responseDTOs.stream()
                .map(dto -> {
                    SurveyResponse response = convertToEntity(dto);
                    response.setSurvey(survey);
                    response.setStudentId(authenticatedUserId);
                    response.setSubmissionDate(submissionTime);
                    return response;
                })
                .collect(Collectors.toList());

        List<SurveyResponse> savedResponses = responseRepository.saveAll(responses);
        List<SurveyResponseDTO> result = savedResponses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        try {
            surveyResponseMessageService.publishSurveyResponsesSubmitted(result, surveyId);
        } catch (Exception e) {
            logger.warn("Errore durante invio notifica per risposte questionario {}: {}", surveyId, e.getMessage());
        }

        return result;
    }

    /**
     * Valida le risposte del questionario
     */
    private void validateSurveyResponses(List<SurveyResponseDTO> responseDTOs, TeacherSurvey survey) {
        for (SurveyResponseDTO dto : responseDTOs) {

            if (dto.getQuestionId() == null || dto.getQuestionId().trim().isEmpty()) {
                throw new IllegalArgumentException("ID domanda non può essere vuoto");
            }

            if (dto.getNumericRating() == null &&
                    (dto.getTextComment() == null || dto.getTextComment().trim().isEmpty())) {
                throw new IllegalArgumentException("Ogni risposta deve contenere almeno una valutazione numerica o un commento");
            }

            if (dto.getNumericRating() != null) {
                if (dto.getNumericRating() < 1 || dto.getNumericRating() > 5) {
                    throw new IllegalArgumentException("La valutazione numerica deve essere tra 1 e 5");
                }
            }

            if (dto.getTextComment() != null && dto.getTextComment().length() > 1000) {
                throw new IllegalArgumentException("Il commento non può superare i 1000 caratteri");
            }

        }
    }

    public List<SurveyResponseDTO> getResponsesByStudentId(String studentId) {
        return responseRepository.findByStudentId(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TeacherSurvey> getAvailableSurveysForStudent(String studentId) {
        return surveyRepository.findByStatus(SurveyStatus.ACTIVE);
    }

    @Transactional
    public SurveyResponseDTO createResponse(SurveyResponseDTO responseDTO) {
        TeacherSurvey survey = surveyRepository.findById(responseDTO.getSurveyId())
                .orElseThrow(() -> new ResourceNotFoundException("Questionario non trovato con id: " + responseDTO.getSurveyId()));

        if (survey.getStatus() != SurveyStatus.ACTIVE) {
            throw new SurveyClosedException("Non è possibile inviare risposte ad un Questionario chiuso");
        }

        SurveyResponse response = convertToEntity(responseDTO);
        response.setSurvey(survey);
        response.setSubmissionDate(LocalDateTime.now());

        SurveyResponse savedResponse = responseRepository.save(response);
        SurveyResponseDTO result = convertToDTO(savedResponse);

        surveyResponseMessageService.publishSurveyResponseSubmitted(result);

        return result;
    }

    private SurveyResponseDTO convertToDTO(SurveyResponse response) {
        SurveyResponseDTO dto = new SurveyResponseDTO();
        dto.setId(response.getId());
        dto.setSurveyId(response.getSurvey().getId());
        dto.setStudentId(response.getStudentId());
        dto.setQuestionId(response.getQuestionId());
        dto.setNumericRating(response.getNumericRating());
        dto.setTextComment(response.getTextComment());
        dto.setSubmissionDate(response.getSubmissionDate());
        return dto;
    }

    private SurveyResponse convertToEntity(SurveyResponseDTO dto) {
        SurveyResponse response = new SurveyResponse();
        response.setId(dto.getId());
        response.setStudentId(dto.getStudentId());
        response.setQuestionId(dto.getQuestionId());
        response.setNumericRating(dto.getNumericRating());
        response.setTextComment(dto.getTextComment());
        response.setSubmissionDate(dto.getSubmissionDate() != null ? dto.getSubmissionDate() : LocalDateTime.now());
        return response;
    }
}