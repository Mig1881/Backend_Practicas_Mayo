package com.svalero.apicozybites.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOutDto {
  private long id;
  private String name;
  private String email;
  private String phone;
  private String password;
  private String role;
  private int age;
  private boolean advertising;
  private LocalDate registrationDate;
  private String profileImageUrl;
}