package com.project.riverbed.domain.hand.result;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.hand.enums.HandRank;

import java.util.List;

public record HandEvaluation(
        HandRank rank,
        List<Integer> tieBreakValues,
        List<Card> bestFiveCards
) {

    public HandEvaluation {
        tieBreakValues = List.copyOf(tieBreakValues);
        bestFiveCards = List.copyOf(bestFiveCards);
    }
}
