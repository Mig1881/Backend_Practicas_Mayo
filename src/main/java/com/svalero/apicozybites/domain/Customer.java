package com.svalero.apicozybites.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    @Column
    private String phone;
    @Column
    private String password;
    @Column
    @ColumnDefault("'USER'")
    private String role = "USER";
    @Column
    private int age;
    @Column
    @ColumnDefault("FALSE")
    private boolean advertising = false;
    @Column
    private LocalDate registrationDate;
    //
    @OneToMany(mappedBy = "customer")
    @JsonBackReference(value="customers_orders")
    private List<Order> orders;
}
