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
@Entity(name="items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private Float price;
    @Column
    @ColumnDefault("TRUE")
    private Boolean isNew = true;
    @Column
    private LocalDate releaseDate;
    //
    @OneToMany(mappedBy = "item")
    @JsonBackReference(value="items_orders")
    private List<Order> orders;
}