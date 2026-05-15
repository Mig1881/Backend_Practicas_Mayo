package com.svalero.apicozybites.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SignupDto {
    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    private String phone;
    @Positive(message = "La edad debe ser un número positivo")
    private int age;

    private boolean advertising;
}
