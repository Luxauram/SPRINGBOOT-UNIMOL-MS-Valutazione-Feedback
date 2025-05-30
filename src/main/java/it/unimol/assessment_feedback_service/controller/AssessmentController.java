package it.unimol.assessment_feedback_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unimol.assessment_feedback_service.dto.AssessmentDTO;
import it.unimol.assessment_feedback_service.exception.ErrorResponse;
import it.unimol.assessment_feedback_service.service.AssessmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assessments")
@Tag(name = "Assessment Controller", description = "APIs for managing assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Get all assessments", description = "Retrieves all assessments (accessible to teachers)")
    public ResponseEntity<List<AssessmentDTO>> getAllAssessments() {
        return ResponseEntity.ok(assessmentService.getAllAssessments());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Get assessment by ID", description = "Retrieves a specific assessment by its ID")
    @ApiResponses(value = {
            // @ApiResponse(responseCode = "200", description = "Assessment found")

            @ApiResponse(responseCode = "404", description = "Assessment not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AssessmentDTO> getAssessmentById(@PathVariable Long id) {
        return ResponseEntity.ok(assessmentService.getAssessmentById(id));
    }

    @GetMapping("/assignment/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Get assessments by assignment", description = "Retrieves all assessments for a specific assignment")
    public ResponseEntity<List<AssessmentDTO>> getAssessmentsByAssignment(@PathVariable Long id) {
        return ResponseEntity.ok(assessmentService.getAssessmentsByAssignment(id));
    }

    @GetMapping("/exam/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Get assessments by exam", description = "Retrieves all assessments for a specific exam")
    public ResponseEntity<List<AssessmentDTO>> getAssessmentsByExam(@PathVariable Long id) {
        return ResponseEntity.ok(assessmentService.getAssessmentsByExam(id));
    }

    @GetMapping("/student/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Get assessments by student", description = "Retrieves all assessments for a specific student")
    public ResponseEntity<List<AssessmentDTO>> getAssessmentsByStudent(@PathVariable Long id) {
        return ResponseEntity.ok(assessmentService.getAssessmentsByStudentId(id));
    }

    @GetMapping("/course/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Get assessments by course", description = "Retrieves all assessments for a specific course")
    public ResponseEntity<List<AssessmentDTO>> getAssessmentsByCourse(@PathVariable Long id) {
        return ResponseEntity.ok(assessmentService.getAssessmentsByCourse(id));
    }

    @GetMapping("/personal")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get personal assessments", description = "Retrieves all assessments for the authenticated student")
    public ResponseEntity<List<AssessmentDTO>> getPersonalAssessments() {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/personal/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get personal assessment details", description = "Retrieves details of a specific assessment for the authenticated student")
    public ResponseEntity<AssessmentDTO> getPersonalAssessmentDetails(@PathVariable Long id) {
        return ResponseEntity.ok(assessmentService.getAssessmentById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Create assessment", description = "Creates a new assessment")
    public ResponseEntity<AssessmentDTO> createAssessment(@Valid @RequestBody AssessmentDTO assessmentDTO) {
        return new ResponseEntity<>(assessmentService.createAssessment(assessmentDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Update assessment", description = "Updates an existing assessment")
    public ResponseEntity<AssessmentDTO> updateAssessment(@PathVariable Long id,
                                                          @Valid @RequestBody AssessmentDTO assessmentDTO) {
        return ResponseEntity.ok(assessmentService.updateAssessment(id, assessmentDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Delete assessment", description = "Deletes an assessment")
    public ResponseEntity<Void> deleteAssessment(@PathVariable Long id) {
        assessmentService.deleteAssessment(id);
        return ResponseEntity.noContent().build();
    }
}