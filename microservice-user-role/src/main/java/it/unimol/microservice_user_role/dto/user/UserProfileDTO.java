package it.unimol.microservice_user_role.dto.user;

import java.time.LocalDateTime;

public record UserProfileDTO(
        String id,
        String username,
        String email,
        String name,
        String surname,
        String roleName,
        LocalDateTime createdAt,
        LocalDateTime lastLogin
) {
}
