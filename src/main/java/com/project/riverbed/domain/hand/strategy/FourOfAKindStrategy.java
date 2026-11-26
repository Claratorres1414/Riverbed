package com.project.riverbed.domain.hand.strategy;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.card.enums.Rank;
import com.project.riverbed.domain.hand.enums.HandRank;
import com.project.riverbed.domain.hand.result.HandEvaluation;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class FourOfAKindStrategy implements HandEvaluationStrategy {

    @Override
    public HandRank getRank() {
        return HandRank.FOUR_OF_A_KIND;
    }

    @Override
    public Optional<HandEvaluation> evaluate(List<Card> cards) {

        if (cards.size() < 5) {
            return Optional.empty();
        }

        Map<Rank, List<Card>> groupedCards = cards.stream()
                .collect(Collectors.groupingBy(Card::getRank));

        List<Card> fourOfAKind = groupedCards.values().stream()
                .filter(group -> group.size() == 4)
                .findFirst()
                .orElse(null);

        if (fourOfAKind == null) {
            return Optional.empty();
        }

        Rank fourRank = fourOfAKind.getFirst().getRank();

        List<Card> kicker = groupedCards.values().stream()
                .filter(group -> !group.getFirst().getRank().equals(fourRank))
                .flatMap(Collection::stream)
                .max(Comparator.comparingInt(
                        card -> card.getRank().getStrength()
                ))
                .map(List::of)
                .orElse(List.of());

        if (kicker.isEmpty()) {
            return Optional.empty();
        }

        List<Card> bestFiveCards = new ArrayList<>(fourOfAKind);
        bestFiveCards.addAll(kicker);

        List<Integer> tieBreakValues = List.of(
                fourRank.getStrength(),
                kicker.getFirst().getRank().getStrength()
        );

        return Optional.of(new HandEvaluation(
                HandRank.FOUR_OF_A_KIND,
                tieBreakValues,
                bestFiveCards
        ));
    }
}
