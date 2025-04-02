package com.example.time_management.exceptions;

public class VerificationCodeError extends RuntimeException {
    public VerificationCodeError(String message) {
        super(message);
    }
}
