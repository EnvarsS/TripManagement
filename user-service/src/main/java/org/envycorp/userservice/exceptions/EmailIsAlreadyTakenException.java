package org.envycorp.userservice.exceptions;

public class EmailIsAlreadyTakenException extends RuntimeException {
    public EmailIsAlreadyTakenException(String message) {
        super(message);
    }
}
