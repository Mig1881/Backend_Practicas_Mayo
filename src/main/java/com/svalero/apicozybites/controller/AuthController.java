package com.svalero.apicozybites.controller;

import com.svalero.apicozybites.domain.Customer;
import com.svalero.apicozybites.domain.dto.JwtResponse;
import com.svalero.apicozybites.domain.dto.LoginDto;
import com.svalero.apicozybites.domain.dto.SignupDto;
import com.svalero.apicozybites.repository.CustomerRepository;
import com.svalero.apicozybites.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    // ENDPOINT 1: REGISTRO
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupDto signUpDto) {
        // Se comprueba si el email ya existe (ya que es nuestro identificador)
        if (customerRepository.findByEmail(signUpDto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: ¡El email ya está en uso!");
        }

        //Se crea  la cuenta del cliente, encriptando la contraseña
        Customer customer = new Customer();
        customer.setName(signUpDto.getName());
        customer.setEmail(signUpDto.getEmail());
        customer.setPhone(signUpDto.getPhone());
        customer.setPassword(encoder.encode(signUpDto.getPassword()));
        customer.setRole("USER"); // Rol por defecto
        customer.setAge(signUpDto.getAge());
        customer.setAdvertising(signUpDto.isAdvertising());
        customer.setRegistrationDate(LocalDate.now());

        customerRepository.save(customer);

        return ResponseEntity.ok("¡Cliente registrado con éxito!");
    }

    // ENDPOINT 2: LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDto loginDto) {

        //El mánager comprueba si el email y contraseña son correctos
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

        //Si es correcto, lo apuntamos en el registro de visitantes
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Fabricamos el Token JWT
        String jwt = jwtUtils.generateJwtToken(authentication);

        //Se saca los datos del usuario autenticado
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        //Se envia al cliente
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), roles));
    }
}
