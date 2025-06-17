package it.unimol.microservice_user_role.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileDTO(
        @Size(min = 3, max = 50, message = "Username deve essere tra 3 e 50 caratteri")
        String username,

        @Email(message = "Email deve essere valida")
        String email,

        @Size(max = 100, message = "Nome non può superare 100 caratteri")
        String name,

        @Size(max = 100, message = "Cognome non può superare 100 caratteri")
        String surname
) {}
