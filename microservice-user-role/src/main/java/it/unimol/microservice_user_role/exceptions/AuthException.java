package it.unimol.microservice_user_role.exceptions;

public class AuthException extends Exception {
    public AuthException(String message) {
        super(message);
    }
}
