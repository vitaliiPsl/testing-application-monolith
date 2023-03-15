package com.example.testing.exceptions;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String msg) {
        super(msg);
    }
}
