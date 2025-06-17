package it.unimol.microservice_user_role.dto.auth;

public record TokenDTO(
        String token,
        String type,
        long expiresIn
) {
    public TokenDTO(String token) {
        this(token, "Bearer", 0L);
    }

    public TokenDTO(String token, long expiresIn) {
        this(token, "Bearer", expiresIn);
    }
}
