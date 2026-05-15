package com.svalero.apicozybites.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long customerId;
    private String email;
    private List<String> roles;

    public JwtResponse(String accessToken, Long customerId, String email, List<String> roles) {
        this.token = accessToken;
        this.customerId = customerId;
        this.email = email;
        this.roles = roles;
    }
}
