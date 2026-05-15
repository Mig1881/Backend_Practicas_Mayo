package com.svalero.apicozybites.controller;


import com.svalero.apicozybites.domain.Customer;
import com.svalero.apicozybites.domain.dto.CustomerInDto;
import com.svalero.apicozybites.domain.dto.CustomerOutDto;
import com.svalero.apicozybites.domain.dto.CustomerProfileUpdateDto;
import com.svalero.apicozybites.exception.CustomerNotFoundException;
import com.svalero.apicozybites.service.CustomerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    private final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @GetMapping
    public ResponseEntity<List<CustomerOutDto>> getAll(@RequestParam(value = "name", required = false) String name,
                                                       @RequestParam(value = "email", required = false) String email) {

        List<CustomerOutDto> customers = customerService.getAll(name, email);
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<CustomerOutDto> getMyProfile(Authentication authentication)
            throws CustomerNotFoundException {

        CustomerOutDto customer = customerService.getProfile(authentication.getName());
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    @PutMapping("/me")
    public ResponseEntity<CustomerOutDto> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody CustomerProfileUpdateDto profileDto)
            throws CustomerNotFoundException {

        CustomerOutDto updatedCustomer = customerService.updateProfile(
                authentication.getName(),
                profileDto
        );

        return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyProfile(Authentication authentication)
            throws CustomerNotFoundException {

        customerService.deleteProfile(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomer(@PathVariable long customerId) throws CustomerNotFoundException {
        Customer customer = customerService.get(customerId);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    //cambio para capturar el error 409 que es el de campo unique asi, puedo capturarlo en android
    @PostMapping
    public ResponseEntity<?> addCustomer(@Valid @RequestBody CustomerInDto customerInDto) {
        try {
            CustomerOutDto newCustomer = customerService.add(customerInDto);
            return new ResponseEntity<>(newCustomer, HttpStatus.CREATED);

        } catch (DataIntegrityViolationException e) {
            // Esto captura el error SQL de "Duplicate entry" y devuelve un 409 Conflict
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El email o el nombre de usuario ya existen en la base de datos.");
        }
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerOutDto> modifyCustomer(@PathVariable long customerId,@Valid  @RequestBody CustomerInDto customerInDto) throws CustomerNotFoundException {
        CustomerOutDto modifiedCustomer = customerService.modify(customerId, customerInDto);
        return new ResponseEntity<>(modifiedCustomer, HttpStatus.OK);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> removeCustomer(@PathVariable long customerId) throws CustomerNotFoundException{
        customerService.remove(customerId);
        return ResponseEntity.noContent().build();
    }
}

