package com.svalero.apicozybites.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "UserOrder")
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;
    @Column(name = "total_price")
    private Float totalPrice;
    //
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    //
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
}
