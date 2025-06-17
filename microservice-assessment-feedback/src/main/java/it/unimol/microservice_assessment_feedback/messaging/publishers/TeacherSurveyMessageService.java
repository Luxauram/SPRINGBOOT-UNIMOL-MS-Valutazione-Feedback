package it.unimol.microservice_assessment_feedback.messaging.publishers;

import it.unimol.microservice_assessment_feedback.dto.TeacherSurveyDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TeacherSurveyMessageService extends BaseEventPublisher {

    public void publishSurveyCompleted(TeacherSurveyDTO survey) {
        Map<String, Object> message = createSurveyMessage(survey, "SURVEY_COMPLETED");
        publishMessage("survey.completed", message, "survey", survey.getId());
    }

    public void publishSurveyResultsRequested(String surveyId, String requestedBy) {
        Map<String, Object> message = new HashMap<>();
        addBaseMessageFields(message, "SURVEY_RESULTS_REQUESTED");
        message.put("surveyId", surveyId);
        message.put("requestedBy", requestedBy);
        publishMessage("survey.results.requested", message, "survey", surveyId);
    }

    public void publishSurveyCommentsRequested(String surveyId, String requestedBy) {
        Map<String, Object> message = new HashMap<>();
        addBaseMessageFields(message, "SURVEY_COMMENTS_REQUESTED");
        message.put("surveyId", surveyId);
        message.put("requestedBy", requestedBy);
        publishMessage("survey.comments.requested", message, "survey", surveyId);
    }

    private Map<String, Object> createSurveyMessage(TeacherSurveyDTO survey, String eventType) {
        Map<String, Object> message = new HashMap<>();
        addBaseMessageFields(message, eventType);
        message.put("surveyId", survey.getId());
        message.put("courseId", survey.getCourseId());
        message.put("teacherId", survey.getTeacherId());
        message.put("academicYear", survey.getAcademicYear());
        message.put("semester", survey.getSemester());
        message.put("status", survey.getStatus().toString());
        message.put("creationDate", survey.getCreationDate());
        message.put("closingDate", survey.getClosingDate());
        return message;
    }
}