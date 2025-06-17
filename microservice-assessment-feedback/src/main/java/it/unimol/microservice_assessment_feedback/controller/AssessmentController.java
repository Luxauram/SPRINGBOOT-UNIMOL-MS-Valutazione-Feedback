package it.unimol.microservice_assessment_feedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unimol.microservice_assessment_feedback.dto.AssessmentDTO;
import it.unimol.microservice_assessment_feedback.service.AssessmentService;
import it.unimol.microservice_assessment_feedback.common.exception.ErrorResponse;
import it.unimol.microservice_assessment_feedback.enums.RoleType;
import it.unimol.microservice_assessment_feedback.common.util.JWTRequestHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assessments")
@Tag(name = "Assessment Controller", description = "API per la gestione di Assessment (Valutazioni)")
@SecurityRequirement(name = "bearerAuth")
public class AssessmentController {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentController.class);
    private final AssessmentService assessmentService;

    @Autowired
    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @Autowired
    private JWTRequestHelper jwtRequestHelper;

    /**
     * @apiNote GET - getAllAssessments - TEACHER/ADMIN/SUPER_ADMIN
     * TRACCIA: Implicito per gestione valutazioni da parte docenti
     * NOTA: ADMIN/SUPER_ADMIN aggiunti per coerenza architetturale e supervisione amministrativa
     * @return Una lista di oggetti {@link it.unimol.microservice_assessment_feedback.dto.AssessmentDTO} che rappresentano tutte le valutazioni presenti.
     * @see it.unimol.microservice_assessment_feedback.service.AssessmentService#getAllAssessments()
     * @see it.unimol.microservice_assessment_feedback.enums.RoleType
     */
    @GetMapping
    @PreAuthorize("hasRole('" + RoleType.ROLE_TEACHER + "') " +
            "or hasRole('" + RoleType.ROLE_ADMIN + "') " +
            "or hasRole('" + RoleType.ROLE_SUPER_ADMIN + "')")
    @Operation(summary = "Ottieni tutte le valutazioni",
            description = "Ottieni tutte le valutazioni (accessibili a TEACHER e ADMIN)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Valutazioni trovate con successo",
                    content = @Content(schema = @Schema(implementation = AssessmentDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato - Token JWT richiesto"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - ruolo TEACHER o ADMIN richiesto"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AssessmentDTO>> getAllAssessments() {
        logger.info("Richiesta per ottenere tutte le valutazioni");
        return ResponseEntity.ok(assessmentService.getAllAssessments());
    }

    /**
     * @apiNote GET - getAssessmentById - TEACHER/ADMIN/SUPER_ADMIN o STUDENT (solo per le proprie)
     * TRACCIA: "Studenti - Visualizzazione del feedback ricevuto" + gestione docenti
     * @param id L'ID univoco della valutazione da recuperare.
     * @return Un oggetto {@link it.unimol.microservice_assessment_feedback.dto.AssessmentDTO} che rappresenta la valutazione richiesta.
     * @see it.unimol.microservice_assessment_feedback.service.AssessmentService#getAssessmentById(String)
     * @see it.unimol.microservice_assessment_feedback.enums.RoleType
     **/
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('" + RoleType.ROLE_STUDENT + "') " +
            "or hasRole('" + RoleType.ROLE_TEACHER + "') " +
            "or hasRole('" + RoleType.ROLE_ADMIN + "') " +
            "or hasRole('" + RoleType.ROLE_SUPER_ADMIN + "')")
    @Operation(summary = "Ottieni valutazione tramite ID",
            description = "Ottiene una specifica valutazione tramite il suo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Valutazione trovata con successo",
                    content = @Content(schema = @Schema(implementation = AssessmentDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato - Token JWT richiesto"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - autorizzazione insufficiente"),
            @ApiResponse(responseCode = "404", description = "Valutazione non trovata",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AssessmentDTO> getAssessmentById(@PathVariable String id) {
        logger.info("Richiesta per ottenere valutazione con ID: {}", id);
        return ResponseEntity.ok(assessmentService.getAssessmentById(id));
    }

    /**
     * @apiNote GET - getAssessmentsByAssignment - TEACHER/ADMIN/SUPER_ADMIN
     * TRACCIA: Gestione valutazioni da parte docenti (per compiti)
     * NOTA: ADMIN/SUPER_ADMIN per supervisione amministrativa
     * @param id L'ID univoco del compito di cui recuperare le valutazioni.
     * @return Una lista di oggetti {@link it.unimol.microservice_assessment_feedback.dto.AssessmentDTO} che rappresentano le valutazioni per il compito specificato.
     * @see it.unimol.microservice_assessment_feedback.service.AssessmentService#getAssessmentsByAssignment(String)
     * @see it.unimol.microservice_assessment_feedback.enums.RoleType
     **/
    @GetMapping("/assignment/{id}")
    @PreAuthorize("hasRole('" + RoleType.ROLE_TEACHER + "') " +
            "or hasRole('" + RoleType.ROLE_ADMIN + "') " +
            "or hasRole('" + RoleType.ROLE_SUPER_ADMIN + "')")
    @Operation(summary = "Ottieni valutazioni per compito",
            description = "Ottiene tutte le valutazioni per uno specifico compito")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Valutazioni trovate con successo",
                    content = @Content(schema = @Schema(implementation = AssessmentDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato - Token JWT richiesto"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - ruolo TEACHER o ADMIN richiesto"),
            @ApiResponse(responseCode = "404", description = "Compito non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AssessmentDTO>> getAssessmentsByAssignment(@PathVariable String id) {
        logger.info("Richiesta per ottenere valutazioni per compito con ID: {}", id);
        return ResponseEntity.ok(assessmentService.getAssessmentsByAssignment(id));
    }

    /**
     * @apiNote GET - getAssessmentsByExam - TEACHER/ADMIN/SUPER_ADMIN
     * TRACCIA: "Docenti - Fornitura di feedback dettagliato sui compiti e sugli esami"
     * NOTA: ADMIN/SUPER_ADMIN per supervisione amministrativa
     * @param id L'ID univoco dell'esame di cui recuperare le valutazioni.
     * @return Una lista di oggetti {@link it.unimol.microservice_assessment_feedback.dto.AssessmentDTO} che rappresentano le valutazioni per l'esame specificato.
     * @see it.unimol.microservice_assessment_feedback.service.AssessmentService#getAssessmentsByExam(String)
     * @see it.unimol.microservice_assessment_feedback.enums.RoleType
     **/
    @GetMapping("/exam/{id}")
    @PreAuthorize("hasRole('" + RoleType.ROLE_TEACHER + "') " +
            "or hasRole('" + RoleType.ROLE_ADMIN + "') " +
            "or hasRole('" + RoleType.ROLE_SUPER_ADMIN + "')")
    @Operation(summary = "Ottieni valutazioni per esame",
            description = "Ottiene tutte le valutazioni per uno specifico esame")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Valutazioni trovate con successo",
                    content = @Content(schema = @Schema(implementation = AssessmentDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato - Token JWT richiesto"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - ruolo TEACHER o ADMIN richiesto"),
            @ApiResponse(responseCode = "404", description = "Esame non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AssessmentDTO>> getAssessmentsByExam(@PathVariable String id) {
        logger.info("Richiesta per ottenere valutazioni per esame con ID: {}", id);
        return ResponseEntity.ok(assessmentService.getAssessmentsByExam(id));
    }

    /**
     * @apiNote GET - getAssessmentsByStudent - TEACHER/ADMIN/SUPER_ADMIN o STUDENT (solo per se stesso)
     * TRACCIA: "Studenti - Visualizzazione del feedback ricevuto" + gestione docenti
     * @param id L'ID univoco dello studente di cui recuperare le valutazioni.
     * @return Una lista di oggetti {@link it.unimol.microservice_assessment_feedback.dto.AssessmentDTO} che rappresenta le valutazioni per lo studente specificato.
     * @see it.unimol.microservice_assessment_feedback.service.AssessmentService#getAssessmentsByStudentId(String)
     * @see it.unimol.microservice_assessment_feedback.enums.RoleType
     **/
    @GetMapping("/student/{id}")
    @PreAuthorize("hasRole('" + RoleType.ROLE_STUDENT + "') " +
            "or hasRole('" + RoleType.ROLE_TEACHER + "') " +
            "or hasRole('" + RoleType.ROLE_ADMIN + "') " +
            "or hasRole('" + RoleType.ROLE_SUPER_ADMIN + "')")
    @Operation(summary = "Ottieni valutazioni per studente",
            description = "Ottiene tutte le valutazioni per uno specifico studente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Valutazioni trovate con successo",
                    content = @Content(schema = @Schema(implementation = AssessmentDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato - Token JWT richiesto"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - autorizzazione insufficiente"),
            @ApiResponse(responseCode = "404", description = "Studente non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AssessmentDTO>> getAssessmentsByStudent(@PathVariable String id) {
        logger.info("Richiesta per ottenere valutazioni per studente con ID: {}", id);
        return ResponseEntity.ok(assessmentService.getAssessmentsByStudentId(id));
    }

    /**
     * @apiNote GET - getAssessmentsByCourse - TEACHER/ADMIN/SUPER_ADMIN
     * TRACCIA: Gestione valutazioni da parte docenti (per corso)
     * NOTA: ADMIN/SUPER_ADMIN per supervisione amministrativa
     * @param id L'ID univoco del corso di cui recuperare le valutazioni.
     * @return Una lista di oggetti {@link it.unimol.microservice_assessment_feedback.dto.AssessmentDTO} che rappresentano le valutazioni per il corso specificato.
     * @see it.unimol.microservice_assessment_feedback.service.AssessmentService#getAssessmentsByCourse(String)
     * @see it.unimol.microservice_assessment_feedback.enums.RoleType
     **/
    @GetMapping("/course/{id}")
    @PreAuthorize("hasRole('" + RoleType.ROLE_TEACHER + "') " +
            "or hasRole('" + RoleType.ROLE_ADMIN + "') " +
            "or hasRole('" + RoleType.ROLE_SUPER_ADMIN + "')")
    @Operation(summary = "Ottieni valutazioni per corso",
            description = "Ottiene tutte le valutazioni per uno specifico corso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Valutazioni trovate con successo",
                    content = @Content(schema = @Schema(implementation = AssessmentDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato - Token JWT richiesto"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - autorizzazione insufficiente"),
            @ApiResponse(responseCode = "404", description = "Corso non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AssessmentDTO>> getAssessmentsByCourse(@PathVariable String id) {
        logger.info("Richiesta per ottenere valutazioni per corso con ID: {}", id);
        return ResponseEntity.ok(assessmentService.getAssessmentsByCourse(id));
    }

    /**
     * @apiNote GET - getPersonalAssessments - STUDENT/ADMIN/SUPER_ADMIN
     * TRACCIA: "Studenti - Visualizzazione del feedback ricevuto"
     * NOTA: ADMIN/SUPER_ADMIN per supervisione amministrativa
     * @param request L'oggetto {@link jakarta.servlet.http.HttpServletRequest} contenente le informazioni della richiesta,
     * utilizzato per estrarre l'ID dello studente autenticato.
     * @return Una lista di oggetti {@link it.unimol.microservice_assessment_feedback.dto.AssessmentDTO} che rappresenta
     * tutte le valutazioni associate allo studente autenticato.
     * @see it.unimol.microservice_assessment_feedback.service.AssessmentService#getAssessmentsByStudentId(String)
     * @see it.unimol.microservice_assessment_feedback.enums.RoleType
     * @see JWTRequestHelper#getUsernameFromRequest(HttpServletRequest)
     * @see JWTRequestHelper#extractStudentIdFromRequest(HttpServletRequest)
     */
    @GetMapping("/personal")
    @PreAuthorize("hasRole('" + RoleType.ROLE_STUDENT + "') " +
            "or hasRole('" + RoleType.ROLE_ADMIN + "') " +
            "or hasRole('" + RoleType.ROLE_SUPER_ADMIN + "')")
    @Operation(summary = "Ottieni valutazioni personali",
            description = "Ottiene tutte le valutazioni per lo studente autenticato")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Valutazioni personali trovate con successo",
                    content = @Content(schema = @Schema(implementation = AssessmentDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - ruolo STUDENT richiesto"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AssessmentDTO>> getPersonalAssessments(HttpServletRequest request) {
        String username = jwtRequestHelper.getUsernameFromRequest(request);
        logger.info("Richiesta per ottenere valutazioni personali per utente: {}", username);

        String studentId = jwtRequestHelper.extractStudentIdFromRequest(request);
        return ResponseEntity.ok(assessmentService.getAssessmentsByStudentId(studentId));
    }

    /**
     * @apiNote GET - getPersonalAssessmentDetails - STUDENT/ADMIN/SUPER_ADMIN
     * TRACCIA: "Studenti - Visualizzazione del feedback ricevuto" (dettagli specifici)
     * NOTA: ADMIN/SUPER_ADMIN per supervisione amministrativa
     * @param id L'ID univoco della valutazione di cui recuperare i dettagli.
     * Per gli studenti, questo ID deve corrispondere a una delle proprie valutazioni.
     * @return Un oggetto {@link it.unimol.microservice_assessment_feedback.dto.AssessmentDTO} che rappresenta
     * i dettagli della valutazione richiesta.
     * @see it.unimol.microservice_assessment_feedback.service.AssessmentService#getAssessmentById(String)
     * @see it.unimol.microservice_assessment_feedback.enums.RoleType
     **/
    @GetMapping("/personal/{id}")
    @PreAuthorize("hasRole('" + RoleType.ROLE_STUDENT + "') " +
            "or hasRole('" + RoleType.ROLE_ADMIN + "') " +
            "or hasRole('" + RoleType.ROLE_SUPER_ADMIN + "')")
    @Operation(summary = "Ottieni dettagli valutazione personale",
            description = "Ottieni dettagli di una specifica valutazione per lo studente autenticato")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Valutazione personale trovata con successo",
                    content = @Content(schema = @Schema(implementation = AssessmentDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato - Token JWT richiesto"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - valutazione non accessibile"),
            @ApiResponse(responseCode = "404", description = "Valutazione non trovata",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AssessmentDTO> getPersonalAssessmentDetails(@PathVariable String id) {
        logger.info("Richiesta per ottenere dettagli valutazione personale con ID: {}", id);
        return ResponseEntity.ok(assessmentService.getAssessmentById(id));
    }

    /**
     * @apiNote POST - createAssessment - TEACHER/ADMIN/SUPER_ADMIN
     * TRACCIA: "Docenti - Fornitura di feedback dettagliato sui compiti e sugli esami"
     * NOTA: ADMIN/SUPER_ADMIN per gestione amministrativa delle valutazioni
     * @param assessmentDTO Un oggetto {@link it.unimol.microservice_assessment_feedback.dto.AssessmentDTO}
     * contenente i dati della nuova valutazione da creare.
     * @param request L'oggetto {@link jakarta.servlet.http.HttpServletRequest} utilizzato per estrarre l'ID del docente autenticato.
     * @return Un {@link org.springframework.http.ResponseEntity} contenente l'oggetto {@link it.unimol.microservice_assessment_feedback.dto.AssessmentDTO}
     * della valutazione appena creata, con stato HTTP 201 (Created).
     * @see it.unimol.microservice_assessment_feedback.service.AssessmentService#createAssessment(AssessmentDTO)
     * @see it.unimol.microservice_assessment_feedback.enums.RoleType
     * @see JWTRequestHelper#getUsernameFromRequest(HttpServletRequest)
     * @see JWTRequestHelper#extractTeacherIdFromRequest(HttpServletRequest)
     */
    @PostMapping
    @PreAuthorize("hasRole('" + RoleType.ROLE_TEACHER + "') " +
            "or hasRole('" + RoleType.ROLE_ADMIN + "') " +
            "or hasRole('" + RoleType.ROLE_SUPER_ADMIN + "')")
    @Operation(summary = "Crea valutazione",
            description = "Crea una nuova valutazione")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Valutazione creata con successo",
                    content = @Content(schema = @Schema(implementation = AssessmentDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dati richiesta non validi",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - ruolo TEACHER richiesto o non autorizzato per questo corso"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AssessmentDTO> createAssessment(@Valid @RequestBody AssessmentDTO assessmentDTO,
                                                          HttpServletRequest request) {
        String username = jwtRequestHelper.getUsernameFromRequest(request);
        logger.info("Richiesta per creare nuova valutazione da utente: {}", username);

        String teacherId = jwtRequestHelper.extractTeacherIdFromRequest(request);
        assessmentDTO.setTeacherId(teacherId);

        AssessmentDTO createdAssessment = assessmentService.createAssessment(assessmentDTO);
        logger.info("Valutazione creata con successo con ID: {}", createdAssessment.getId());

        return new ResponseEntity<>(createdAssessment, HttpStatus.CREATED);
    }

    /**
     * @apiNote PUT - updateAssessment - TEACHER/ADMIN/SUPER_ADMIN
     * TRACCIA: Gestione e modifica feedback da parte docenti
     * NOTA: ADMIN/SUPER_ADMIN per correzioni amministrative
     * @param id L'ID univoco della valutazione da aggiornare.
     * @param assessmentDTO Un oggetto {@link it.unimol.microservice_assessment_feedback.dto.AssessmentDTO}
     * contenente i dati aggiornati per la valutazione.
     * @return Un {@link org.springframework.http.ResponseEntity} contenente l'oggetto {@link it.unimol.microservice_assessment_feedback.dto.AssessmentDTO}
     * della valutazione aggiornata, con stato HTTP 200 (OK).
     * @see it.unimol.microservice_assessment_feedback.service.AssessmentService#updateAssessment(String, AssessmentDTO)
     * @see it.unimol.microservice_assessment_feedback.enums.RoleType
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('" + RoleType.ROLE_TEACHER + "') " +
            "or hasRole('" + RoleType.ROLE_ADMIN + "') " +
            "or hasRole('" + RoleType.ROLE_SUPER_ADMIN + "')")
    @Operation(summary = "Aggiorna valutazione",
            description = "Aggiorna una valutazione esistente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Valutazione aggiornata con successo",
                    content = @Content(schema = @Schema(implementation = AssessmentDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dati richiesta non validi",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - non autorizzato a modificare questa valutazione"),
            @ApiResponse(responseCode = "404", description = "Valutazione non trovata",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AssessmentDTO> updateAssessment(@PathVariable String id,
                                                          @Valid @RequestBody AssessmentDTO assessmentDTO) {
        logger.info("Richiesta per aggiornare valutazione con ID: {}", id);
        AssessmentDTO updatedAssessment = assessmentService.updateAssessment(id, assessmentDTO);
        logger.info("Valutazione aggiornata con successo con ID: {}", id);

        return ResponseEntity.ok(updatedAssessment);
    }

    /**
     * @apiNote DELETE - deleteAssessment - TEACHER/ADMIN/SUPER_ADMIN
     * TRACCIA: Gestione valutazioni da parte docenti
     * NOTA: ADMIN/SUPER_ADMIN per eliminazioni amministrative
     * @param id L'ID univoco della valutazione da eliminare.
     * @return Un {@link org.springframework.http.ResponseEntity} con stato HTTP 204 (No Content)
     * se la valutazione è stata eliminata con successo.
     * @see it.unimol.microservice_assessment_feedback.service.AssessmentService#deleteAssessment(String)
     * @see it.unimol.microservice_assessment_feedback.enums.RoleType
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('" + RoleType.ROLE_TEACHER + "') " +
            "or hasRole('" + RoleType.ROLE_ADMIN + "') " +
            "or hasRole('" + RoleType.ROLE_SUPER_ADMIN + "')")
    @Operation(summary = "Elimina valutazione",
            description = "Elimina una valutazione")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Valutazione eliminata con successo"),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - non autorizzato a eliminare questa valutazione"),
            @ApiResponse(responseCode = "404", description = "Valutazione non trovata",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteAssessment(@PathVariable String id) {
        logger.info("Richiesta per eliminare valutazione con ID: {}", id);
        assessmentService.deleteAssessment(id);
        logger.info("Valutazione eliminata con successo con ID: {}", id);

        return ResponseEntity.noContent().build();
    }
}