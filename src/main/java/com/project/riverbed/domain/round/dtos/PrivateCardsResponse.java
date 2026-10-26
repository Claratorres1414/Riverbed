package com.project.riverbed.domain.round.dtos;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.card.enums.Rank;
import com.project.riverbed.domain.card.enums.Suit;

import java.util.List;

public record PrivateCardsResponse(
        List<PrivateCardResponse> cards
) {
    public record PrivateCardResponse(
            Long id,
            Suit suit,
            Rank rank
    ) {
        public static PrivateCardResponse from(Card card) {
            return new PrivateCardResponse(
                    card.getId(),
                    card.getSuit(),
                    card.getRank()
            );
        }
    }
}