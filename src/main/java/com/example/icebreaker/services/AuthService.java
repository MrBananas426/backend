package com.example.icebreaker.services;


import com.example.icebreaker.impl.Generator;
import com.example.icebreaker.models.*;
import com.example.icebreaker.repositories.AccountRepository;
import com.example.icebreaker.repositories.CardRepository;
import com.example.icebreaker.repositories.ImageRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenBasedRememberMeServices rememberMeServices;
    private final CardRepository cardRepository;
    private final ImageRepository imageRepository;
    @Async
    public CompletableFuture<String> login(String email, String password, HttpServletRequest request, HttpServletResponse response) {

    String respond = "";
    if (accountRepository.findByEmail(email).isPresent()) {

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
            rememberMeServices.loginSuccess(request, response, auth);

            respond = "success";
        } catch (Exception e) {
            respond = "password incorrect";
        }


    } else {
        respond = "email not found";
    }

    return CompletableFuture.completedFuture(respond);

}

@Async
    public CompletableFuture<String> signup(AccountDTO account1, HttpServletRequest request, HttpServletResponse response) {

    String email = account1.getEmail();
    String password = account1.getPassword();
    String firstName = account1.getFirstName();
    String lastName = account1.getLastName();
    String gender = account1.getGender();
    String birthday = account1.getBirthday();

    String respond = "";
    if (accountRepository.findByEmail(email).isPresent()) {

        respond = "email already exists";

    } else {

        Account account = new Account();
        Card card = new Card();
        if (isValidEmail(email)) {

            account.setEmail(email);
            account.setRole("USER");
            card.setVisibility("false");
            account.setProvider("local");
            account.setUserId(Generator.generateUserId(30));
            LocalDate currentDate = LocalDate.now(ZoneId.of("UTC"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            String formattedDate = currentDate.format(formatter);
            account.setCreatedAt(formattedDate);
            account.setBirthday(birthday);

            boolean complete = true;

            if (password.length()<6||password.length()>30) {
                complete = false;
                respond = "password length must be 6 - 30";
            } else {
                account.setPassword(passwordEncoder.encode(password));
            }

            if (complete) {
                if ((firstName.length()>1&&firstName.length()<=20)&&(lastName.length()>1&&lastName.length()<=20)) {
                    card.setFirstName(firstName);
                    card.setLastName(lastName);
                } else {
                    complete = false;
                    respond = "first name must be between 1 and 20 characters";
                }
            }

            if (complete) {
                if (gender.equals("male")||gender.equals("female")||gender.equals("non")||gender.equals("preferNotToSay")) {
                    account.setGender(gender);
                } else {
                    complete = false;
                    respond = "gender incorrect";
                }
            }

            if (complete) {
                account.setCard(card);
                accountRepository.save(account);
                Authentication auth = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(email, password)
                );

                SecurityContextHolder.getContext().setAuthentication(auth);

                rememberMeServices.loginSuccess(request, response, auth);
                respond = "success";
            }




        } else {
            respond = "email isn't valid";
        }

    }


return CompletableFuture.completedFuture(respond);

}

    private final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    private final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

}
