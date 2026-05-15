package com.svalero.apicozybites.domain.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInDto {
  @NotBlank(message = "Name is a mandatory field")
  private String name;
  @NotBlank(message = "email is a mandatory field")
  private String email;
  @NotBlank(message = "phone is a mandatory field")
  private String phone;
  @NotBlank(message = "phone is a mandatory field")
  private String password;
  private String role;
  @Min(value = 1, message = "Age must be at least 1 year")
  private int age;
  private boolean advertising;
  @NotNull(message = "registrationDate is a mandatory field")
  private LocalDate registrationDate;
}