package it.unimol.assessment_feedback_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unimol.assessment_feedback_service.dto.DetailedFeedbackDTO;
import it.unimol.assessment_feedback_service.service.DetailedFeedbackService;
import it.unimol.assessment_feedback_service.exception.ErrorResponse;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feedback")
@Tag(name = "DetailedFeedback Controller", description = "API per la gestione di DetailedFeedback (Feedback)")
public class DetailedFeedbackController {

    // Costruttore
    private final DetailedFeedbackService feedbackService;

    @Autowired
    public DetailedFeedbackController(DetailedFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    // GET - getFeedbackByAssessmentId
    @GetMapping("/assessment/{id}")
    @Operation(summary = "Ottieni feedback tramite ID della valutazione",
            description = "Recupera tutte le voci di feedback per una specifica valutazione")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voci di feedback trovate con successo",
                    content = @Content(schema = @Schema(implementation = DetailedFeedbackDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "404", description = "Valutazione non trovata",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<DetailedFeedbackDTO>> getFeedbackByAssessmentId(
            @Parameter(description = "Assessment ID", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.getFeedbackByAssessmentId(id));
    }

    // GET - getFeedbackById
    @GetMapping("/{id}")
    @Operation(summary = "Ottieni feedback tramite ID",
            description = "Recupera una specifica voce di feedback tramite il suo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voce di feedback trovata con successo",
                    content = @Content(schema = @Schema(implementation = DetailedFeedbackDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "404", description = "Feedback non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DetailedFeedbackDTO> getFeedbackById(
            @Parameter(description = "Feedback ID", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.getFeedbackById(id));
    }

    // POST - createFeedback
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Crea un feedback",
            description = "Crea una nuova voce di feedback")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Feedback creato con successo",
                    content = @Content(schema = @Schema(implementation = DetailedFeedbackDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dati richiesta non validi",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - Ruolo TEACHER richiesto"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DetailedFeedbackDTO> createFeedback(
            @Parameter(description = "Feedback data", required = true)
            @Valid @RequestBody DetailedFeedbackDTO feedbackDTO) {
        return new ResponseEntity<>(feedbackService.createFeedback(feedbackDTO), HttpStatus.CREATED);
    }

    // PUT - updateFeedback
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Aggiorna un feedback",
            description = "Aggiorna una voce di feedback esistente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedback aggiornato con successo",
                    content = @Content(schema = @Schema(implementation = DetailedFeedbackDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dati richiesta non validi",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - Ruolo TEACHER richiesto"),
            @ApiResponse(responseCode = "404", description = "Feedback non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DetailedFeedbackDTO> updateFeedback(
            @Parameter(description = "Feedback ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Updated feedback data", required = true)
            @Valid @RequestBody DetailedFeedbackDTO feedbackDTO) {
        return ResponseEntity.ok(feedbackService.updateFeedback(id, feedbackDTO));
    }

    // DELETE - deleteFeedback
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Elimina un feedback",
            description = "Elimina una voce di feedback (Solo TEACHER)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Feedback eliminato con successo"),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - Ruolo TEACHER richiesto"),
            @ApiResponse(responseCode = "404", description = "Feedback non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteFeedback(
            @Parameter(description = "Feedback ID", required = true, example = "1")
            @PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
}