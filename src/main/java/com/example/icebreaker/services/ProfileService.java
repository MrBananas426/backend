package com.example.icebreaker.services;

import com.example.icebreaker.models.Account;
import com.example.icebreaker.models.Card;
import com.example.icebreaker.models.CardDTO;
import com.example.icebreaker.repositories.AccountRepository;
import com.example.icebreaker.repositories.CardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class ProfileService {

    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;

    /**
     * Update the user's Card based on the incoming DTO.
     * Only non-null (and for Strings, non-blank) values are applied.
     */
    public CompletableFuture<String> editCard(CardDTO dto, Account account) {
        if (account == null) {
            return CompletableFuture.completedFuture("account not found");
        }

        Card card = account.getCard();
        if (card == null) {
            card = new Card();
        }

        // Strings
        if (dto.getFirstName() != null && !dto.getFirstName().isBlank()) {
            card.setFirstName(dto.getFirstName().trim());
        }
        if (dto.getLastName() != null && !dto.getLastName().isBlank()) {
            card.setLastName(dto.getLastName().trim());
        }
        if (dto.getVibeIndicator() != null && !dto.getVibeIndicator().isBlank()) {
            card.setVibeIndicator(dto.getVibeIndicator().trim());
        }

        // Arrays (entity likely uses String[])
        if (dto.getTalkAbout() != null) {
            card.setTalkAbout(dto.getTalkAbout());
        }
        // If your DTO has more fields (openTo, tone, locationCue, etc.), mirror the same pattern.

        // Persist updates
        cardRepository.save(card);
        if (account.getCard() == null || !Objects.equals(account.getCard().getId(), card.getId())) {
            account.setCard(card);
            accountRepository.save(account);
        }

        return CompletableFuture.completedFuture("success");
    }
}
