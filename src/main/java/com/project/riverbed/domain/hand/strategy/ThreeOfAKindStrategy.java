package com.project.riverbed.domain.hand.strategy;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.card.enums.Rank;
import com.project.riverbed.domain.hand.enums.HandRank;
import com.project.riverbed.domain.hand.result.HandEvaluation;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ThreeOfAKindStrategy implements HandEvaluationStrategy {

    @Override
    public HandRank getRank() {
        return HandRank.THREE_OF_A_KIND;
    }

    @Override
    public Optional<HandEvaluation> evaluate(List<Card> cards) {

        if (cards.size() < 5) {
            return Optional.empty();
        }

        Map<Rank, List<Card>> groupedCards = cards.stream()
                .collect(Collectors.groupingBy(Card::getRank));

        // Não aceitar quadras ou full houses.
        if (groupedCards.values().stream()
                .anyMatch(group -> group.size() >= 4)) {
            return Optional.empty();
        }

        List<Card> threeOfAKind = groupedCards.values().stream()
                .filter(group -> group.size() == 3)
                .findFirst()
                .orElse(null);

        if (threeOfAKind == null) {
            return Optional.empty();
        }

        if (groupedCards.values().stream()
                .filter(group -> group.size() >= 2)
                .count() > 1) {
            return Optional.empty();
        }

        Rank threeRank = threeOfAKind.getFirst().getRank();

        List<Card> kickers = groupedCards.values().stream()
                .filter(group -> !group.getFirst().getRank().equals(threeRank))
                .flatMap(Collection::stream)
                .sorted(Comparator.comparingInt(
                    (Card card) -> card.getRank().getStrength()
                ).reversed())
                .limit(2)
                .toList();

        if (kickers.size() < 2) {
            return Optional.empty();
        }

        List<Card> bestFiveCards = new ArrayList<>(threeOfAKind);
        bestFiveCards.addAll(kickers);

        List<Integer> tieBreakValues = new ArrayList<>();
        tieBreakValues.add(threeRank.getStrength());

        kickers.stream()
                .map(card -> card.getRank().getStrength())
                .forEach(tieBreakValues::add);

        return Optional.of(new HandEvaluation(
                HandRank.THREE_OF_A_KIND,
                tieBreakValues,
                bestFiveCards
        ));
    }
}
