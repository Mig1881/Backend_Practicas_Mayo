package com.svalero.apicozybites.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderOutDto {
    private long id;
    private LocalDate orderDate;
    private Float totalPrice;
    private CustomerOutDto customer;
    private ItemOutDto item;
}