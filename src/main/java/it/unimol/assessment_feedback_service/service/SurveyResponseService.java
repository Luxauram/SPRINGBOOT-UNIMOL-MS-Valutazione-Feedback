package it.unimol.assessment_feedback_service.service;

import it.unimol.assessment_feedback_service.dto.SurveyResponseDTO;
import it.unimol.assessment_feedback_service.model.SurveyResponse;
import it.unimol.assessment_feedback_service.model.TeacherSurvey;
import it.unimol.assessment_feedback_service.enums.SurveyStatus;
import it.unimol.assessment_feedback_service.exception.ResourceNotFoundException;
import it.unimol.assessment_feedback_service.exception.SurveyClosedException;
import it.unimol.assessment_feedback_service.repository.SurveyResponseRepository;
import it.unimol.assessment_feedback_service.repository.TeacherSurveyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SurveyResponseService {

    private final SurveyResponseRepository responseRepository;
    private final TeacherSurveyRepository surveyRepository;

    public SurveyResponseService(SurveyResponseRepository responseRepository,
                                 TeacherSurveyRepository surveyRepository) {
        this.responseRepository = responseRepository;
        this.surveyRepository = surveyRepository;
    }

    public List<SurveyResponseDTO> getResponsesBySurveyId(Long surveyId) {
        return responseRepository.findBySurveyId(surveyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<SurveyResponseDTO> getSurveyComments(Long surveyId) {
        return responseRepository.findAllWithCommentsForSurvey(surveyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Map<Long, Double> getSurveyResults(Long surveyId) {
        List<SurveyResponse> responses = responseRepository.findBySurveyId(surveyId);

        return responses.stream()
                .filter(response -> response.getNumericRating() != null)
                .collect(Collectors.groupingBy(
                        SurveyResponse::getQuestionId,
                        Collectors.averagingDouble(SurveyResponse::getNumericRating)
                ));
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
        return convertToDTO(savedResponse);
    }

    @Transactional
    public List<SurveyResponseDTO> submitSurveyResponses(Long surveyId, List<SurveyResponseDTO> responseDTOs) {
         TeacherSurvey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Questionario non trovato con id: " + surveyId));

        if (survey.getStatus() != SurveyStatus.ACTIVE) {
             throw new SurveyClosedException("Non è possibile inviare risposte ad un Questionario chiuso");
         }

        List<SurveyResponse> responses = responseDTOs.stream()
                .map(dto -> {
                    SurveyResponse response = convertToEntity(dto);
                    response.setSurvey(survey);
                    response.setSubmissionDate(LocalDateTime.now());
                    return response;
                })
                .collect(Collectors.toList());

        List<SurveyResponse> savedResponses = responseRepository.saveAll(responses);
        return savedResponses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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