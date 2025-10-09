package com.example.icebreaker.controllers;

import com.example.icebreaker.dto.LoginRequest;
import com.example.icebreaker.dto.LoginResponse;
import com.example.icebreaker.dto.UserDto;
import com.example.icebreaker.security.JwtUtil;
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
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        UserDetails principal = (UserDetails) auth.getPrincipal();
        UserDto userDto = new UserDto(null, principal.getUsername());

        // Generate JWT using the SAME util the filter validates with
        String token = jwtUtil.generateToken(principal.getUsername());

        return ResponseEntity.ok(new LoginResponse("Login successful", token, userDto));
    }
}