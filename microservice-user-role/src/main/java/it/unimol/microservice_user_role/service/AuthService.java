package it.unimol.microservice_user_role.service;

import it.unimol.microservice_user_role.dto.auth.TokenDTO;
import it.unimol.microservice_user_role.dto.user.UserDTO;
import it.unimol.microservice_user_role.dto.converter.UserConverter;
import it.unimol.microservice_user_role.exceptions.AuthException;
import it.unimol.microservice_user_role.exceptions.UnknownUserException;
import it.unimol.microservice_user_role.model.User;
import it.unimol.microservice_user_role.repository.UserRepository;
import it.unimol.microservice_user_role.util.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private TokenJWTService tokenService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserConverter userConverter;
    @Autowired
    private MessageService messageService;

    /**
     * Registra un nuovo utente nel sistema.
     * 
     * @param user L'utente da registrare.
     * @throws AuthException Se l'utente esiste già o se viene tentato di registrare un super admin.
     */
    public void register(User user) throws AuthException {
        try {
            if (user.getRole().getId().equals("sadmin")) {
                throw new AuthException("Ehh, volevi!");
            }

            String password = PasswordUtils.hashPassword(user.getPassword());
            user.setPassword(password);
            userRepository.save(user);

            UserDTO userDto = userConverter.toDto(user);
            messageService.publishUserCreated(userDto);
        } catch (Exception e) {
            throw new AuthException(e.getMessage());
        }
    }

    /**
     * Effettua il login di un utente nel sistema.
     * 
     * @param username L'username dell'utente.
     * @param password La password dell'utente.
     * @return Un oggetto TokenJWTDto contenente il token JWT generato.
     * @throws AuthException Se l'autenticazione fallisce a causa di credenziali non valide.
     * @throws UnknownUserException Se l'utente non esiste nel sistema.
     */
    public TokenDTO login(String username, String password) throws AuthException, UnknownUserException {
        Optional<User> existsUser = userRepository.findByUsername(username);
        if (existsUser.isPresent()) {
            User user = existsUser.get();
            if (PasswordUtils.verificaPassword(user.getPassword(), password)) {
                user.setLastLogin(LocalDateTime.now());

                userRepository.save(user);
                return tokenService.generateToken(user.getId(), user.getUsername(), user.getRole().getId());
            }
        }
        throw new AuthException("Username o password non valida");
    }

    /**
     * Effettua il logout dell'utente invalidando il token.
     * 
     * @param token Il token JWT da invalidare.
     */
    public void logout(String token) {
        tokenService.invalidateToken(token);
    }

    /**
     * Rinnova il token JWT dell'utente.
     * 
     * @param token Il token JWT da rinnovare.
     * @return Un oggetto TokenJWTDto contenente il nuovo token JWT.
     * @throws RuntimeException Se il rinnovo del token fallisce.
     */
    public TokenDTO refreshToken(String token) throws RuntimeException {
        return tokenService.refreshToken(token);
    }

    /**
     * Aggiorna ultimo login
     */
    public void updateLastLogin(String userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.updateLastLogin();
            userRepository.save(user);
        });
    }
}
