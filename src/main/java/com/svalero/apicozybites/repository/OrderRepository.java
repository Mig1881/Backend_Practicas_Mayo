package com.svalero.apicozybites.repository;

import com.svalero.apicozybites.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long>, JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    List<Order> findAll();
    List<Order> findByOrderDate(LocalDate orderDate);
    List<Order> findByTotalPrice(Float totalprice);

    List<Order> findByOrderDateAndTotalPrice(LocalDate orderDate, Float totalPrice);

}