package it.unimol.microservice_assessment_feedback.messaging.publishers;

import it.unimol.microservice_assessment_feedback.dto.SurveyResponseDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SurveyResponseMessageService extends BaseEventPublisher {

    public void publishSurveyResponseSubmitted(SurveyResponseDTO response) {
        Map<String, Object> message = createSurveyResponseMessage(response, "SURVEY_RESPONSE_SUBMITTED");
        publishMessage("survey.response.submitted", message, "survey response", response.getId());
    }

    public void publishSurveyResponsesSubmitted(List<SurveyResponseDTO> responses, String surveyId) {
        Map<String, Object> message = createBulkSurveyResponseMessage(responses, surveyId, "SURVEY_RESPONSES_BULK_SUBMITTED");
        publishMessage("survey.responses.bulk.submitted", message, "survey responses", surveyId);
        logger.info("Bulk survey responses submitted event published successfully for survey ID: {} with {} responses",
                surveyId, responses.size());
    }

    private Map<String, Object> createSurveyResponseMessage(SurveyResponseDTO response, String eventType) {
        Map<String, Object> message = new HashMap<>();
        addBaseMessageFields(message, eventType);
        message.put("responseId", response.getId());
        message.put("surveyId", response.getSurveyId());
        message.put("studentId", response.getStudentId());
        message.put("questionId", response.getQuestionId());
        message.put("numericRating", response.getNumericRating());
        message.put("textComment", response.getTextComment());
        message.put("submissionDate", response.getSubmissionDate());
        return message;
    }

    private Map<String, Object> createBulkSurveyResponseMessage(List<SurveyResponseDTO> responses, String surveyId, String eventType) {
        Map<String, Object> message = new HashMap<>();
        addBaseMessageFields(message, eventType);
        message.put("surveyId", surveyId);
        message.put("responseCount", responses.size());
        message.put("responses", responses.stream().map(response -> {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("responseId", response.getId());
            responseData.put("studentId", response.getStudentId());
            responseData.put("questionId", response.getQuestionId());
            responseData.put("numericRating", response.getNumericRating());
            responseData.put("textComment", response.getTextComment());
            responseData.put("submissionDate", response.getSubmissionDate());
            return responseData;
        }).toList());
        return message;
    }
}
