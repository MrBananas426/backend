package com.example.icebreaker.controllers;

import com.example.icebreaker.models.Account;
import com.example.icebreaker.models.CardDTO;
import com.example.icebreaker.repositories.AccountRepository;
import com.example.icebreaker.services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class MainController {

    public final AccountRepository accountRepository;
    public final ProfileService profileService;

    @GetMapping("/actuator/ping")
    public String ping() {
        return "ok";
    }

    @GetMapping("/")
    public String home() {
        return "hello";
    }

    @PostMapping("/dev/login-legacy")
    public ResponseEntity<String> login(@AuthenticationPrincipal Account account) {
        // FIX: remove invalid argument; getEmail() takes no parameters
        System.out.println("User logged in: " + (account != null ? account.getEmail() : "anonymous"));
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/card/edit")
    public CompletableFuture<ResponseEntity<String>> editCard(
            @RequestBody CardDTO dto,
            @AuthenticationPrincipal Account account) {

        if (account == null) {
            return CompletableFuture.completedFuture(ResponseEntity.status(401).body("unauthorized"));
        }

        return profileService.editCard(dto, account)
                .thenApply(msg -> ResponseEntity.ok(msg));
    }
}
