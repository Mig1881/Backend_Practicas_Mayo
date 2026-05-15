package com.svalero.apicozybites.service;

import com.svalero.apicozybites.domain.Customer;
import com.svalero.apicozybites.domain.Item;
import com.svalero.apicozybites.domain.Order;
import com.svalero.apicozybites.domain.dto.CustomerOutDto;
import com.svalero.apicozybites.domain.dto.ItemOutDto;
import com.svalero.apicozybites.domain.dto.OrderInDto;
import com.svalero.apicozybites.domain.dto.OrderOutDto;
import com.svalero.apicozybites.exception.CustomerNotFoundException;
import com.svalero.apicozybites.exception.ItemNotFoundException;
import com.svalero.apicozybites.exception.OrderNotFoundException;
import com.svalero.apicozybites.repository.CustomerRepository;
import com.svalero.apicozybites.repository.ItemRepository;
import com.svalero.apicozybites.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ModelMapper modelMapper;

    // --- MÉTODO GET ALL CORREGIDO ---
    public List<OrderOutDto> getAll(LocalDate orderDate, Float totalPrice) {
        List<Order> orderList;

        if (orderDate != null && totalPrice != null) {
            orderList = orderRepository.findByOrderDateAndTotalPrice(orderDate, totalPrice);
        } else if (orderDate != null) {
            orderList = orderRepository.findByOrderDate(orderDate);
        } else if (totalPrice != null) {
            orderList = orderRepository.findByTotalPrice(totalPrice);
        } else {
            orderList = orderRepository.findAll();
        }

        // Usamos stream() para convertir cada orden usando nuestro método auxiliar
        return orderList.stream()
                .map(this::mapToDto) // <--- Aquí está la magia
                .collect(Collectors.toList());
    }

    public OrderOutDto get(long id) throws OrderNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(OrderNotFoundException::new);
        return mapToDto(order);
    }

    public OrderOutDto add(OrderInDto orderInDto) throws CustomerNotFoundException, ItemNotFoundException {
        Order order = modelMapper.map(orderInDto, Order.class);

        if (orderInDto.getCustomerId() != null) {
            Customer customer = customerRepository.findById(orderInDto.getCustomerId())
                    .orElseThrow(CustomerNotFoundException::new);
            order.setCustomer(customer);
        }

        if (orderInDto.getItemId() != null) {
            Item item = itemRepository.findById(orderInDto.getItemId())
                    .orElseThrow(ItemNotFoundException::new);
            order.setItem(item);
        }

        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDate.now());
        }

        Order newOrder = orderRepository.save(order);
        return mapToDto(newOrder);
    }

    public OrderOutDto modify(long orderId, OrderInDto orderInDto) throws OrderNotFoundException, CustomerNotFoundException, ItemNotFoundException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        modelMapper.map(orderInDto, order);

        if (orderInDto.getCustomerId() != null) {
            Customer customer = customerRepository.findById(orderInDto.getCustomerId())
                    .orElseThrow(CustomerNotFoundException::new);
            order.setCustomer(customer);
        }

        if (orderInDto.getItemId() != null) {
            Item item = itemRepository.findById(orderInDto.getItemId())
                    .orElseThrow(ItemNotFoundException::new);
            order.setItem(item);
        }

        Order newOrder = orderRepository.save(order);
        return mapToDto(newOrder);
    }

    public void remove(long orderId) throws OrderNotFoundException {
        orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
        orderRepository.deleteById(orderId);
    }

    private OrderOutDto mapToDto(Order order) {
        OrderOutDto outDto = modelMapper.map(order, OrderOutDto.class);

        // Mapeamos y asignamos el cliente completo al DTO
        if (order.getCustomer() != null) {
            CustomerOutDto customerDto = modelMapper.map(order.getCustomer(), com.svalero.apicozybites.domain.dto.CustomerOutDto.class);
            outDto.setCustomer(customerDto);
        }

        // Mapeamos y asignamos el producto completo al DTO
        if (order.getItem() != null) {
            ItemOutDto itemDto = modelMapper.map(order.getItem(), com.svalero.apicozybites.domain.dto.ItemOutDto.class);
            outDto.setItem(itemDto);
        }

        return outDto;
    }
}