package it.unimol.assessment_feedback_service.service;

import it.unimol.assessment_feedback_service.dto.DetailedFeedbackDTO;
import it.unimol.assessment_feedback_service.model.Assessment;
import it.unimol.assessment_feedback_service.model.DetailedFeedback;
// import it.unimol.assessment_feedback_service.exception.ResourceNotFoundException;
import it.unimol.assessment_feedback_service.repository.AssessmentRepository;
import it.unimol.assessment_feedback_service.repository.DetailedFeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DetailedFeedbackService {

    private final DetailedFeedbackRepository feedbackRepository;
    private final AssessmentRepository assessmentRepository;

    public DetailedFeedbackService(DetailedFeedbackRepository feedbackRepository,
                                   AssessmentRepository assessmentRepository) {
        this.feedbackRepository = feedbackRepository;
        this.assessmentRepository = assessmentRepository;
    }

    public List<DetailedFeedbackDTO> getFeedbackByAssessmentId(Long assessmentId) {
        return feedbackRepository.findByAssessmentId(assessmentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public DetailedFeedbackDTO getFeedbackById(Long id) {
        DetailedFeedback feedback = feedbackRepository.findById(id).get();
        // TODO: Error Handling
        // DetailedFeedback feedback = feedbackRepository.findById(id)
        //         .orElseThrow(() -> new ResourceNotFoundException("Feedback non trovato con id: " + id));
        return convertToDTO(feedback);
    }

    @Transactional
    public DetailedFeedbackDTO createFeedback(DetailedFeedbackDTO feedbackDTO) {
        Assessment assessment = assessmentRepository.findById(feedbackDTO.getAssessmentId()).get();
        // TODO: Error Handling
        // Assessment assessment = assessmentRepository.findById(feedbackDTO.getAssessmentId())
        //         .orElseThrow(() -> new ResourceNotFoundException("Valutazione non trovata con id: " + feedbackDTO.getAssessmentId()));

        DetailedFeedback feedback = convertToEntity(feedbackDTO);
        feedback.setAssessment(assessment);

        DetailedFeedback savedFeedback = feedbackRepository.save(feedback);
        return convertToDTO(savedFeedback);
    }

    @Transactional
    public DetailedFeedbackDTO updateFeedback(Long id, DetailedFeedbackDTO feedbackDTO) {
        DetailedFeedback existingFeedback = feedbackRepository.findById(id).get();
        // TODO: Error Handling
        // DetailedFeedback existingFeedback = feedbackRepository.findById(id)
        //         .orElseThrow(() -> new ResourceNotFoundException("Feedback non trovato con id: " + id));

        existingFeedback.setFeedbackText(feedbackDTO.getFeedbackText());
        existingFeedback.setCategory(feedbackDTO.getCategory());
        existingFeedback.setStrengths(feedbackDTO.getStrengths());
        existingFeedback.setImprovementAreas(feedbackDTO.getImprovementAreas());

        DetailedFeedback updatedFeedback = feedbackRepository.save(existingFeedback);
        return convertToDTO(updatedFeedback);
    }

    @Transactional
    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
        // TODO: Error Handling
        // if (!feedbackRepository.existsById(id)) {
        //     throw new ResourceNotFoundException("Feedback non trovato con id: " + id);
        // }
        // feedbackRepository.deleteById(id);
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