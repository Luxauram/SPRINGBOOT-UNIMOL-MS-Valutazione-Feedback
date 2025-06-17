package it.unimol.microservice_user_role.dto.user;

import it.unimol.microservice_user_role.dto.role.RoleDTO;
import java.time.LocalDateTime;

public record UserDTO(
        String id,
        String username,
        String email,
        String name,
        String surname,
        LocalDateTime createdAt,
        LocalDateTime lastLogin,
        RoleDTO role
) {
    public String getRoleName() {
        return role != null ? role.name() : null;
    }
}
