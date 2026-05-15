package com.svalero.apicozybites.exception;

public class ImageNotFoundException extends Exception {

    public ImageNotFoundException() {
        super("This item haves no image");
    }

    public ImageNotFoundException(String message) {
        super(message);
    }
}
