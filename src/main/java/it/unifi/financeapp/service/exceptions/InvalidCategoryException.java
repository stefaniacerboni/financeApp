package it.unifi.financeapp.service.exceptions;

public class InvalidCategoryException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidCategoryException(String message) {
        super(message);
    }
}

