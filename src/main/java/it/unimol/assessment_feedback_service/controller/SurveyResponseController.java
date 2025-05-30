package it.unimol.assessment_feedback_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unimol.assessment_feedback_service.dto.SurveyResponseDTO;
// import it.unimol.assessment_feedback_service.exception.ErrorResponse; // TODO: Commentato - implementare gestione errori
import it.unimol.assessment_feedback_service.service.SurveyResponseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/surveys")
@Tag(name = "Survey Response Controller", description = "APIs for managing survey responses")
public class SurveyResponseController {

    private final SurveyResponseService responseService;

    // Costruttore per dependency injection (sostituisce @RequiredArgsConstructor di Lombok)
    public SurveyResponseController(SurveyResponseService responseService) {
        this.responseService = responseService;
    }

    @GetMapping("/{id}/responses")
    @PreAuthorize("hasAnyRole('ADMINISTRATIVE', 'TEACHER')")
    @Operation(summary = "Get responses by survey ID", description = "Retrieves all responses for a specific survey")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Responses found")
            // TODO: Commentato fino a quando non viene implementata la gestione errori
            // @ApiResponse(responseCode = "404", description = "Survey not found",
            //         content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<SurveyResponseDTO>> getResponsesBySurveyId(@PathVariable Long id) {
        return ResponseEntity.ok(responseService.getResponsesBySurveyId(id));
    }

    @GetMapping("/{id}/comments")
    @PreAuthorize("hasAnyRole('ADMINISTRATIVE', 'TEACHER')")
    @Operation(summary = "Get survey comments", description = "Retrieves all comments for a specific survey")
    public ResponseEntity<List<SurveyResponseDTO>> getSurveyComments(@PathVariable Long id) {
        return ResponseEntity.ok(responseService.getSurveyComments(id));
    }

    @GetMapping("/{id}/results")
    @PreAuthorize("hasAnyRole('ADMINISTRATIVE', 'TEACHER')")
    @Operation(summary = "Get survey results", description = "Retrieves aggregated results for a specific survey")
    public ResponseEntity<Map<Long, Double>> getSurveyResults(@PathVariable Long id) {
        return ResponseEntity.ok(responseService.getSurveyResults(id));
    }

    @PostMapping("/{id}/responses")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Submit survey responses", description = "Submits multiple responses for a specific survey")
    public ResponseEntity<List<SurveyResponseDTO>> submitSurveyResponses(
            @PathVariable Long id,
            @Valid @RequestBody List<SurveyResponseDTO> responseDTOs) {
        return new ResponseEntity<>(responseService.submitSurveyResponses(id, responseDTOs), HttpStatus.CREATED);
    }

    @PostMapping("/response")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Create single response", description = "Creates a single survey response")
    public ResponseEntity<SurveyResponseDTO> createSurveyResponse(@Valid @RequestBody SurveyResponseDTO responseDTO) {
        return new ResponseEntity<>(responseService.createResponse(responseDTO), HttpStatus.CREATED);
    }
}