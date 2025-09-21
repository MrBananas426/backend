package com.example.icebreaker.controllers;

import com.example.icebreaker.dto.LoginRequest;
import com.example.icebreaker.dto.LoginResponse;
import com.example.icebreaker.dto.UserDto;
import com.example.icebreaker.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        // 1) Authenticate username/password
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        // 2) Build user DTO from principal
        UserDetails principal = (UserDetails) auth.getPrincipal();
        UserDto userDto = new UserDto(null, principal.getUsername()); // 2-arg ctor (id, username)

        // 3) Generate JWT
        String token = jwtService.generateToken(principal);

        // 4) Return response with token
        return ResponseEntity.ok(new LoginResponse("Login successful", token, userDto));
    }
}


