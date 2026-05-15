package com.svalero.apicozybites.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderInDto {
    @NotNull(message = "Order date is required")
    private LocalDate orderDate;
    @NotNull(message = "Total Price is required")
    private Float totalPrice;
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    @NotNull(message = "Item ID is required")
    private Long itemId;
}