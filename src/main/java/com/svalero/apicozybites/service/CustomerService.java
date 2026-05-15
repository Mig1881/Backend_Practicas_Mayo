package com.svalero.apicozybites.service;


import com.svalero.apicozybites.domain.Customer;
import com.svalero.apicozybites.domain.dto.CustomerInDto;
import com.svalero.apicozybites.domain.dto.CustomerOutDto;
import com.svalero.apicozybites.exception.CustomerNotFoundException;
import com.svalero.apicozybites.repository.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ModelMapper modelMapper;
    public List<CustomerOutDto> getAll(String name, String email) {
        List<Customer> customerList;

        if (name == null && email == null) {
            customerList = customerRepository.findAll();
        } else if (email == null) {
            customerList = customerRepository.findByName(name);
        } else if (name == null) {
            java.util.Optional<Customer> optCustomer = customerRepository.findByEmail(email);
            customerList = optCustomer.isPresent() ? List.of(optCustomer.get()) : java.util.List.of();
        } else {
            customerList = customerRepository.findByNameAndEmail(name, email);
        }

        return modelMapper.map(customerList, new TypeToken<List<CustomerOutDto>>() {
        }.getType());
    }

    public Customer get(long id) throws CustomerNotFoundException {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + id + " does not exist"));
    }

    public CustomerOutDto add(CustomerInDto customerInDto) {
        Customer customer = modelMapper.map(customerInDto, Customer.class);
        Customer newCustomer = customerRepository.save(customer);

        return modelMapper.map(newCustomer, CustomerOutDto.class);
    }

    public CustomerOutDto modify(long customerId, CustomerInDto customerInDto) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        modelMapper.map(customerInDto, customer);
        customerRepository.save(customer);

        return modelMapper.map(customer, CustomerOutDto.class);
    }
    public void remove(long customerId) throws CustomerNotFoundException {
        customerRepository.findById(customerId).orElseThrow(CustomerNotFoundException::new);
        customerRepository.deleteById(customerId);
    }
}
