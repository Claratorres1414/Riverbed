package com.project.riverbed.domain.card.seed;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.card.enums.Rank;
import com.project.riverbed.domain.card.enums.Suit;
import com.project.riverbed.domain.card.repositorys.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CardSeeder implements CommandLineRunner {

    private final CardRepository cardRepository;

    @Override
    @Transactional
    public void run(String... args) {
        Set<String> existingCards = new HashSet<>();

        cardRepository.findAll().forEach(card ->
                existingCards.add(card.getSuit().name() + "_" + card.getRank().name())
        );

        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                String key = suit.name() + "_" + rank.name();

                if (!existingCards.contains(key)) {
                    cardRepository.save(
                            Card.builder()
                                    .suit(suit)
                                    .rank(rank)
                                    .build()
                    );
                }
            }
        }
    }
}