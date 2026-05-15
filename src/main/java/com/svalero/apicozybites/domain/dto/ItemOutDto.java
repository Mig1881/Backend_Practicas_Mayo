package com.svalero.apicozybites.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemOutDto {
    private long id;
    private String name;
    private String description;
    private Float price;
    private Boolean isNew;
    private LocalDate releaseDate;

    // Atributo para cargar la imagen
    private String imageUrl;
}