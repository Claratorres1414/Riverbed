package com.project.riverbed.domain.hand.strategy;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.card.enums.Suit;
import com.project.riverbed.domain.hand.enums.HandRank;
import com.project.riverbed.domain.hand.result.HandEvaluation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StraightFlushStrategy implements HandEvaluationStrategy {

    private final StraightStrategy straightStrategy;

    @Override
    public HandRank getRank() {
        return HandRank.STRAIGHT_FLUSH;
    }

    @Override
    public Optional<HandEvaluation> evaluate(List<Card> cards) {

        if (cards.size() < 5) {
            return Optional.empty();
        }

        Map<Suit, List<Card>> groupedCards = cards.stream()
                .collect(Collectors.groupingBy(Card::getSuit));

        for (List<Card> suitedCards : groupedCards.values()) {

            if (suitedCards.size() < 5) {
                continue;
            }

            Optional<HandEvaluation> straightEvaluation =
                    straightStrategy.evaluate(suitedCards);

            if (straightEvaluation.isEmpty()) {
                continue;
            }

            HandEvaluation straight = straightEvaluation.get();

            return Optional.of(new HandEvaluation(
                    HandRank.STRAIGHT_FLUSH,
                    straight.tieBreakValues(),
                    straight.bestFiveCards()
            ));
        }

        return Optional.empty();
    }
}