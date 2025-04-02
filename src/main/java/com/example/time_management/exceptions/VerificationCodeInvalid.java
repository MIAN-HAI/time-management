package com.example.time_management.exceptions;

public class VerificationCodeInvalid extends RuntimeException {
    public VerificationCodeInvalid(String message) {
        super(message);
    }
}
