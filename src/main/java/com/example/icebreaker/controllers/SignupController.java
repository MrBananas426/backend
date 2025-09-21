package com.example.icebreaker.controllers;

import com.example.icebreaker.models.Account;
import com.example.icebreaker.models.AccountDTO;
import com.example.icebreaker.models.Card;
import com.example.icebreaker.repositories.AccountRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
public class SignupController {

    private final AccountRepository accounts;
    private final PasswordEncoder encoder;

    public SignupController(AccountRepository accounts, PasswordEncoder encoder) {
        this.accounts = accounts;
        this.encoder = encoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody AccountDTO dto) {
        if (dto.getEmail() == null || dto.getPassword() == null) {
            return ResponseEntity.badRequest().body("email and password are required");
        }
        if (accounts.findByEmail(dto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered.");
        }

    Account a = new Account();
        a.setEmail(dto.getEmail());
        a.setPassword(encoder.encode(dto.getPassword())); // BCrypt
        a.setRole("USER");
        a.setProvider("local");
        a.setUserId("user-" + System.currentTimeMillis());
        a.setCreatedAt(LocalDate.now().toString());
        a.setBirthday(dto.getBirthday());
        a.setGender(dto.getGender());

    Card c = new Card();
        c.setFirstName(dto.getFirstName());
        c.setLastName(dto.getLastName());
        c.setVisibility("PUBLIC");
        a.setCard(c);

        accounts.save(a);
        return ResponseEntity.ok("Signed up");
    }
}
