package it.unimol.microservice_user_role.dto.auth;

public record ChangePasswordRequestDTO(
        String currentPassword,
        String newPassword
) {
}
