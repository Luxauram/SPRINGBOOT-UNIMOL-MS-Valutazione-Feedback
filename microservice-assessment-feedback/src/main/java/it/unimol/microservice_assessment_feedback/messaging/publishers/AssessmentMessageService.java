package it.unimol.microservice_assessment_feedback.messaging.publishers;

import it.unimol.microservice_assessment_feedback.dto.AssessmentDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AssessmentMessageService extends BaseEventPublisher {

    public void publishAssessmentCreated(AssessmentDTO assessment) {
        Map<String, Object> message = createAssessmentMessage(assessment, "ASSESSMENT_CREATED");
        publishMessage("assessment.created", message, "assessment", assessment.getId());
    }

    public void publishAssessmentUpdated(AssessmentDTO assessment) {
        Map<String, Object> message = createAssessmentMessage(assessment, "ASSESSMENT_UPDATED");
        publishMessage("assessment.updated", message, "assessment", assessment.getId());
    }

    public void publishAssessmentDeleted(String assessmentId) {
        Map<String, Object> message = new HashMap<>();
        addBaseMessageFields(message, "ASSESSMENT_DELETED");
        message.put("assessmentId", assessmentId);
        publishMessage("assessment.deleted", message, "assessment", assessmentId);
    }

    private Map<String, Object> createAssessmentMessage(AssessmentDTO assessment, String eventType) {
        Map<String, Object> message = new HashMap<>();
        addBaseMessageFields(message, eventType);
        message.put("assessmentId", assessment.getId());
        message.put("referenceId", assessment.getReferenceId());
        message.put("referenceType", assessment.getReferenceType().toString());
        message.put("studentId", assessment.getStudentId());
        message.put("teacherId", assessment.getTeacherId());
        message.put("courseId", assessment.getCourseId());
        message.put("score", assessment.getScore());
        message.put("assessmentDate", assessment.getAssessmentDate());
        message.put("notes", assessment.getNotes());
        return message;
    }
}
