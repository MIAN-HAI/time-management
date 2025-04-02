package com.example.time_management.exceptions;

public class UpdateObjectNotExists extends RuntimeException {
    public UpdateObjectNotExists(String message) {
        super(message);
    }
}
