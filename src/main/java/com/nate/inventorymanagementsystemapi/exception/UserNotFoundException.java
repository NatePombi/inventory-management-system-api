package com.nate.inventorymanagementsystemapi.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super("User with username " + username + " not found");
    }

    public UserNotFoundException(Long id) {
        super("User with ID " + id + " not found");
    }
}
