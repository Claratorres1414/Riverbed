package com.project.riverbed.domain.hand.service;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.hand.result.HandEvaluation;
import com.project.riverbed.domain.hand.strategy.HandEvaluationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HandEvaluatorService {

    private final List<HandEvaluationStrategy> strategies;

    public HandEvaluation evaluate(List<Card> cards) {

        if (cards == null || cards.size() < 5) {
            throw new IllegalArgumentException(
                    "At least 5 cards are required to evaluate a hand"
            );
        }

        List<HandEvaluationStrategy> orderedStrategies = strategies.stream()
                .sorted(
                        Comparator.comparingInt(
                            (HandEvaluationStrategy strategy) -> strategy.getRank().getStrength()
                        ).reversed()
                )
                .toList();

        return orderedStrategies.stream()
                .map(strategy -> strategy.evaluate(cards))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No hand evaluation strategy matched the given cards"
                ));
    }
}