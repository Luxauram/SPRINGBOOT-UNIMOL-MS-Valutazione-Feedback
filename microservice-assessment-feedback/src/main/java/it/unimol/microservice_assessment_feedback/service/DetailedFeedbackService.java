package it.unimol.microservice_assessment_feedback.service;

import it.unimol.microservice_assessment_feedback.dto.DetailedFeedbackDTO;
import it.unimol.microservice_assessment_feedback.messaging.publishers.FeedbackMessageService;
import it.unimol.microservice_assessment_feedback.model.Assessment;
import it.unimol.microservice_assessment_feedback.model.DetailedFeedback;
import it.unimol.microservice_assessment_feedback.common.exception.ResourceNotFoundException;
import it.unimol.microservice_assessment_feedback.repository.AssessmentRepository;
import it.unimol.microservice_assessment_feedback.repository.DetailedFeedbackRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DetailedFeedbackService {

    private static final Logger logger = LoggerFactory.getLogger(DetailedFeedbackService.class);

    private final DetailedFeedbackRepository feedbackRepository;
    private final AssessmentRepository assessmentRepository;
    private final FeedbackMessageService feedbackMessageService;


    public DetailedFeedbackService(DetailedFeedbackRepository feedbackRepository,
                                   AssessmentRepository assessmentRepository,
                                   FeedbackMessageService feedbackMessageService) {
        this.feedbackRepository = feedbackRepository;
        this.assessmentRepository = assessmentRepository;
        this.feedbackMessageService = feedbackMessageService;
    }

    public List<DetailedFeedbackDTO> getAllFeedback() {
        logger.debug("Recupero di tutti i feedback");
        List<DetailedFeedback> feedbacks = feedbackRepository.findAll();
        logger.debug("Trovati {} feedback totali", feedbacks.size());
        return feedbacks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DetailedFeedbackDTO> getFeedbackByAssessmentId(String assessmentId) {
        logger.debug("Retrieving feedback for assessment ID: {}", assessmentId);
        return feedbackRepository.findByAssessmentId(assessmentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public DetailedFeedbackDTO getFeedbackById(String id) {
        logger.debug("Retrieving feedback with ID: {}", id);
        DetailedFeedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback non trovato con id: " + id));
        return convertToDTO(feedback);
    }

    public List<DetailedFeedbackDTO> getFeedbackByStudentId(String studentId) {
        logger.debug("Retrieving all feedback for student ID: {}", studentId);
        return feedbackRepository.findByStudentId(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DetailedFeedbackDTO createFeedback(DetailedFeedbackDTO feedbackDTO) {
        logger.info("Creating new feedback for assessment ID: {}", feedbackDTO.getAssessmentId());

        Assessment assessment = assessmentRepository.findById(feedbackDTO.getAssessmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Valutazione non trovata con id: " + feedbackDTO.getAssessmentId()));

        DetailedFeedback feedback = convertToEntity(feedbackDTO);
        feedback.setAssessment(assessment);

        DetailedFeedback savedFeedback = feedbackRepository.save(feedback);
        DetailedFeedbackDTO resultDTO = convertToDTO(savedFeedback);

        try {
            feedbackMessageService.publishFeedbackCreated(resultDTO);
            logger.info("Feedback created event published for feedback ID: {}", resultDTO.getId());
        } catch (Exception e) {
            logger.error("Failed to publish feedback created event for feedback ID: {}", resultDTO.getId(), e);
        }

        return resultDTO;
    }

    @Transactional
    public DetailedFeedbackDTO updateFeedback(String feedbackId, DetailedFeedbackDTO feedbackDTO) {
        logger.info("Updating feedback with ID: {}", feedbackId);
        logger.debug("Received feedbackDTO: {}", feedbackDTO);

        try {
            logger.debug("Searching for feedback with ID: {}", feedbackId);
            DetailedFeedback existingFeedback = feedbackRepository.findById(feedbackId)
                    .orElseThrow(() -> {
                        logger.error("Feedback not found with ID: {}", feedbackId);
                        return new ResourceNotFoundException("Feedback non trovato con id: " + feedbackId);
                    });

            logger.info("Found existing feedback with ID: {} for assessment: {}",
                    feedbackId, existingFeedback.getAssessment().getId());

            logger.debug("Current values - Text: {}, Category: {}, Strengths: {}, ImprovementAreas: {}",
                    existingFeedback.getFeedbackText(),
                    existingFeedback.getCategory(),
                    existingFeedback.getStrengths(),
                    existingFeedback.getImprovementAreas());

            logger.debug("New values - Text: {}, Category: {}, Strengths: {}, ImprovementAreas: {}",
                    feedbackDTO.getFeedbackText(),
                    feedbackDTO.getCategory(),
                    feedbackDTO.getStrengths(),
                    feedbackDTO.getImprovementAreas());

            existingFeedback.setFeedbackText(feedbackDTO.getFeedbackText());
            existingFeedback.setCategory(feedbackDTO.getCategory());
            existingFeedback.setStrengths(feedbackDTO.getStrengths());
            existingFeedback.setImprovementAreas(feedbackDTO.getImprovementAreas());

            logger.debug("About to save updated feedback with ID: {}", existingFeedback.getId());

            DetailedFeedback updatedFeedback = feedbackRepository.save(existingFeedback);
            logger.debug("Feedback saved successfully with ID: {}", updatedFeedback.getId());

            DetailedFeedbackDTO resultDTO = convertToDTO(updatedFeedback);
            logger.debug("Converted to DTO: {}", resultDTO);

            logger.info("Feedback updated successfully with ID: {}", feedbackId);

            try {
                feedbackMessageService.publishFeedbackUpdated(resultDTO);
                logger.info("Feedback updated event published for feedback ID: {}", resultDTO.getId());
            } catch (Exception e) {
                logger.error("Failed to publish feedback updated event for feedback ID: {}", resultDTO.getId(), e);
            }

            return resultDTO;

        } catch (Exception e) {
            logger.error("Error updating feedback with ID: {}", feedbackId, e);
            throw e;
        }
    }

    @Transactional
    public void deleteFeedback(String id) {
        logger.info("Deleting feedback with ID: {}", id);

        if (!feedbackRepository.existsById(id)) {
            throw new ResourceNotFoundException("Feedback non trovato con id: " + id);
        }

        feedbackRepository.deleteById(id);
        logger.info("Feedback deleted successfully with ID: {}", id);

        try {
            feedbackMessageService.publishFeedbackDeleted(id);
            logger.info("Feedback deleted event published for feedback ID: {}", id);
        } catch (Exception e) {
            logger.error("Failed to publish feedback deleted event for feedback ID: {}", id, e);
        }
    }

    private DetailedFeedbackDTO convertToDTO(DetailedFeedback feedback) {
        DetailedFeedbackDTO dto = new DetailedFeedbackDTO();
        dto.setId(feedback.getId());
        dto.setAssessmentId(feedback.getAssessment().getId());
        dto.setFeedbackText(feedback.getFeedbackText());
        dto.setCategory(feedback.getCategory());
        dto.setStrengths(feedback.getStrengths());
        dto.setImprovementAreas(feedback.getImprovementAreas());
        return dto;
    }

    private DetailedFeedback convertToEntity(DetailedFeedbackDTO dto) {
        DetailedFeedback feedback = new DetailedFeedback();
        feedback.setId(dto.getId());
        feedback.setFeedbackText(dto.getFeedbackText());
        feedback.setCategory(dto.getCategory());
        feedback.setStrengths(dto.getStrengths());
        feedback.setImprovementAreas(dto.getImprovementAreas());
        return feedback;
    }
}