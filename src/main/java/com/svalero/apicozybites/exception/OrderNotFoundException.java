package com.svalero.apicozybites.exception;

public class OrderNotFoundException extends Exception {

    public OrderNotFoundException() {
        super("Order not found");
    }

    public OrderNotFoundException(String message) {
        super(message);
    }
}
