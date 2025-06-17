package it.unimol.microservice_assessment_feedback.messaging.publishers;

import it.unimol.microservice_assessment_feedback.dto.DetailedFeedbackDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FeedbackMessageService extends BaseEventPublisher{

    public void publishFeedbackCreated(DetailedFeedbackDTO feedback) {
        Map<String, Object> message = createFeedbackMessage(feedback, "FEEDBACK_CREATED");
        publishMessage("feedback.created", message, "feedback", feedback.getId());
    }

    public void publishFeedbackUpdated(DetailedFeedbackDTO feedback) {
        Map<String, Object> message = createFeedbackMessage(feedback, "FEEDBACK_UPDATED");
        publishMessage("feedback.updated", message, "feedback", feedback.getId());
    }

    public void publishFeedbackDeleted(String feedbackId) {
        Map<String, Object> message = new HashMap<>();
        addBaseMessageFields(message, "FEEDBACK_DELETED");
        message.put("feedbackId", feedbackId);
        publishMessage("feedback.deleted", message, "feedback", feedbackId);
    }

    private Map<String, Object> createFeedbackMessage(DetailedFeedbackDTO feedback, String eventType) {
        Map<String, Object> message = new HashMap<>();
        addBaseMessageFields(message, eventType);
        message.put("feedbackId", feedback.getId());
        message.put("assessmentId", feedback.getAssessmentId());
        message.put("feedbackText", feedback.getFeedbackText());
        message.put("category", feedback.getCategory().toString());
        message.put("strengths", feedback.getStrengths());
        message.put("improvementAreas", feedback.getImprovementAreas());
        return message;
    }
}
