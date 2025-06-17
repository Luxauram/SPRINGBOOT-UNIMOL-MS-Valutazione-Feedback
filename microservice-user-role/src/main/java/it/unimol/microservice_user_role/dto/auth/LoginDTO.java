package it.unimol.microservice_user_role.dto.auth;

public record LoginDTO(
        String username,
        String password
) {
}
