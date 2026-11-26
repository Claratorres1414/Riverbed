package com.project.riverbed.domain.hand.strategy;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.hand.enums.HandRank;
import com.project.riverbed.domain.hand.result.HandEvaluation;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class StraightStrategy implements HandEvaluationStrategy {

    @Override
    public HandRank getRank() {
        return HandRank.STRAIGHT;
    }

    @Override
    public Optional<HandEvaluation> evaluate(List<Card> cards) {

        if (cards.size() < 5) {
            return Optional.empty();
        }

        Map<Integer, Card> uniqueCards = cards.stream()
                .sorted(Comparator.comparingInt(
                                (Card card) -> card.getRank().getStrength()
                ).reversed())
                .collect(Collectors.toMap(
                        card -> card.getRank().getStrength(),
                        Function.identity(),
                        (first, second) -> first,
                        TreeMap::new
                ));

        List<Integer> values = new ArrayList<>(uniqueCards.keySet());

        if (uniqueCards.containsKey(14)) {
            values.addFirst(1);
        }

        for (int i = values.size() - 1; i >= 4; i--) {

            List<Integer> sequence = values.subList(i - 4, i + 1);

            boolean consecutive = true;

            for (int j = 1; j < sequence.size(); j++) {
                if (sequence.get(j) - sequence.get(j - 1) != 1) {
                    consecutive = false;
                    break;
                }
            }

            if (!consecutive) {
                continue;
            }

            int highCard = sequence.getLast();

            List<Card> bestFiveCards = sequence.stream()
                    .map(value -> uniqueCards.get(value == 1 ? 14 : value))
                    .toList();

            return Optional.of(new HandEvaluation(
                    HandRank.STRAIGHT,
                    List.of(highCard),
                    bestFiveCards
            ));
        }

        return Optional.empty();
    }
}