package it.unimol.assessment_feedback_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unimol.assessment_feedback_service.dto.TeacherSurveyDTO;
import it.unimol.assessment_feedback_service.enums.SurveyStatus;
// import it.unimol.assessment_feedback_service.exception.ErrorResponse; // TODO: Commentato - implementare gestione errori
import it.unimol.assessment_feedback_service.service.TeacherSurveyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/surveys")
@Tag(name = "Teacher Survey Controller", description = "APIs for managing teacher surveys")
public class TeacherSurveyController {

    private final TeacherSurveyService surveyService;

    // Costruttore per dependency injection (sostituisce @RequiredArgsConstructor di Lombok)
    public TeacherSurveyController(TeacherSurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATIVE')")
    @Operation(summary = "Get all surveys", description = "Retrieves all teacher surveys")
    public ResponseEntity<List<TeacherSurveyDTO>> getAllSurveys() {
        return ResponseEntity.ok(surveyService.getAllSurveys());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATIVE', 'TEACHER')")
    @Operation(summary = "Get survey by ID", description = "Retrieves a specific survey by its ID")
    // TODO: Commentato fino a quando non viene implementata la gestione errori
    // @ApiResponses(value = {
    //         @ApiResponse(responseCode = "200", description = "Survey found"),
    //         @ApiResponse(responseCode = "404", description = "Survey not found",
    //                 content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    // })
    public ResponseEntity<TeacherSurveyDTO> getSurveyById(@PathVariable Long id) {
        return ResponseEntity.ok(surveyService.getSurveyById(id));
    }

    @GetMapping("/course/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATIVE', 'TEACHER')")
    @Operation(summary = "Get surveys by course", description = "Retrieves all surveys for a specific course")
    public ResponseEntity<List<TeacherSurveyDTO>> getSurveysByCourse(@PathVariable Long id) {
        return ResponseEntity.ok(surveyService.getSurveysByCourse(id));
    }

    @GetMapping("/teacher/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATIVE', 'TEACHER')")
    @Operation(summary = "Get surveys by teacher", description = "Retrieves all surveys for a specific teacher")
    public ResponseEntity<List<TeacherSurveyDTO>> getSurveysByTeacher(@PathVariable Long id) {
        return ResponseEntity.ok(surveyService.getSurveysByTeacher(id));
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get available surveys", description = "Retrieves all active surveys available for students")
    public ResponseEntity<List<TeacherSurveyDTO>> getAvailableSurveys() {
        return ResponseEntity.ok(surveyService.getActiveSurveys());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATIVE')")
    @Operation(summary = "Create survey", description = "Creates a new teacher survey")
    public ResponseEntity<TeacherSurveyDTO> createSurvey(@Valid @RequestBody TeacherSurveyDTO surveyDTO) {
        return new ResponseEntity<>(surveyService.createSurvey(surveyDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATIVE')")
    @Operation(summary = "Update survey", description = "Updates an existing teacher survey")
    public ResponseEntity<TeacherSurveyDTO> updateSurvey(@PathVariable Long id,
                                                         @Valid @RequestBody TeacherSurveyDTO surveyDTO) {
        return ResponseEntity.ok(surveyService.updateSurvey(id, surveyDTO));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMINISTRATIVE')")
    @Operation(summary = "Change survey status", description = "Changes the status of an existing survey (ACTIVE/CLOSED)")
    public ResponseEntity<TeacherSurveyDTO> changeSurveyStatus(@PathVariable Long id,
                                                               @RequestParam SurveyStatus status) {
        return ResponseEntity.ok(surveyService.changeSurveyStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATIVE')")
    @Operation(summary = "Delete survey", description = "Deletes a teacher survey")
    public ResponseEntity<Void> deleteSurvey(@PathVariable Long id) {
        surveyService.deleteSurvey(id);
        return ResponseEntity.noContent().build();
    }
}