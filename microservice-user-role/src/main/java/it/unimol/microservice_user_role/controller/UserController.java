package it.unimol.microservice_user_role.controller;

import it.unimol.microservice_user_role.dto.auth.ChangePasswordRequestDTO;
import it.unimol.microservice_user_role.dto.role.AssignRoleDTO;
import it.unimol.microservice_user_role.dto.user.CreateUserDTO;
import it.unimol.microservice_user_role.dto.user.UserDTO;
import it.unimol.microservice_user_role.dto.user.UserProfileDTO;
import it.unimol.microservice_user_role.dto.user.UpdateUserProfileDTO;
import it.unimol.microservice_user_role.exceptions.InvalidIdException;
import it.unimol.microservice_user_role.exceptions.InvalidRequestException;
import it.unimol.microservice_user_role.exceptions.UnknownUserException;
import it.unimol.microservice_user_role.service.RoleService;
import it.unimol.microservice_user_role.service.TokenJWTService;
import it.unimol.microservice_user_role.enums.RoleType;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
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
import it.unimol.microservice_user_role.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "API per la gestione degli utenti")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private TokenJWTService tokenService;

    // ============= ENDPOINTS PUBBLICI =============

    @Operation(summary = "Crea SuperAdmin iniziale", description = "Crea il primo SuperAdmin del sistema se non esiste già")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "SuperAdmin creato con successo"),
            @ApiResponse(responseCode = "409", description = "SuperAdmin già esistente"),
            @ApiResponse(responseCode = "400", description = "Dati non validi")
    })
    @PostMapping("/superadmin/init")
    public ResponseEntity<UserDTO> createSuperAdmin(@Valid @RequestBody CreateUserDTO request) {
        try {
            UserDTO superAdmin = userService.createSuperAdminIfNotExists(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(superAdmin);
        } catch (InvalidRequestException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ============= ENDPOINTS ADMIN =============

    @Operation(summary = "Crea nuovo utente", description = "Crea un nuovo utente nel sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utente creato con successo"),
            @ApiResponse(responseCode = "400", description = "Dati non validi"),
            @ApiResponse(responseCode = "403", description = "Privilegi insufficienti"),
            @ApiResponse(responseCode = "409", description = "Username o email già esistente")
    })
    @PostMapping
    public ResponseEntity<UserDTO> createUser(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateUserDTO request) {
        try {
            String token = tokenService.extractTokenFromHeader(authHeader);
            roleService.checkRole(token, RoleType.ADMIN);

            UserDTO user = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (InvalidRequestException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Ottieni tutti gli utenti", description = "Restituisce la lista di tutti gli utenti")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista utenti recuperata con successo"),
            @ApiResponse(responseCode = "403", description = "Privilegi insufficienti")
    })
    @GetMapping
    public ResponseEntity<List<UserProfileDTO>> getAllUsers(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = tokenService.extractTokenFromHeader(authHeader);
            roleService.checkRole(token, RoleType.ADMIN);

            List<UserProfileDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Ottieni utente per ID", description = "Restituisce i dettagli di un utente specifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utente trovato"),
            @ApiResponse(responseCode = "403", description = "Privilegi insufficienti"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String id) {

        String token = null;

        try {
            // STEP 1: Estrazione token
            System.out.println("=== STEP 1: Estrazione Token ===");
            token = tokenService.extractTokenFromHeader(authHeader);
            System.out.println("Token estratto con successo");

        } catch (Exception e) {
            System.out.println("ERRORE in extractTokenFromHeader: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore estrazione token", "message", e.getMessage()));
        }

        try {
            // STEP 2: Check Role
            System.out.println("=== STEP 2: Check Role ===");
            roleService.checkRole(token, RoleType.ADMIN);
            System.out.println("Check role passato con successo");

        } catch (SecurityException e) {
            System.out.println("ERRORE SecurityException: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Privilegi insufficienti", "message", e.getMessage()));
        }

        try {
            // STEP 3: Find User
            System.out.println("=== STEP 3: Find User ===");
            System.out.println("Cercando utente con ID: " + id);
            UserDTO user = userService.findById(id);
            System.out.println("Utente trovato con successo");

            return ResponseEntity.ok(user);

        } catch (UnknownUserException e) {
            System.out.println("ERRORE UnknownUserException: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Utente non trovato", "message", e.getMessage()));
        } catch (Exception e) {
            System.out.println("ERRORE in findById: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore ricerca utente", "message", e.getMessage(), "type", e.getClass().getSimpleName()));
        }
    }

    @Operation(summary = "Aggiorna utente", description = "Aggiorna i dati di un utente esistente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utente aggiornato con successo"),
            @ApiResponse(responseCode = "400", description = "Dati non validi"),
            @ApiResponse(responseCode = "403", description = "Privilegi insufficienti"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String id,
            @Valid @RequestBody UpdateUserProfileDTO request) {
        try {
            String token = tokenService.extractTokenFromHeader(authHeader);
            roleService.checkRole(token, RoleType.ADMIN);

            UserDTO updatedUser = userService.updateUser(id, request);
            return ResponseEntity.ok(updatedUser);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (UnknownUserException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Elimina utente", description = "Elimina un utente dal sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Utente eliminato con successo"),
            @ApiResponse(responseCode = "400", description = "Non puoi eliminare te stesso"),
            @ApiResponse(responseCode = "403", description = "Privilegi insufficienti"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String id) {
        try {
            String token = tokenService.extractTokenFromHeader(authHeader);
            roleService.checkRole(token, RoleType.ADMIN);

            // Impedisce all'admin di eliminare se stesso
            if (tokenService.extractUserId(token).equals(id)) {
                return ResponseEntity.badRequest().build();
            }

            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ============= ENDPOINTS PROFILO UTENTE =============

    @Operation(summary = "Ottieni profilo utente corrente", description = "Restituisce il profilo dell'utente autenticato")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profilo ottenuto con successo",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token non valido"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato")
    })
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = tokenService.extractTokenFromHeader(authHeader);
            UserProfileDTO profile = userService.getCurrentUserProfile(token);
            return ResponseEntity.ok(profile);
        } catch (UnknownUserException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Aggiorna profilo utente corrente", description = "Aggiorna il profilo dell'utente autenticato")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profilo aggiornato con successo"),
            @ApiResponse(responseCode = "400", description = "Dati non validi"),
            @ApiResponse(responseCode = "401", description = "Token non valido"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato")
    })
    @PutMapping("/profile")
    public ResponseEntity<UserProfileDTO> updateUserProfile(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UpdateUserProfileDTO request) {
        try {
            String token = tokenService.extractTokenFromHeader(authHeader);
            UserProfileDTO profile = userService.updateCurrentUserProfile(token, request);
            return ResponseEntity.ok(profile);
        } catch (UnknownUserException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // ============= ENDPOINTS GESTIONE PASSWORD =============

    @Operation(summary = "Cambia password", description = "Cambia la password dell'utente corrente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password cambiata con successo"),
            @ApiResponse(responseCode = "400", description = "Password attuale errata o nuova password non valida"),
            @ApiResponse(responseCode = "401", description = "Token non valido")
    })
    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ChangePasswordRequestDTO request) {
        try {
            String token = tokenService.extractTokenFromHeader(authHeader);
            boolean success = userService.changePassword(token, request.currentPassword(), request.newPassword());

            if (success) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (UnknownUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Reset password", description = "Resetta la password dell'utente corrente a una temporanea")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password resettata con successo"),
            @ApiResponse(responseCode = "400", description = "Password attuale errata"),
            @ApiResponse(responseCode = "401", description = "Token non valido")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ChangePasswordRequestDTO request) {
        try {
            String token = tokenService.extractTokenFromHeader(authHeader);
            String tempPassword = userService.resetPassword(token, request.currentPassword());
            return ResponseEntity.ok(tempPassword);
        } catch (SecurityException e) {
            return ResponseEntity.badRequest().build();
        } catch (UnknownUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ============= ENDPOINTS GESTIONE RUOLI =============

    @Operation(summary = "Assegna ruolo a utente", description = "Assegna un ruolo specifico a un utente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ruolo assegnato con successo"),
            @ApiResponse(responseCode = "400", description = "Dati non validi"),
            @ApiResponse(responseCode = "403", description = "Privilegi insufficienti"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato")
    })
    @PostMapping("/{id}/roles")
    public ResponseEntity<Void> assignRole(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String id,
            @Valid @RequestBody AssignRoleDTO roleRequest) {
        try {
            String token = tokenService.extractTokenFromHeader(authHeader);
            roleService.checkRole(token, RoleType.ADMIN);

            boolean success = roleService.assignRole(id, roleRequest.roleId());
            if (success) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Aggiorna ruolo utente", description = "Aggiorna il ruolo di un utente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ruolo aggiornato con successo"),
            @ApiResponse(responseCode = "400", description = "Dati non validi"),
            @ApiResponse(responseCode = "403", description = "Privilegi insufficienti"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato")
    })
    @PutMapping("/{id}/roles")
    public ResponseEntity<Void> updateUserRole(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String id,
            @Valid @RequestBody AssignRoleDTO roleRequest) {
        try {
            String token = tokenService.extractTokenFromHeader(authHeader);
            roleService.checkRole(token, RoleType.ADMIN);

            boolean success = roleService.assignRole(id, roleRequest.roleId());
            if (success) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
