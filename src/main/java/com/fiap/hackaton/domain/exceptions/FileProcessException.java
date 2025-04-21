package com.fiap.hackaton.domain.exceptions;

public class FileProcessException extends RuntimeException {

    public FileProcessException() {
        super();
    }

    public FileProcessException(String message) {
        super(message);
    }

    public FileProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}
