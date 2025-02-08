package it.unifi.financeapp.service.exceptions;

public class InvalidExpenseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidExpenseException(String message) {
        super(message);
    }
}