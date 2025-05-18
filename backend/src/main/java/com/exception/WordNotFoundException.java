package com.exception;

public class WordNotFoundException extends RuntimeException {
    public WordNotFoundException(String message) {
        super(message);
    }

    public static WordNotFoundException withId(Long id) {
        return new WordNotFoundException("Word not found with id: " + id);
    }

    public static WordNotFoundException withText(String text) {
        return new WordNotFoundException("Word not found with text: " + text);
    }
} 