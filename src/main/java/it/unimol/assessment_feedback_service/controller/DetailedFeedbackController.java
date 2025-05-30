package it.unimol.assessment_feedback_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unimol.assessment_feedback_service.dto.DetailedFeedbackDTO;
// import it.unimol.assessment_feedback_service.exception.ErrorResponse; // COMMENTATO: ErrorResponse non ancora implementata
import it.unimol.assessment_feedback_service.service.DetailedFeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feedback")
@Tag(name = "Detailed Feedback Controller", description = "APIs for managing detailed feedback")
public class DetailedFeedbackController {

    private final DetailedFeedbackService feedbackService;

    public DetailedFeedbackController(DetailedFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/assessment/{id}")
    @Operation(summary = "Get feedback by assessment ID",
            description = "Retrieves all feedback entries for a specific assessment")
    /*@ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedback entries found"),
            @ApiResponse(responseCode = "404", description = "Assessment not found"
                    // content = @Content(schema = @Schema(implementation = ErrorResponse.class)) // COMMENTATO: ErrorResponse non implementata
            )
    })*/
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedback entries found successfully",
                    content = @Content(schema = @Schema(implementation = DetailedFeedbackDTO.class))),
            @ApiResponse(responseCode = "404", description = "Assessment not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<DetailedFeedbackDTO>> getFeedbackByAssessmentId(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.getFeedbackByAssessmentId(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get feedback by ID",
            description = "Retrieves a specific feedback entry by its ID")
    /*@ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedback entry found"),
            @ApiResponse(responseCode = "404", description = "Feedback not found"
                    // content = @Content(schema = @Schema(implementation = ErrorResponse.class)) // COMMENTATO: ErrorResponse non implementata
            )
    })*/
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedback entry found successfully",
                    content = @Content(schema = @Schema(implementation = DetailedFeedbackDTO.class))),
            @ApiResponse(responseCode = "404", description = "Feedback not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DetailedFeedbackDTO> getFeedbackById(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.getFeedbackById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Create feedback",
            description = "Creates a new detailed feedback entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Feedback created successfully",
                    content = @Content(schema = @Schema(implementation = DetailedFeedbackDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Teacher role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DetailedFeedbackDTO> createFeedback(@Valid @RequestBody DetailedFeedbackDTO feedbackDTO) {
        return new ResponseEntity<>(feedbackService.createFeedback(feedbackDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Update feedback",
            description = "Updates an existing feedback entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedback updated successfully",
                    content = @Content(schema = @Schema(implementation = DetailedFeedbackDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Teacher role required"),
            @ApiResponse(responseCode = "404", description = "Feedback not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DetailedFeedbackDTO> updateFeedback(@PathVariable Long id,
                                                              @Valid @RequestBody DetailedFeedbackDTO feedbackDTO) {
        return ResponseEntity.ok(feedbackService.updateFeedback(id, feedbackDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Delete detailed feedback",
            description = "Deletes a detailed feedback entry (Teacher only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Feedback deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Teacher role required"),
            @ApiResponse(responseCode = "404", description = "Feedback not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
}