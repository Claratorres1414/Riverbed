package com.project.riverbed.domain.hand.strategy;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.card.enums.Rank;
import com.project.riverbed.domain.card.enums.Suit;
import com.project.riverbed.domain.hand.enums.HandRank;
import com.project.riverbed.domain.hand.result.HandEvaluation;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class RoyalFlushStrategy implements HandEvaluationStrategy {

    private static final Set<Rank> ROYAL_RANKS = Set.of(
            Rank.TEN,
            Rank.JACK,
            Rank.QUEEN,
            Rank.KING,
            Rank.ACE
    );

    @Override
    public HandRank getRank() {
        return HandRank.ROYAL_FLUSH;
    }

    @Override
    public Optional<HandEvaluation> evaluate(List<Card> cards) {

        if (cards.size() < 5) {
            return Optional.empty();
        }

        Map<Suit, List<Card>> groupedCards = cards.stream()
                .collect(Collectors.groupingBy(Card::getSuit));

        for (List<Card> suitedCards : groupedCards.values()) {

            Set<Rank> ranks = suitedCards.stream()
                    .map(Card::getRank)
                    .collect(Collectors.toSet());

            if (!ranks.containsAll(ROYAL_RANKS)) {
                continue;
            }

            List<Card> bestFiveCards = suitedCards.stream()
                    .filter(card -> ROYAL_RANKS.contains(card.getRank()))
                    .sorted(Comparator.comparingInt(
                            card -> card.getRank().getStrength()
                    ))
                    .toList();

            List<Integer> tieBreakValues = List.of(14);

            return Optional.of(new HandEvaluation(
                    HandRank.ROYAL_FLUSH,
                    tieBreakValues,
                    bestFiveCards
            ));
        }

        return Optional.empty();
    }
}
