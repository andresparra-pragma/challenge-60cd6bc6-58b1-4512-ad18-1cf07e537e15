package com.library.exception;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String email) {
        super("No existe un usuario con el email '" + email + "'");
    }
}
