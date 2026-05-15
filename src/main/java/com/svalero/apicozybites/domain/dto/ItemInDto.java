package com.svalero.apicozybites.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemInDto {
    @NotBlank(message = "Name is a mandatory field")
    private String name;
    @NotBlank(message = "Description is a mandatory field")
    private String description;
    private Float price;
    private Boolean isNew;
    private LocalDate releaseDate;

    // Campo para recibir la imagen (en formato byte[])
    private byte[] image;
}