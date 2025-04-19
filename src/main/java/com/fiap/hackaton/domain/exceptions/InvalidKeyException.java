package com.fiap.hackaton.domain.exceptions;

public class InvalidKeyException extends RuntimeException {

    public InvalidKeyException() {
        super();
    }

    public InvalidKeyException(String message) {
        super(message);
    }
}
