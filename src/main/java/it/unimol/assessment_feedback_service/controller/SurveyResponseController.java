package it.unimol.assessment_feedback_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unimol.assessment_feedback_service.dto.SurveyResponseDTO;
import it.unimol.assessment_feedback_service.service.SurveyResponseService;
import it.unimol.assessment_feedback_service.exception.ErrorResponse;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/surveys")
@Tag(name = "SurveyResponse Controller", description = "API per la gestione di SurveyResponse (Risposte Questionario)")
public class SurveyResponseController {

    // Costruttore
    private final SurveyResponseService responseService;

    @Autowired
    public SurveyResponseController(SurveyResponseService responseService) {
        this.responseService = responseService;
    }

    // GET - getResponsesBySurveyId
    @GetMapping("/{id}/responses")
    @PreAuthorize("hasAnyRole('ADMINISTRATIVE', 'TEACHER')")
    @Operation(summary = "Ottieni risposte tramite ID questionario",
            description = "Recupera tutte le risposte per un questionario specifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Risposte trovate con successo",
                    content = @Content(schema = @Schema(implementation = SurveyResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description =
                    "Accesso vietato - Ruolo ADMINISTRATIVE o TEACHER richiesto"),
            @ApiResponse(responseCode = "404", description = "Questionario non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<SurveyResponseDTO>> getResponsesBySurveyId(@PathVariable Long id) {
        return ResponseEntity.ok(responseService.getResponsesBySurveyId(id));
    }

    // GET - getSurveyComments
    @GetMapping("/{id}/comments")
    @PreAuthorize("hasAnyRole('ADMINISTRATIVE', 'TEACHER')")
    @Operation(summary = "Ottieni commenti del questionario",
            description = "Recupera tutti i commenti per un questionario specifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commenti trovati con successo",
                    content = @Content(schema = @Schema(implementation = SurveyResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description =
                    "Accesso vietato - Ruolo ADMINISTRATIVE o TEACHER richiesto"),
            @ApiResponse(responseCode = "404", description = "Questionario non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<SurveyResponseDTO>> getSurveyComments(@PathVariable Long id) {
        return ResponseEntity.ok(responseService.getSurveyComments(id));
    }

    // GET - getSurveyResults
    @GetMapping("/{id}/results")
    @PreAuthorize("hasAnyRole('ADMINISTRATIVE', 'TEACHER')")
    @Operation(summary = "Ottieni risultati del questionario",
            description = "Recupera risultati aggregati per un questionario specifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Risultati del questionario trovati con successo",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description =
                    "Accesso vietato - Ruolo ADMINISTRATIVE o TEACHER richiesto"),
            @ApiResponse(responseCode = "404", description = "Questionario non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<Long, Double>> getSurveyResults(@PathVariable Long id) {
        return ResponseEntity.ok(responseService.getSurveyResults(id));
    }

    // POST - submitSurveyResponses
    @PostMapping("/{id}/responses")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Invia le risposte del questionario",
            description = "Invia più risposte per un questionario specifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Risposte del questionario inviate con successo",
                    content = @Content(schema = @Schema(implementation = SurveyResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dati richiesta non validi",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - Ruolo STUDENT richiesto"),
            @ApiResponse(responseCode = "404", description = "Questionario non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<SurveyResponseDTO>> submitSurveyResponses(
            @PathVariable Long id,
            @Valid @RequestBody List<SurveyResponseDTO> responseDTOs) {
        return new ResponseEntity<>(responseService.submitSurveyResponses(id, responseDTOs), HttpStatus.CREATED);
    }

    // POST - createSurveyResponse
    @PostMapping("/response")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Crea una singola risposta",
            description = "Crea una singola risposta al questionario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Risposta al questionario creata con successo",
                    content = @Content(schema = @Schema(implementation = SurveyResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dati richiesta non validi",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - Ruolo STUDENT richiesto"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SurveyResponseDTO> createSurveyResponse(@Valid @RequestBody SurveyResponseDTO responseDTO) {
        return new ResponseEntity<>(responseService.createResponse(responseDTO), HttpStatus.CREATED);
    }
}