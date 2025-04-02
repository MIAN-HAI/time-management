package com.example.time_management.exceptions;

public class UserNeverExsits extends RuntimeException {
    public UserNeverExsits(String message) {
        super(message);
    }
}
