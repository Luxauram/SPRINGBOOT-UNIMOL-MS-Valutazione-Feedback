package it.unimol.assessment_feedback_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unimol.assessment_feedback_service.dto.TeacherSurveyDTO;
import it.unimol.assessment_feedback_service.enums.SurveyStatus;
import it.unimol.assessment_feedback_service.service.TeacherSurveyService;
import it.unimol.assessment_feedback_service.exception.ErrorResponse;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/surveys")
@Tag(name = "TeacherSurvey Controller", description = "API per la gestione di TeacherSurvey")
public class TeacherSurveyController {

    // Costruttore
    private final TeacherSurveyService surveyService;

    @Autowired
    public TeacherSurveyController(TeacherSurveyService surveyService) {
        this.surveyService = surveyService;
    }

    // GET - getAllSurveys
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATIVE')")
    @Operation(summary = "Ottieni tutti i questionari",
            description = "Recupera tutti i questionari degli insegnanti")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questionari trovati con successo",
                    content = @Content(schema = @Schema(implementation = TeacherSurveyDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description =
                    "Accesso vietato - Ruolo ADMINISTRATIVE richiesto"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TeacherSurveyDTO>> getAllSurveys() {
        return ResponseEntity.ok(surveyService.getAllSurveys());
    }

    // GET - getSurveyById
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATIVE', 'TEACHER')")
    @Operation(summary = "Ottieni questionario tramite ID",
            description = "Retrieves a specific survey by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questionario trovato con successo",
                    content = @Content(schema = @Schema(implementation = TeacherSurveyDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description =
                    "Accesso vietato - Ruolo ADMINISTRATIVE o TEACHER richiesto"),
            @ApiResponse(responseCode = "404", description = "Questionario non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TeacherSurveyDTO> getSurveyById(@PathVariable Long id) {
        return ResponseEntity.ok(surveyService.getSurveyById(id));
    }

    // GET - getSurveysByCourse
    @GetMapping("/course/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATIVE', 'TEACHER')")
    @Operation(summary = "Ottieni questionari per corso",
            description = "Recupera tutti i questionari per un corso specifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questionari trovati con successo",
                    content = @Content(schema = @Schema(implementation = TeacherSurveyDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description =
                    "Accesso vietato - Ruolo ADMINISTRATIVE o TEACHER richiesto"),
            @ApiResponse(responseCode = "404", description = "Corso non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TeacherSurveyDTO>> getSurveysByCourse(@PathVariable Long id) {
        return ResponseEntity.ok(surveyService.getSurveysByCourse(id));
    }

    // GET - getSurveysByTeacher
    @GetMapping("/teacher/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATIVE', 'TEACHER')")
    @Operation(summary = "Ottieni questionari per docente",
            description = "Recupera tutti i questionari per un docente specifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questionari trovati con successo",
                    content = @Content(schema = @Schema(implementation = TeacherSurveyDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description =
                    "Accesso vietato - Ruolo ADMINISTRATIVE o TEACHER richiesto"),
            @ApiResponse(responseCode = "404", description = "Docente non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TeacherSurveyDTO>> getSurveysByTeacher(@PathVariable Long id) {
        return ResponseEntity.ok(surveyService.getSurveysByTeacher(id));
    }

    // GET - getAvailableSurveys
    @GetMapping("/available")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Ottieni i questionari disponibili",
            description = "Recupera tutti i questionari attivi disponibili per gli studenti")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Questionari disponibili trovati con successo",
                    content = @Content(schema = @Schema(implementation = TeacherSurveyDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description =
                    "Accesso vietato - Ruolo STUDENT richiesto"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TeacherSurveyDTO>> getAvailableSurveys() {
        return ResponseEntity.ok(surveyService.getActiveSurveys());
    }

    // POST - createSurvey
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATIVE')")
    @Operation(summary = "Crea questionario",
            description = "Crea un nuovo questionario per docente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Questionario creato con successo",
                    content = @Content(schema = @Schema(implementation = TeacherSurveyDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dati richiesta non validi",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description =
                    "Accesso vietato - Ruolo ADMINISTRATIVE richiesto"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TeacherSurveyDTO> createSurvey(@Valid @RequestBody TeacherSurveyDTO surveyDTO) {
        return new ResponseEntity<>(surveyService.createSurvey(surveyDTO), HttpStatus.CREATED);
    }

    // PUT - updateSurvey
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATIVE')")
    @Operation(summary = "Aggiorna questionario",
            description = "Aggiorna un questionario docente esistente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questionario aggiornato con successo",
                    content = @Content(schema = @Schema(implementation = TeacherSurveyDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dati richiesta non validi",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description =
                    "Accesso vietato - Ruolo ADMINISTRATIVE richiesto"),
            @ApiResponse(responseCode = "404", description = "Questionario non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TeacherSurveyDTO> updateSurvey(@PathVariable Long id,
                                                         @Valid @RequestBody TeacherSurveyDTO surveyDTO) {
        return ResponseEntity.ok(surveyService.updateSurvey(id, surveyDTO));
    }

    // PUT - changeSurveyStatus
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMINISTRATIVE')")
    @Operation(summary = "Modifica stato questionari",
            description = "Modifica stato di un questionario esistente (ACTIVE/CLOSED)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Stato del questionario modificato con successo",
                    content = @Content(schema = @Schema(implementation = TeacherSurveyDTO.class))),
            @ApiResponse(responseCode = "400", description = "Valore dello stato non valido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description =
                    "Accesso vietato - Ruolo ADMINISTRATIVE richiesto"),
            @ApiResponse(responseCode = "404", description = "Questionario non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TeacherSurveyDTO> changeSurveyStatus(@PathVariable Long id,
                                                               @RequestParam SurveyStatus status) {
        return ResponseEntity.ok(surveyService.changeSurveyStatus(id, status));
    }

    // DELETE - deleteSurvey
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATIVE')")
    @Operation(summary = "Elimina questionario",
            description = "Elimina un questionario docente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Questionario eliminato con successo"),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description =
                    "Accesso vietato - Ruolo ADMINISTRATIVE richiesto"),
            @ApiResponse(responseCode = "404", description = "Questionario non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteSurvey(@PathVariable Long id) {
        surveyService.deleteSurvey(id);
        return ResponseEntity.noContent().build();
    }
}