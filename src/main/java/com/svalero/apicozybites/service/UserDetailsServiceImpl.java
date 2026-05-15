package com.svalero.apicozybites.service;

import com.svalero.apicozybites.domain.Customer;
import com.svalero.apicozybites.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Usamos el EMAIL como identificador de login
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // Le añadimos el prefijo "ROLE_" que exige Spring Security por defecto
        String roleName = customer.getRole();
        if (roleName != null && !roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        } else if (roleName == null) {
            roleName = "ROLE_USER"; // Por seguridad, si está nulo le damos el rol básico
        }

        GrantedAuthority authority = new SimpleGrantedAuthority(roleName);

        // Devolvemos el objeto User interno de Spring
        return new org.springframework.security.core.userdetails.User(
                customer.getEmail(),
                customer.getPassword(),
                Collections.singletonList(authority)
        );
    }
}