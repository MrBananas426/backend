package com.example.icebreaker.services;

import com.example.icebreaker.impl.Generator;
import com.example.icebreaker.models.Account;
import com.example.icebreaker.models.Card;
import com.example.icebreaker.repositories.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

@Component
@AllArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AccountRepository accountRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oauthUser = super.loadUser(userRequest);
        Map<String, Object> attrs = oauthUser.getAttributes();

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String email = (String) attrs.get("email");
        String firstName = (String) attrs.getOrDefault("given_name", null);
        String lastName  = (String) attrs.getOrDefault("family_name", null);

        Account account = accountRepository.findByEmail(email).orElseGet(() -> {
            Account newAccount = new Account();
            newAccount.setUserId(Generator.generateUserId(30));
            newAccount.setEmail(email);
            newAccount.setPassword(""); // no local password for OAuth
            newAccount.setRole("USER");

            LocalDate currentDate = LocalDate.now(ZoneId.of("UTC"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            newAccount.setCreatedAt(currentDate.format(formatter));
            newAccount.setProvider(provider);

            Card card = new Card();
            card.setFirstName(firstName);
            card.setLastName(lastName);
            card.setVisibility("false");
            newAccount.setCard(card);

            return accountRepository.save(newAccount);
        });

        // Return a Spring Security OAuth2User (not your Account entity)
        String nameAttrKey = userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        if (nameAttrKey == null || nameAttrKey.isBlank()) {
            // Google usually uses "sub"
            nameAttrKey = "sub";
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attrs,
                nameAttrKey
        );
    }
}
