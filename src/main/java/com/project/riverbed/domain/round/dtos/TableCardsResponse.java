package com.project.riverbed.domain.round.dtos;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.card.enums.Rank;
import com.project.riverbed.domain.card.enums.Suit;

import java.util.List;

public record TableCardsResponse(
        List<TableCardResponse> cards
) {

    public static TableCardsResponse from(List<Card> cards) {
        return new TableCardsResponse(
                cards.stream()
                        .map(TableCardResponse::from)
                        .toList()
        );
    }

    public record TableCardResponse(
            Long id,
            Suit suit,
            Rank rank
    ) {

        public static TableCardResponse from(Card card) {
            return new TableCardResponse(
                    card.getId(),
                    card.getSuit(),
                    card.getRank()
            );
        }
    }
}
