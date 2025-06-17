package it.unimol.microservice_assessment_feedback.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import it.unimol.microservice_assessment_feedback.dto.TeacherSurveyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@Converter
public class ListToJsonConverter implements AttributeConverter<List<TeacherSurveyDTO.SurveyQuestionDTO>, String> {

    private static final Logger logger = LoggerFactory.getLogger(ListToJsonConverter.class);
    private final ObjectMapper objectMapper;

    /**
     * Costruttore. Inizializza ObjectMapper con il modulo per la gestione delle date
     */
    public ListToJsonConverter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    /**
     * Converte una lista di oggetti SurveyQuestionDTO in una stringa JSON per il database.
     *
     * @param attribute La lista di SurveyQuestionDTO da convertire.
     * @return La rappresentazione JSON della lista, o null se la lista è vuota o null.
     */
    @Override
    public String convertToDatabaseColumn(List<TeacherSurveyDTO.SurveyQuestionDTO> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            logger.error("Errore durante la serializzazione della lista di domande in JSON: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Converte una stringa JSON dal database in una lista di oggetti SurveyQuestionDTO.
     *
     * @param dbData La stringa JSON da convertire.
     * @return La lista di SurveyQuestionDTO deserializzata, o null se la stringa è vuota o null.
     */
    @Override
    public List<TeacherSurveyDTO.SurveyQuestionDTO> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, objectMapper.getTypeFactory().constructCollectionType(List.class, TeacherSurveyDTO.SurveyQuestionDTO.class));
        } catch (IOException e) {
            logger.error("Errore durante la deserializzazione della stringa JSON in lista di domande: {}", e.getMessage(), e);
            return null;
        }
    }
}