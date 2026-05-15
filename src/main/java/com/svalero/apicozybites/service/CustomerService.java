package com.svalero.apicozybites.service;


import com.svalero.apicozybites.domain.Customer;
import com.svalero.apicozybites.domain.dto.CustomerInDto;
import com.svalero.apicozybites.domain.dto.CustomerOutDto;
import com.svalero.apicozybites.domain.dto.CustomerProfileUpdateDto;
import com.svalero.apicozybites.exception.CustomerNotFoundException;
import com.svalero.apicozybites.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import com.svalero.apicozybites.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

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

    public CustomerOutDto getProfile(String email) throws CustomerNotFoundException {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(CustomerNotFoundException::new);

        return modelMapper.map(customer, CustomerOutDto.class);
    }

    public CustomerOutDto updateProfile(String email, CustomerProfileUpdateDto profileDto)
            throws CustomerNotFoundException {

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(CustomerNotFoundException::new);

        customer.setName(profileDto.getName());
        customer.setPhone(profileDto.getPhone());
        customer.setAge(profileDto.getAge());
        customer.setAdvertising(profileDto.isAdvertising());
        customer.setProfileImageUrl(profileDto.getProfileImageUrl());

        Customer updatedCustomer = customerRepository.save(customer);

        return modelMapper.map(updatedCustomer, CustomerOutDto.class);
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


    // Metodo para que ADMIN elimine los pedidos asociados a un cliente:
    // Así cuando en el cliente el admin borre customer:
    // 1. Busca el cliente por su ID, si no lo encuentra lanza una excepción.
    // 2. Si el cliente existe, elimina todos los pedidos asociados a ese cliente utilizando
    //    el metodo deleteByCustomerId del OrderRepository.
    // 3. Finalmente, elimina el cliente de la base de datos.
    @Transactional
    public void remove(long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        orderRepository.deleteByCustomerId(customerId);

        customerRepository.delete(customer);
    }

    // Metodo para que el cliente elimine su perfil, eliminando también los pedidos asociados a él.
    // Uso metodo distinto al anterior, ya que por seguridad el cliente no debería tener acceso al ID de su cuenta,
    // sino que se identifica por su email.
    @Transactional
    public void deleteProfile(String email) throws CustomerNotFoundException {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(CustomerNotFoundException::new);

        orderRepository.deleteByCustomerId(customer.getId());

        customerRepository.delete(customer);
    }


    // Metodo original para eliminar un cliente sin eliminar los pedidos asociados:
//    public void remove(long customerId) throws CustomerNotFoundException {
//        customerRepository.findById(customerId).orElseThrow(CustomerNotFoundException::new);
//        customerRepository.deleteById(customerId);
//    }

}