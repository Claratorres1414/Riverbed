package com.project.riverbed.domain.hand.strategy;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.card.enums.Suit;
import com.project.riverbed.domain.hand.enums.HandRank;
import com.project.riverbed.domain.hand.result.HandEvaluation;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FlushStrategy implements HandEvaluationStrategy {

    @Override
    public HandRank getRank() {
        return HandRank.FLUSH;
    }

    @Override
    public Optional<HandEvaluation> evaluate(List<Card> cards) {

        if (cards.size() < 5) {
            return Optional.empty();
        }

        Map<Suit, List<Card>> groupedCards = cards.stream()
                .collect(Collectors.groupingBy(Card::getSuit));

        List<Card> flushCards = groupedCards.values().stream()
                .filter(group -> group.size() >= 5)
                .findFirst()
                .orElse(null);

        if (flushCards == null) {
            return Optional.empty();
        }

        List<Card> bestFiveCards = flushCards.stream()
                .sorted(Comparator.comparingInt(
                    (Card card) -> card.getRank().getStrength()
                ).reversed())
                .limit(5)
                .toList();

        List<Integer> tieBreakValues = bestFiveCards.stream()
                .map(card -> card.getRank().getStrength())
                .toList();

        return Optional.of(new HandEvaluation(
                HandRank.FLUSH,
                tieBreakValues,
                bestFiveCards
        ));
    }
}