package com.svalero.apicozybites.controller;

import com.svalero.apicozybites.domain.dto.OrderInDto;
import com.svalero.apicozybites.domain.dto.OrderOutDto;
import com.svalero.apicozybites.exception.CustomerNotFoundException;
import com.svalero.apicozybites.exception.ItemNotFoundException;
import com.svalero.apicozybites.exception.OrderNotFoundException;
import com.svalero.apicozybites.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    private final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @GetMapping
    public ResponseEntity<List<OrderOutDto>> getAll(@RequestParam(value = "orderDate", required = false) LocalDate orderDate,
                                                    @RequestParam(value = "totalPrice", required = false) Float totalPrice)  {
        logger.info("Begin Get all Orders");
        List<OrderOutDto> orders = orderService.getAll(orderDate, totalPrice);
        logger.info("End Get all Orders");
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderOutDto> getOrder(@PathVariable long orderId) throws OrderNotFoundException {
        logger.info("Begin Get Order");
        OrderOutDto orderOutDto = orderService.get(orderId);
        logger.info("Fetching order with id: {}", orderId);
        return new ResponseEntity<>(orderOutDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<OrderOutDto> addOrder(@RequestBody @Valid OrderInDto order)throws CustomerNotFoundException, ItemNotFoundException {
        logger.info("Adding new Order");
        OrderOutDto newOrder = orderService.add(order);
        logger.info("End adding new Order");
        return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> removeOrder(@PathVariable long orderId) throws OrderNotFoundException {
        logger.info("Deleting Order with id: {}", orderId);
        orderService.remove(orderId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderOutDto> modifyItem(@PathVariable @Valid long orderId, @RequestBody OrderInDto order) throws OrderNotFoundException, CustomerNotFoundException, ItemNotFoundException {
        logger.info("Begin Modify Order");
        OrderOutDto modifiedOrder = orderService.modify(orderId, order);
        logger.info("End Modify Order");
        return new ResponseEntity<>(modifiedOrder, HttpStatus.OK);
    }
}
