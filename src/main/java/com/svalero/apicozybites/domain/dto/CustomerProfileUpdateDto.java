package com.svalero.apicozybites.domain.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CustomerProfileUpdateDto {
    private String name;
    private String phone;

    @Positive(message = "Age must be a positive number")
    private int age;

    private boolean advertising;

    private String profileImageUrl;
}