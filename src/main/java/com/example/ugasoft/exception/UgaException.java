package com.example.ugasoft.exception;

import lombok.Getter;

@Getter
public class UgaException extends RuntimeException {

    private final String message;

    public UgaException(String message) {
        super(message);
        this.message = message;
    }
}
