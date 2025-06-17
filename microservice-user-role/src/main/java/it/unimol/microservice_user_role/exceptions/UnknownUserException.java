package it.unimol.microservice_user_role.exceptions;

public class UnknownUserException extends Exception {
    public UnknownUserException(String message) {
        super(message);
    }
}
