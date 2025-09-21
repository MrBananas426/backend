package com.example.icebreaker.controllers;

import com.example.icebreaker.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class UserController {

    /**
     * Returns the authenticated user's basic info.
     * Requires a valid JWT (Authorization: Bearer <token>).
     * SecurityConfig permits /login and /signup; all others (including /me) are authenticated.
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Map authorities to a simple Set<String> of role names
        Set<String> roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // Your UserDto has (id, username, email, roles) and also a 2-arg (id, username) ctor.
        // We'll use the 4-arg ctor to include roles; email is unknown here -> null.
        UserDto dto = new UserDto(null, principal.getUsername(), null, roles);

        return ResponseEntity.ok(dto);
    }
}
