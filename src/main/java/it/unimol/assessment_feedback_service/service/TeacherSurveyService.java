package it.unimol.assessment_feedback_service.service;

import it.unimol.assessment_feedback_service.dto.TeacherSurveyDTO;
import it.unimol.assessment_feedback_service.model.TeacherSurvey;
import it.unimol.assessment_feedback_service.enums.SurveyStatus;
import it.unimol.assessment_feedback_service.exception.ResourceNotFoundException;
import it.unimol.assessment_feedback_service.repository.TeacherSurveyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherSurveyService {

    private final TeacherSurveyRepository surveyRepository;

    public TeacherSurveyService(TeacherSurveyRepository surveyRepository) {
        this.surveyRepository = surveyRepository;
    }

    public List<TeacherSurveyDTO> getAllSurveys() {
        return surveyRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TeacherSurveyDTO getSurveyById(Long id) {
        TeacherSurvey survey = surveyRepository.findById(id)
                 .orElseThrow(() -> new ResourceNotFoundException("Questionario non trovato con id: " + id));
        return survey != null ? convertToDTO(survey) : null;
    }

    public List<TeacherSurveyDTO> getSurveysByCourse(Long courseId) {
        return surveyRepository.findByCourseId(courseId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TeacherSurveyDTO> getSurveysByTeacher(Long teacherId) {
        return surveyRepository.findByTeacherId(teacherId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TeacherSurveyDTO> getActiveSurveys() {
        return surveyRepository.findByStatus(SurveyStatus.ACTIVE).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TeacherSurveyDTO createSurvey(TeacherSurveyDTO surveyDTO) {
        if (surveyRepository.existsByTeacherIdAndCourseIdAndAcademicYearAndSemester(
                surveyDTO.getTeacherId(), surveyDTO.getCourseId(),
                surveyDTO.getAcademicYear(), surveyDTO.getSemester())) {
            throw new IllegalArgumentException("Esiste già un questionario per questo docente, corso e periodo");
        }

        TeacherSurvey survey = convertToEntity(surveyDTO);
        survey.setStatus(SurveyStatus.ACTIVE);
        survey.setCreationDate(LocalDateTime.now());

        TeacherSurvey savedSurvey = surveyRepository.save(survey);
        return convertToDTO(savedSurvey);
    }

    @Transactional
    public TeacherSurveyDTO updateSurvey(Long id, TeacherSurveyDTO surveyDTO) {
        TeacherSurvey existingSurvey = surveyRepository.findById(id)
                 .orElseThrow(() -> new ResourceNotFoundException("Questionario non trovato con id: " + id));

        if (existingSurvey != null) {
            existingSurvey.setAcademicYear(surveyDTO.getAcademicYear());
            existingSurvey.setSemester(surveyDTO.getSemester());

            TeacherSurvey updatedSurvey = surveyRepository.save(existingSurvey);
            return convertToDTO(updatedSurvey);
        }
        return null;
    }

    @Transactional
    public TeacherSurveyDTO changeSurveyStatus(Long id, SurveyStatus newStatus) {
        TeacherSurvey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Questionario non trovato con id: " + id));

        if (survey != null) {
            survey.setStatus(newStatus);

            if (newStatus == SurveyStatus.CLOSED) {
                survey.setClosingDate(LocalDateTime.now());
            }

            TeacherSurvey updatedSurvey = surveyRepository.save(survey);
            return convertToDTO(updatedSurvey);
        }
        return null;
    }

    @Transactional
    public void deleteSurvey(Long id) {
         if (!surveyRepository.existsById(id)) {
             throw new ResourceNotFoundException("Questionario non trovato con id: " + id);
         }
        surveyRepository.deleteById(id);
    }

    private TeacherSurveyDTO convertToDTO(TeacherSurvey survey) {
        TeacherSurveyDTO dto = new TeacherSurveyDTO();
        dto.setId(survey.getId());
        dto.setCourseId(survey.getCourseId());
        dto.setTeacherId(survey.getTeacherId());
        dto.setAcademicYear(survey.getAcademicYear());
        dto.setSemester(survey.getSemester());
        dto.setStatus(survey.getStatus());
        dto.setCreationDate(survey.getCreationDate());
        dto.setClosingDate(survey.getClosingDate());
        return dto;
    }

    private TeacherSurvey convertToEntity(TeacherSurveyDTO dto) {
        TeacherSurvey survey = new TeacherSurvey();
        survey.setId(dto.getId());
        survey.setCourseId(dto.getCourseId());
        survey.setTeacherId(dto.getTeacherId());
        survey.setAcademicYear(dto.getAcademicYear());
        survey.setSemester(dto.getSemester());
        survey.setStatus(dto.getStatus() != null ? dto.getStatus() : SurveyStatus.ACTIVE);
        survey.setCreationDate(dto.getCreationDate() != null ? dto.getCreationDate() : LocalDateTime.now());
        survey.setClosingDate(dto.getClosingDate());
        return survey;
    }
}