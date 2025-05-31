package it.unimol.assessment_feedback_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unimol.assessment_feedback_service.dto.AssessmentDTO;
import it.unimol.assessment_feedback_service.service.AssessmentService;
import it.unimol.assessment_feedback_service.exception.ErrorResponse;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assessments")
@Tag(name = "Assessment Controller", description = "API per la gestione di Assessment (Valutazioni)")
public class AssessmentController {

    // Costruttore
    private final AssessmentService assessmentService;

    @Autowired
    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    // GET - getAllAssessments
    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Ottieni tutte le valutazioni",
            description = "Ottieni tutte le valutazioni (accessibili a TEACHER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Valutazioni trovate con successo",
                    content = @Content(schema = @Schema(implementation = AssessmentDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - ruolo TEACHER richiesto"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AssessmentDTO>> getAllAssessments() {
        return ResponseEntity.ok(assessmentService.getAllAssessments());
    }

    // GET - getAssessmentById
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Ottieni valutazione tramite ID",
            description = "Ottiene una specifica valutazione tramite il suo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Valutazione trovata con successo",
                    content = @Content(schema = @Schema(implementation = AssessmentDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - ruolo TEACHER richiesto"),
            @ApiResponse(responseCode = "404", description = "Valutazione non trovata",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AssessmentDTO> getAssessmentById(@PathVariable Long id) {
        return ResponseEntity.ok(assessmentService.getAssessmentById(id));
    }

    // GET - getAssessmentsByAssignment
    @GetMapping("/assignment/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Ottieni valutazioni per compito",
            description = "Ottiene tutte le valutazioni per uno specifico compito")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Valutazioni trovate con successo",
                    content = @Content(schema = @Schema(implementation = AssessmentDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - ruolo TEACHER richiesto"),
            @ApiResponse(responseCode = "404", description = "Compito non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AssessmentDTO>> getAssessmentsByAssignment(@PathVariable Long id) {
        return ResponseEntity.ok(assessmentService.getAssessmentsByAssignment(id));
    }

    // GET - getAssessmentsByExam
    @GetMapping("/exam/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Ottieni valutazioni per esame",
            description = "Ottiene tutte le valutazioni per uno specifico esame")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Valutazioni trovate con successo",
                    content = @Content(schema = @Schema(implementation = AssessmentDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - ruolo TEACHER richiesto"),
            @ApiResponse(responseCode = "404", description = "Esame non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AssessmentDTO>> getAssessmentsByExam(@PathVariable Long id) {
        return ResponseEntity.ok(assessmentService.getAssessmentsByExam(id));
    }

    // GET - getAssessmentsByStudent
    @GetMapping("/student/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Ottieni valutazioni per studente",
            description = "Ottiene tutte le valutazioni per uno specifico studente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Valutazioni trovate con successo",
                    content = @Content(schema = @Schema(implementation = AssessmentDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - ruolo TEACHER richiesto"),
            @ApiResponse(responseCode = "404", description = "Studente non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AssessmentDTO>> getAssessmentsByStudent(@PathVariable Long id) {
        return ResponseEntity.ok(assessmentService.getAssessmentsByStudentId(id));
    }

    // GET - getAssessmentsByCourse
    @GetMapping("/course/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Ottieni valutazioni per corso",
            description = "Ottiene tutte le valutazioni per uno specifico cors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Valutazioni trovate con successo",
                    content = @Content(schema = @Schema(implementation = AssessmentDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - ruolo TEACHER richiesto"),
            @ApiResponse(responseCode = "404", description = "Corso non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AssessmentDTO>> getAssessmentsByCourse(@PathVariable Long id) {
        return ResponseEntity.ok(assessmentService.getAssessmentsByCourse(id));
    }

    // GET - getPersonalAssessments
    @GetMapping("/personal")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Ottieni valutazioni personali",
            description = "Ottiene tutte le valutazioni per lo studente autenticato")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Valutazioni personali trovate con successo",
                    content = @Content(schema = @Schema(implementation = AssessmentDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - ruolo STUDENT richiesto"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AssessmentDTO>> getPersonalAssessments() {
        return ResponseEntity.ok(List.of());
    }

    // GET - getPersonalAssessmentDetails
    @GetMapping("/personal/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Ottieni dettagli valutazione personale",
            description = "Ottieni dettagli di una specifica valutazione per lo studente autenticato")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Valutazione personale trovata con success",
                    content = @Content(schema = @Schema(implementation = AssessmentDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - ruolo STUDENT richiesto"),
            @ApiResponse(responseCode = "404", description = "Valutazione non trovata o non accessibile",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AssessmentDTO> getPersonalAssessmentDetails(@PathVariable Long id) {
        return ResponseEntity.ok(assessmentService.getAssessmentById(id));
    }

    // POST - createAssessment
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Crea valutazione",
            description = "Crea una nuova valutazione")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Valutazione creata con successo",
                    content = @Content(schema = @Schema(implementation = AssessmentDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dati richiesta non validi",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - ruolo TEACHER richiesto"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AssessmentDTO> createAssessment(@Valid @RequestBody AssessmentDTO assessmentDTO) {
        return new ResponseEntity<>(assessmentService.createAssessment(assessmentDTO), HttpStatus.CREATED);
    }

    // PUT - updateAssessment
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Aggiorna valutazione",
            description = "Aggiorna una valutazione esistente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Valutazione aggiornata con successo",
                    content = @Content(schema = @Schema(implementation = AssessmentDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dati richiesta non validi",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - ruolo TEACHER richiesto"),
            @ApiResponse(responseCode = "404", description = "Valutazione non trovata",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AssessmentDTO> updateAssessment(@PathVariable Long id,
                                                          @Valid @RequestBody AssessmentDTO assessmentDTO) {
        return ResponseEntity.ok(assessmentService.updateAssessment(id, assessmentDTO));
    }

    // DELETE - deleteAssessment
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Elimina valutazione",
            description = "Elimina una valutazione")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Valutazione eliminata con successo"),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - ruolo TEACHER richiesto"),
            @ApiResponse(responseCode = "404", description = "Valutazione non trovata",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteAssessment(@PathVariable Long id) {
        assessmentService.deleteAssessment(id);
        return ResponseEntity.noContent().build();
    }
}