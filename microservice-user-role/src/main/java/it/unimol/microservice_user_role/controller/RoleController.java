package it.unimol.microservice_user_role.controller;

import it.unimol.microservice_user_role.dto.role.AssignRoleDTO;
import it.unimol.microservice_user_role.dto.role.RoleDTO;
import it.unimol.microservice_user_role.exceptions.InvalidRequestException;
import it.unimol.microservice_user_role.exceptions.UnknownUserException;
import it.unimol.microservice_user_role.service.RoleService;
import it.unimol.microservice_user_role.enums.RoleType;
import it.unimol.microservice_user_role.service.TokenJWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/roles")
@Tag(name = "Roles", description = "API per la gestione dei ruoli")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private TokenJWTService tokenService;

    @Operation(
            summary = "Ottieni tutti i ruoli",
            description = "Restituisce la lista di tutti i ruoli disponibili. Richiede privilegi di amministratore."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista ruoli ottenuta con successo",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token non valido o scaduto"),
            @ApiResponse(responseCode = "403", description = "Privilegi insufficienti - richiesto ruolo ADMIN"),
            @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @GetMapping
    public ResponseEntity<?> getAllRoles(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = tokenService.extractTokenFromHeader(authHeader);
            roleService.checkRole(token, RoleType.ADMIN);

            List<RoleDTO> roles = roleService.getAllRoles();
            return ResponseEntity.ok(roles);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accesso negato", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno del server", "message", e.getMessage()));
        }
    }

    @Operation(
            summary = "Ottieni ruolo per ID",
            description = "Restituisce i dettagli di un ruolo specifico. Richiede privilegi di amministratore."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ruolo trovato",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token non valido o scaduto"),
            @ApiResponse(responseCode = "403", description = "Privilegi insufficienti"),
            @ApiResponse(responseCode = "404", description = "Ruolo non trovato"),
            @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @GetMapping("/{roleId}")
    public ResponseEntity<?> getRoleById(
            @PathVariable String roleId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = tokenService.extractTokenFromHeader(authHeader);
            roleService.checkRole(token, RoleType.ADMIN);

            RoleDTO role = roleService.findById(roleId);
            if (role == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Ruolo non trovato", "roleId", roleId));
            }

            return ResponseEntity.ok(role);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accesso negato", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno del server", "message", e.getMessage()));
        }
    }

    @Operation(
            summary = "Assegna ruolo a utente",
            description = "Assegna un ruolo specifico a un utente. Richiede privilegi di super amministratore."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ruolo assegnato con successo"),
            @ApiResponse(responseCode = "400", description = "Richiesta non valida"),
            @ApiResponse(responseCode = "401", description = "Token non valido o scaduto"),
            @ApiResponse(responseCode = "403", description = "Privilegi insufficienti - richiesto SUPER_ADMIN"),
            @ApiResponse(responseCode = "404", description = "Utente o ruolo non trovato"),
            @ApiResponse(responseCode = "409", description = "L'utente ha già questo ruolo"),
            @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @PostMapping("/assign/{userId}")
    public ResponseEntity<?> assignRole(
            @PathVariable String userId,
            @RequestBody AssignRoleDTO assignRoleDTO,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = tokenService.extractTokenFromHeader(authHeader);
            roleService.checkRole(token, RoleType.SUPER_ADMIN);

            boolean assigned = roleService.assignRole(userId, assignRoleDTO.roleId());

            if (assigned) {
                return ResponseEntity.ok(Map.of(
                        "message", "Ruolo assegnato con successo",
                        "userId", userId,
                        "roleId", assignRoleDTO.roleId()
                ));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "L'utente ha già questo ruolo"));
            }

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accesso negato", "message", e.getMessage()));
        } catch (UnknownUserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Utente non trovato", "userId", userId));
        } catch (InvalidRequestException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Ruolo non trovato", "roleId", assignRoleDTO.roleId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno del server", "message", e.getMessage()));
        }
    }

    @Operation(
            summary = "Rimuovi ruolo da utente",
            description = "Rimuove il ruolo assegnato a un utente. Richiede privilegi di super amministratore."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ruolo rimosso con successo"),
            @ApiResponse(responseCode = "401", description = "Token non valido o scaduto"),
            @ApiResponse(responseCode = "403", description = "Privilegi insufficienti - richiesto SUPER_ADMIN"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato"),
            @ApiResponse(responseCode = "409", description = "L'utente non ha un ruolo assegnato"),
            @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @DeleteMapping("/remove/{userId}")
    public ResponseEntity<?> removeRole(
            @PathVariable String userId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = tokenService.extractTokenFromHeader(authHeader);
            roleService.checkRole(token, RoleType.SUPER_ADMIN);

            boolean removed = roleService.removeRole(userId);

            if (removed) {
                return ResponseEntity.ok(Map.of(
                        "message", "Ruolo rimosso con successo",
                        "userId", userId
                ));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "L'utente non ha un ruolo assegnato"));
            }

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accesso negato", "message", e.getMessage()));
        } catch (UnknownUserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Utente non trovato", "userId", userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno del server", "message", e.getMessage()));
        }
    }

}
