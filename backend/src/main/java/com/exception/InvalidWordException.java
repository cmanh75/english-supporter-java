package com.exception;

public class InvalidWordException extends RuntimeException {
    public InvalidWordException(String message) {
        super(message);
    }
} 