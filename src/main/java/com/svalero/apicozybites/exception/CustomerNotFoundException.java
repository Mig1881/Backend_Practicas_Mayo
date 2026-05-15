package com.svalero.apicozybites.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Customer not found")
public class CustomerNotFoundException extends Exception {

  public CustomerNotFoundException() {
    super("This customer does not exist");
  }

  public CustomerNotFoundException(String message) {
    super(message);
  }
}