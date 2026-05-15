package com.svalero.apicozybites.repository;

import com.svalero.apicozybites.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long>, JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    List<Order> findAll();
    List<Order> findByOrderDate(LocalDate orderDate);
    List<Order> findByTotalPrice(Float totalprice);

    List<Order> findByOrderDateAndTotalPrice(LocalDate orderDate, Float totalPrice);

    @Modifying
    @Query("DELETE FROM UserOrder o WHERE o.customer.id = :customerId")
    void deleteByCustomerId(@Param("customerId") Long customerId);
}
