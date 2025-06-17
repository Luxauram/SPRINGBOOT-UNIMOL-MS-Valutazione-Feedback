package it.unimol.microservice_assessment_feedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unimol.microservice_assessment_feedback.common.exception.ResourceNotFoundException;
import it.unimol.microservice_assessment_feedback.dto.DetailedFeedbackDTO;
import it.unimol.microservice_assessment_feedback.enums.RoleType;
import it.unimol.microservice_assessment_feedback.service.DetailedFeedbackService;
import it.unimol.microservice_assessment_feedback.common.exception.ErrorResponse;
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
@RequestMapping("/api/v1/feedback")
@Tag(name = "DetailedFeedback Controller", description = "API per la gestione di DetailedFeedback (Feedback)")
@SecurityRequirement(name = "bearerAuth")
public class DetailedFeedbackController {

    private static final Logger logger = LoggerFactory.getLogger(DetailedFeedbackController.class);
    private final DetailedFeedbackService feedbackService;

    @Autowired
    public DetailedFeedbackController(DetailedFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @Autowired
    private JWTRequestHelper jwtRequestHelper;

    /**
     * @apiNote GET - getAllFeedback - TEACHER/ADMIN/SUPER_ADMIN
     * TRACCIA: "Docenti - Fornitura di feedback dettagliato sui compiti e sugli esami" (gestione docenti)
     * NOTA: ADMIN/SUPER_ADMIN aggiunti per supervisione amministrativa
     * @return Una lista di oggetti {@link it.unimol.microservice_assessment_feedback.dto.DetailedFeedbackDTO} che rappresentano tutti i feedback presenti.
     * @see it.unimol.microservice_assessment_feedback.service.DetailedFeedbackService#getAllFeedback()
     * @see it.unimol.microservice_assessment_feedback.enums.RoleType
     */
    @GetMapping
    @PreAuthorize("hasRole('" + RoleType.ROLE_TEACHER + "') " +
            "or hasRole('" + RoleType.ROLE_ADMIN + "') " +
            "or hasRole('" + RoleType.ROLE_SUPER_ADMIN + "')")
    @Operation(summary = "Ottieni tutti i feedback esistenti",
            description = "Recupera tutte le voci di feedback presenti nel sistema. Accessibile a docenti e admin.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voci di feedback trovate con successo",
                    content = @Content(schema = @Schema(implementation = DetailedFeedbackDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato - Token JWT richiesto"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - ruolo TEACHER o ADMIN richiesto"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<DetailedFeedbackDTO>> getAllFeedback() {
        logger.info("Richiesta per ottenere tutti i feedback");
        List<DetailedFeedbackDTO> feedbacks = feedbackService.getAllFeedback();
        logger.info("Recuperati {} feedback totali", feedbacks.size());
        return ResponseEntity.ok(feedbacks);
    }

    /**
     * @apiNote GET - getFeedbackByAssessmentId - STUDENT/TEACHER/ADMIN/SUPER_ADMIN
     * TRACCIA: "Studenti - Visualizzazione del feedback ricevuto" + gestione docenti
     * NOTA: Studenti possono visualizzare feedback delle proprie valutazioni, docenti possono gestire i propri feedback
     * @param id L'ID univoco della valutazione di cui recuperare i feedback.
     * @return Una lista di oggetti {@link it.unimol.microservice_assessment_feedback.dto.DetailedFeedbackDTO} che rappresentano i feedback per la valutazione specificata.
     * @see it.unimol.microservice_assessment_feedback.service.DetailedFeedbackService#getFeedbackByAssessmentId(String)
     * @see it.unimol.microservice_assessment_feedback.enums.RoleType
     */
    @GetMapping("/assessment/{id}")
    @PreAuthorize("hasRole('" + RoleType.ROLE_STUDENT + "') " +
            "or hasRole('" + RoleType.ROLE_TEACHER + "') " +
            "or hasRole('" + RoleType.ROLE_ADMIN + "') " +
            "or hasRole('" + RoleType.ROLE_SUPER_ADMIN + "')")
    @Operation(summary = "Ottieni feedback tramite ID della valutazione",
            description = "Recupera tutte le voci di feedback per una specifica valutazione")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voci di feedback trovate con successo",
                    content = @Content(schema = @Schema(implementation = DetailedFeedbackDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato - Token JWT richiesto"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - autorizzazione insufficiente"),
            @ApiResponse(responseCode = "404", description = "Valutazione non trovata",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<DetailedFeedbackDTO>> getFeedbackByAssessmentId(
            @Parameter(description = "Assessment ID", required = true, example = "uuid-assessment-123")
            @PathVariable String id) {
        logger.info("Richiesta per ottenere feedback per valutazione con ID: {}", id);
        List<DetailedFeedbackDTO> feedbacks = feedbackService.getFeedbackByAssessmentId(id);
        logger.info("Recuperati {} feedback per valutazione ID: {}", feedbacks.size(), id);
        return ResponseEntity.ok(feedbacks);
    }

    /**
     * @apiNote GET - getFeedbackById - STUDENT/TEACHER/ADMIN/SUPER_ADMIN
     * TRACCIA: "Studenti - Visualizzazione del feedback ricevuto" + gestione docenti
     * NOTA: Studenti possono visualizzare dettagli feedback delle proprie valutazioni, docenti possono gestire i propri feedback
     * @param id L'ID univoco del feedback da recuperare.
     * @return Un oggetto {@link it.unimol.microservice_assessment_feedback.dto.DetailedFeedbackDTO} che rappresenta il feedback richiesto.
     * @see it.unimol.microservice_assessment_feedback.service.DetailedFeedbackService#getFeedbackById(String)
     * @see it.unimol.microservice_assessment_feedback.enums.RoleType
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('" + RoleType.ROLE_STUDENT + "') " +
            "or hasRole('" + RoleType.ROLE_TEACHER + "') " +
            "or hasRole('" + RoleType.ROLE_ADMIN + "') " +
            "or hasRole('" + RoleType.ROLE_SUPER_ADMIN + "')")
    @Operation(summary = "Ottieni feedback tramite ID",
            description = "Recupera una specifica voce di feedback tramite il suo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voce di feedback trovata con successo",
                    content = @Content(schema = @Schema(implementation = DetailedFeedbackDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato - Token JWT richiesto"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - autorizzazione insufficiente"),
            @ApiResponse(responseCode = "404", description = "Feedback non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DetailedFeedbackDTO> getFeedbackById(
            @Parameter(description = "Feedback ID", required = true, example = "uuid-feedback-456")
            @PathVariable String id) {
        logger.info("Richiesta per ottenere feedback con ID: {}", id);
        DetailedFeedbackDTO feedback = feedbackService.getFeedbackById(id);
        logger.info("Recuperato feedback con ID: {}", id);
        return ResponseEntity.ok(feedback);
    }

    /**
     * @apiNote GET - getPersonalFeedback - STUDENT/ADMIN/SUPER_ADMIN
     * TRACCIA: "Studenti - Visualizzazione del feedback ricevuto"
     * NOTA: ADMIN/SUPER_ADMIN per supervisione amministrativa
     * @param request L'oggetto {@link jakarta.servlet.http.HttpServletRequest} contenente le informazioni della richiesta,
     * utilizzato per estrarre l'ID dello studente autenticato.
     * @return Una lista di oggetti {@link it.unimol.microservice_assessment_feedback.dto.DetailedFeedbackDTO} che rappresenta
     * tutti i feedback associati alle valutazioni dello studente autenticato.
     * @see it.unimol.microservice_assessment_feedback.service.DetailedFeedbackService#getFeedbackByStudentId(String)
     * @see JWTRequestHelper#getUsernameFromRequest(HttpServletRequest)
     * @see JWTRequestHelper#extractStudentIdFromRequest(HttpServletRequest)
     * @see it.unimol.microservice_assessment_feedback.enums.RoleType
     */
    @GetMapping("/personal")
    @PreAuthorize("hasRole('" + RoleType.ROLE_STUDENT + "') " +
            "or hasRole('" + RoleType.ROLE_ADMIN + "') " +
            "or hasRole('" + RoleType.ROLE_SUPER_ADMIN + "')")
    @Operation(summary = "Ottieni feedback personali",
            description = "Ottiene tutti i feedback per lo studente autenticato")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedback personali trovati con successo",
                    content = @Content(schema = @Schema(implementation = DetailedFeedbackDTO.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato - Token JWT richiesto"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - ruolo STUDENT richiesto"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<DetailedFeedbackDTO>> getPersonalFeedback(HttpServletRequest request) {
        String username = jwtRequestHelper.getUsernameFromRequest(request);
        String studentId = jwtRequestHelper.extractStudentIdFromRequest(request);
        logger.info("Richiesta per ottenere feedback personali per utente: {} (studentId: {})", username, studentId);

        List<DetailedFeedbackDTO> feedbacks = feedbackService.getFeedbackByStudentId(studentId);
        logger.info("Recuperati {} feedback personali per studente: {}", feedbacks.size(), studentId);
        return ResponseEntity.ok(feedbacks);
    }

    /**
     * @apiNote POST - createFeedback - TEACHER/ADMIN/SUPER_ADMIN
     * TRACCIA: "Docenti - Fornitura di feedback dettagliato sui compiti e sugli esami"
     * NOTA: ADMIN/SUPER_ADMIN per gestione amministrativa dei feedback
     * @param feedbackDTO Un oggetto {@link it.unimol.microservice_assessment_feedback.dto.DetailedFeedbackDTO}
     * contenente i dati del nuovo feedback da creare.
     * @param request L'oggetto {@link jakarta.servlet.http.HttpServletRequest} utilizzato per estrarre l'ID del docente autenticato.
     * @return Un {@link org.springframework.http.ResponseEntity} contenente l'oggetto {@link it.unimol.microservice_assessment_feedback.dto.DetailedFeedbackDTO}
     * del feedback appena creato, con stato HTTP 201 (Created).
     * @see it.unimol.microservice_assessment_feedback.service.DetailedFeedbackService#createFeedback(DetailedFeedbackDTO)
     * @see JWTRequestHelper#getUsernameFromRequest(HttpServletRequest)
     * @see JWTRequestHelper#getUserRoleFromRequest(HttpServletRequest)
     * @see JWTRequestHelper#extractTeacherIdFromRequest(HttpServletRequest)
     * @see it.unimol.microservice_assessment_feedback.enums.RoleType
     */
    @PostMapping
    @PreAuthorize("hasRole('" + RoleType.ROLE_TEACHER + "') " +
            "or hasRole('" + RoleType.ROLE_ADMIN + "') " +
            "or hasRole('" + RoleType.ROLE_SUPER_ADMIN + "')")
    @Operation(summary = "Crea un feedback",
            description = "Crea una nuova voce di feedback. Richiede ruolo TEACHER, ADMIN o SUPER_ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Feedback creato con successo",
                    content = @Content(schema = @Schema(implementation = DetailedFeedbackDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dati richiesta non validi",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato - Token JWT richiesto"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - ruolo TEACHER, ADMIN o SUPER_ADMIN richiesto"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DetailedFeedbackDTO> createFeedback(
            @Parameter(description = "Feedback data", required = true)
            @Valid @RequestBody DetailedFeedbackDTO feedbackDTO,
            HttpServletRequest request) {

        String username = jwtRequestHelper.getUsernameFromRequest(request);
        String userRole = jwtRequestHelper.getUserRoleFromRequest(request);

        logger.info("Richiesta per creare feedback per valutazione ID: {} da utente: {} con ruolo: {}",
                feedbackDTO.getAssessmentId(), username, userRole);

        DetailedFeedbackDTO createdFeedback = feedbackService.createFeedback(feedbackDTO);

        logger.info("Feedback creato con successo con ID: {} da utente: {}",
                createdFeedback.getId(), username);

        return new ResponseEntity<>(createdFeedback, HttpStatus.CREATED);
    }

    /**
     * @apiNote PUT - updateFeedback - TEACHER/ADMIN/SUPER_ADMIN
     * TRACCIA: Gestione e modifica feedback da parte docenti
     * NOTA: ADMIN/SUPER_ADMIN per correzioni amministrative
     * @param id L'ID univoco del feedback da aggiornare.
     * @param feedbackDTO Un oggetto {@link it.unimol.microservice_assessment_feedback.dto.DetailedFeedbackDTO}
     * contenente i dati aggiornati per il feedback.
     * @return Un {@link org.springframework.http.ResponseEntity} contenente l'oggetto {@link it.unimol.microservice_assessment_feedback.dto.DetailedFeedbackDTO}
     * del feedback aggiornato, con stato HTTP 200 (OK).
     * @see it.unimol.microservice_assessment_feedback.service.DetailedFeedbackService#updateFeedback(String, DetailedFeedbackDTO)
     * @see it.unimol.microservice_assessment_feedback.enums.RoleType
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('" + RoleType.ROLE_TEACHER + "') " +
            "or hasRole('" + RoleType.ROLE_ADMIN + "') " +
            "or hasRole('" + RoleType.ROLE_SUPER_ADMIN + "')")
    @Operation(summary = "Aggiorna un feedback",
            description = "Aggiorna una voce di feedback esistente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedback aggiornato con successo",
                    content = @Content(schema = @Schema(implementation = DetailedFeedbackDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dati richiesta non validi",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato - Token JWT richiesto"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - non autorizzato a modificare questo feedback"),
            @ApiResponse(responseCode = "404", description = "Feedback non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DetailedFeedbackDTO> updateFeedback(
            @Parameter(description = "Feedback ID", required = true, example = "uuid-feedback-456")
            @PathVariable String id,
            @Parameter(description = "Updated feedback data", required = true)
            @Valid @RequestBody DetailedFeedbackDTO feedbackDTO) {

        logger.info("Richiesta per aggiornare feedback con ID: {}", id);
        logger.debug("Dati ricevuti: {}", feedbackDTO);

        try {
            DetailedFeedbackDTO updatedFeedback = feedbackService.updateFeedback(id, feedbackDTO);
            logger.info("Feedback aggiornato con successo con ID: {}", id);
            logger.debug("Feedback aggiornato: {}", updatedFeedback);

            return ResponseEntity.ok(updatedFeedback);

        } catch (ResourceNotFoundException e) {
            logger.error("Feedback non trovato con ID: {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Errore durante l'aggiornamento del feedback con ID: {}", id, e);
            throw e;
        }
    }

    /**
     * @apiNote DELETE - deleteFeedback - TEACHER/ADMIN/SUPER_ADMIN
     * TRACCIA: Gestione feedback da parte docenti
     * NOTA: ADMIN/SUPER_ADMIN per eliminazioni amministrative
     * @param id L'ID univoco del feedback da eliminare.
     * @return Un {@link org.springframework.http.ResponseEntity} con stato HTTP 204 (No Content)
     * se il feedback è stato eliminato con successo.
     * @see it.unimol.microservice_assessment_feedback.service.DetailedFeedbackService#deleteFeedback(String)
     * @see it.unimol.microservice_assessment_feedback.enums.RoleType
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('" + RoleType.ROLE_TEACHER + "') " +
            "or hasRole('" + RoleType.ROLE_ADMIN + "') " +
            "or hasRole('" + RoleType.ROLE_SUPER_ADMIN + "')")
    @Operation(summary = "Elimina un feedback",
            description = "Elimina una voce di feedback")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Feedback eliminato con successo"),
            @ApiResponse(responseCode = "401", description = "Accesso non autorizzato - Token JWT richiesto"),
            @ApiResponse(responseCode = "403", description = "Accesso vietato - non autorizzato a eliminare questo feedback"),
            @ApiResponse(responseCode = "404", description = "Feedback non trovato",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteFeedback(
            @Parameter(description = "Feedback ID", required = true, example = "uuid-feedback-456")
            @PathVariable String id) {

        logger.info("Richiesta per eliminare feedback con ID: {}", id);
        feedbackService.deleteFeedback(id);
        logger.info("Feedback eliminato con successo con ID: {}", id);

        return ResponseEntity.noContent().build();
    }
}