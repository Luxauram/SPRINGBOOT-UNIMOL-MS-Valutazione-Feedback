package it.unimol.microservice_user_role.dto.converter;

import it.unimol.microservice_user_role.dto.user.CreateUserDTO;
import it.unimol.microservice_user_role.dto.user.UserDTO;
import it.unimol.microservice_user_role.dto.user.UserProfileDTO;
import it.unimol.microservice_user_role.dto.user.UpdateUserProfileDTO;
import it.unimol.microservice_user_role.model.User;
import it.unimol.microservice_user_role.model.Role;
import it.unimol.microservice_user_role.util.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class UserConverter {

    @Autowired
    private RoleConverter roleConverter;


    // Converte CreateUserDto in User entity
    public User toEntity(CreateUserDTO dto, Role role) {
        if (dto == null) return null;

        String userId = UUID.randomUUID().toString();
        String hashedPassword = PasswordUtils.hashPassword(dto.password());

        return new User(
                userId,
                dto.username(),
                dto.email(),
                dto.name(),
                dto.surname(),
                hashedPassword,
                role
        );
    }

    // Converte User entity in UserDto (completo)
    public UserDTO toDto(User user) {
        if (user == null) return null;

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getSurname(),
                user.getCreatedAt(),
                user.getLastLogin(),
                roleConverter.toDto(user.getRole())
        );
    }

    // Converte User entity in UserProfileDto (pubblico)
    public UserProfileDTO toProfileDto(User user) {
        if (user == null) return null;

        return new UserProfileDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getSurname(),
                user.getRoleName(),
                user.getCreatedAt(),
                user.getLastLogin()
        );
    }

    // Aggiorna User entity con UpdateUserProfileDto
    public void updateEntity(User user, UpdateUserProfileDTO dto) {
        if (user == null || dto == null) return;

        if (dto.username() != null && !dto.username().isBlank()) {
            user.setUsername(dto.username());
        }
        if (dto.email() != null && !dto.email().isBlank()) {
            user.setEmail(dto.email());
        }
        if (dto.name() != null && !dto.name().isBlank()) {
            user.setName(dto.name());
        }
        if (dto.surname() != null && !dto.surname().isBlank()) {
            user.setSurname(dto.surname());
        }
    }

    // Metodo per aggiornare solo i campi non null/blank
    public void updateEntitySelective(User user, UpdateUserProfileDTO dto) {
        updateEntity(user, dto);
    }
}