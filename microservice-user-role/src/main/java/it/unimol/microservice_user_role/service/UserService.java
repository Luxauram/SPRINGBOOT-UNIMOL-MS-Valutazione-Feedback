package it.unimol.microservice_user_role.service;

import it.unimol.microservice_user_role.dto.user.CreateUserDTO;
import it.unimol.microservice_user_role.dto.user.UserProfileDTO;
import it.unimol.microservice_user_role.dto.user.UpdateUserProfileDTO;
import it.unimol.microservice_user_role.dto.user.UserDTO;
import it.unimol.microservice_user_role.dto.converter.UserConverter;
import it.unimol.microservice_user_role.exceptions.UnknownUserException;
import it.unimol.microservice_user_role.exceptions.InvalidRequestException;
import it.unimol.microservice_user_role.model.Role;
import it.unimol.microservice_user_role.model.User;
import it.unimol.microservice_user_role.repository.RoleRepository;
import it.unimol.microservice_user_role.repository.UserRepository;
import it.unimol.microservice_user_role.util.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private TokenJWTService tokenService;

    @Autowired
    private MessageService messageService;

    /**
     * Crea un SuperAdmin se non esiste già.
     *
     * @param request Dati per la creazione del SuperAdmin.
     * @return UserDto rappresentante il SuperAdmin creato.
     * @throws InvalidRequestException Se un SuperAdmin esiste già.
     */
    public UserDTO createSuperAdminIfNotExists(CreateUserDTO request) throws InvalidRequestException {
        if (userRepository.countSuperAdmins() > 0) {
            throw new InvalidRequestException("SuperAdmin già esistente");
        }

        Role superAdminRole = roleRepository.findById("SUPER_ADMIN")
                .orElseThrow(() -> new InvalidRequestException("Ruolo SUPER_ADMIN non trovato"));

        User superAdmin = userConverter.toEntity(request, superAdminRole);
        superAdmin.setId("000000");

        User savedUser = userRepository.save(superAdmin);
        UserDTO userDto = userConverter.toDto(savedUser);

        messageService.publishUserCreated(userDto);
        return userDto;
    }

    /**
     * Crea un nuovo utente
     */
    public UserDTO createUser(CreateUserDTO request) throws InvalidRequestException {
        if (userRepository.existsByUsername(request.username())) {
            throw new InvalidRequestException("Username già esistente");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new InvalidRequestException("Email già esistente");
        }

        Role role;
        if (request.roleId() != null) {
            role = roleRepository.findById(request.roleId())
                    .orElseThrow(() -> new InvalidRequestException("Ruolo non trovato"));
        } else {
            role = roleRepository.findById("STUDENT")
                    .orElseThrow(() -> new InvalidRequestException("Ruolo di default non trovato"));
        }

        User user = userConverter.toEntity(request, role);
        User savedUser = userRepository.save(user);
        UserDTO userDto = userConverter.toDto(savedUser);

        messageService.publishUserCreated(userDto);
        return userDto;
    }

    /**
     * Recupera tutti gli utenti dal database e li converte in UserProfileDto.
     * 
     * @return Lista di UserProfileDto contenente i profili di tutti gli utenti.
     */
    @Transactional(readOnly = true)
    public List<UserProfileDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userConverter::toProfileDto)
                .collect(Collectors.toList());
    }

    /**
     * Trova un utente per ID.
     * 
     * @param id ID dell'utente da cercare.
     * @return UserDto rappresentante l'utente trovato.
     * @throws UnknownUserException Se l'utente non viene trovato.
     */
    @Transactional(readOnly = true)
    public UserDTO findById(String id) throws UnknownUserException {
        System.out.println("=== UserService.findById ===");
        System.out.println("ID ricevuto: " + id);

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            System.out.println("Utente NON trovato nel database");
            throw new UnknownUserException("Utente non trovato");
        }

        User user = userOpt.get();
        System.out.println("Utente trovato: " + user.getUsername());
        System.out.println("Role utente: " + (user.getRole() != null ? user.getRole().getName() : "NULL"));

        try {
            System.out.println("=== Conversione a DTO ===");
            UserDTO dto = userConverter.toDto(user);
            System.out.println("Conversione completata con successo");
            return dto;
        } catch (Exception e) {
            System.out.println("ERRORE nella conversione DTO: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Trova un utente per username.
     * 
     * @param username Nome utente da cercare.
     * @return UserDto rappresentante l'utente trovato.
     * @throws UnknownUserException Se l'utente non viene trovato.
     */
    @Transactional(readOnly = true)
    public UserDTO findByUsername(String username) throws UnknownUserException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnknownUserException("Username non trovato"));
        return userConverter.toDto(user);
    }

    /**
     * Verifica se un utente con l'ID specificato esiste nel database.
     *
     * @param id ID dell'utente da verificare.
     * @return true se l'utente esiste, false altrimenti.
     */
    @Transactional(readOnly = true)
    public boolean existsById(String id) {
        return userRepository.existsById(id);
    }

    /**
     * Aggiorna i dati di un utente esistente.
     * 
     * @param userId ID dell'utente da aggiornare.
     * @param updateData Dati aggiornati dell'utente.
     * @return UserDto rappresentante l'utente aggiornato.
     * @throws UnknownUserException Se l'utente non viene trovato.
     */
    public UserDTO updateUser(String userId, UpdateUserProfileDTO updateData) throws UnknownUserException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnknownUserException("Utente non trovato"));

        userConverter.updateEntity(user, updateData);
        User savedUser = userRepository.save(user);
        UserDTO userDto = userConverter.toDto(savedUser);

        messageService.publishUserUpdated(userDto);
        return userDto;
    }

    /**
     * Elimina un utente dal database.
     * 
     * @param userId ID dell'utente da eliminare.
     * @return true se l'utente è stato eliminato, false se l'utente non esiste.
     */
    public boolean deleteUser(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            messageService.publishUserDeleted(userId);
            return true;
        }
        return false;
    }

    /**
     * Recupera il profilo utente corrente basato sul token JWT.
     * 
     * @param token Token JWT dell'utente.
     * @return UserProfileDto contenente i dettagli del profilo utente.
     * @throws UnknownUserException Se l'utente non viene trovato.
     */
    @Transactional(readOnly = true)
    public UserProfileDTO getCurrentUserProfile(String token) throws UnknownUserException {
        String userId = tokenService.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnknownUserException("Utente non trovato"));

        return userConverter.toProfileDto(user);
    }

    /**
     * Aggiorna il profilo utente corrente basato sul token JWT.
     * 
     * @param token Token JWT dell'utente.
     * @param updateData Dati aggiornati dell'utente.
     * @throws UnknownUserException Se l'utente non viene trovato.
     */
    public UserProfileDTO updateCurrentUserProfile(String token, UpdateUserProfileDTO updateData) throws UnknownUserException {
        String userId = tokenService.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnknownUserException("Utente non trovato"));

        userConverter.updateEntity(user, updateData);
        User savedUser = userRepository.save(user);
        UserProfileDTO profile = userConverter.toProfileDto(savedUser);

        messageService.publishProfileUpdated(profile);
        return profile;
    }

    /**
     * Cambia la password dell'utente corrente basato sul token JWT.
     *
     * @param token Token JWT dell'utente.
     * @param currentPassword Password attuale dell'utente.
     * @param newPassword Nuova password da impostare.
     * @return true se la password è stata cambiata con successo, false altrimenti.
     * @throws UnknownUserException Se l'utente non viene trovato.
     */
    public boolean changePassword(String token, String currentPassword, String newPassword) throws UnknownUserException {
        String userId = tokenService.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnknownUserException("Utente non trovato"));

        if (!PasswordUtils.verificaPassword(user.getPassword(), currentPassword)) {
            return false;
        }

        user.setPassword(PasswordUtils.hashPassword(newPassword));
        userRepository.save(user);

        return true;
    }

    /**
     * Resetta la password dell'utente corrente basato sul token JWT.
     * 
     * @param token Token JWT dell'utente.
     * @param currentPassword Password attuale dell'utente.
     * @throws UnknownUserException Se l'utente non viene trovato.
     * @throws SecurityException Se la password attuale non corrisponde.
     */
    public String resetPassword(String token, String currentPassword) throws UnknownUserException {
        String userId = tokenService.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnknownUserException("Utente non trovato"));

        if (!PasswordUtils.verificaPassword(user.getPassword(), currentPassword)) {
            throw new SecurityException("Password corrente errata");
        }

        String tempPassword = generateTemporaryPassword();
        user.setPassword(PasswordUtils.hashPassword(tempPassword));
        userRepository.save(user);

        return tempPassword;
    }

    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 12);
    }
}
