package com.example.icebreaker.services;

import com.example.icebreaker.models.Account;
import com.example.icebreaker.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository AccountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = AccountRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder()
            .username(account.getEmail())
            .password(account.getPassword()) // should already be encrypted!
            .roles("USER") // or dynamic roles from DB
            .build();
    }
}
