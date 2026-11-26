package com.project.riverbed.domain.hand.strategy;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.hand.enums.HandRank;
import com.project.riverbed.domain.hand.result.HandEvaluation;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class HighCardStrategy implements HandEvaluationStrategy {
    @Override
    public HandRank getRank() {
        return HandRank.HIGH_CARD;
    }

    @Override
    public Optional<HandEvaluation> evaluate(List<Card> cards) {

        if (cards.size() < 5) {
            return Optional.empty();
        }

        List<Card> sortedCards = cards.stream()
                .sorted(Comparator.comparingInt(
                        (Card card) -> card.getRank().getStrength()
                ).reversed())
                .toList();

        List<Card> bestFiveCards = sortedCards.subList(0, 5);

        List<Integer> tieBreakValues = bestFiveCards.stream()
                .map(card -> card.getRank().getStrength())
                .toList();

        return Optional.of(new HandEvaluation(
                HandRank.HIGH_CARD,
                tieBreakValues,
                bestFiveCards
        ));
    }
}
