package it.unimol.microservice_user_role.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserDTO(
        @NotBlank(message = "Username è richiesto")
        @Size(min = 3, max = 50, message = "Username deve essere tra 3 e 50 caratteri")
        String username,

        @NotBlank(message = "Email è richiesta")
        @Email(message = "Email deve essere valida")
        String email,

        @NotBlank(message = "Nome è richiesto")
        @Size(max = 100, message = "Nome non può superare 100 caratteri")
        String name,

        @NotBlank(message = "Cognome è richiesto")
        @Size(max = 100, message = "Cognome non può superare 100 caratteri")
        String surname,

        @NotBlank(message = "Password è richiesta")
        @Size(min = 8, message = "Password deve essere almeno 8 caratteri")
        String password,

        @NotBlank(message = "Il Ruolo è richiesto")
        @Pattern(
                regexp = "^(STUDENT|TEACHER|ADMIN|SUPER_ADMIN)$",
                message = "Il ruolo deve essere uno tra: STUDENT, TEACHER, ADMIN, SUPER_ADMIN"
        )
        String roleId
) {}
