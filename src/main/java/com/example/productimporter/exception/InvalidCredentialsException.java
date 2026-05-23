package com.example.productimporter.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {

        super("Invalid login or password");
    }
}
