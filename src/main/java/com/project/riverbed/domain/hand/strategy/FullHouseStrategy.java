package com.project.riverbed.domain.hand.strategy;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.card.enums.Rank;
import com.project.riverbed.domain.hand.enums.HandRank;
import com.project.riverbed.domain.hand.result.HandEvaluation;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class FullHouseStrategy implements HandEvaluationStrategy {

    @Override
    public HandRank getRank() {
        return HandRank.FULL_HOUSE;
    }

    @Override
    public Optional<HandEvaluation> evaluate(List<Card> cards) {

        if (cards.size() < 5) {
            return Optional.empty();
        }

        Map<Rank, List<Card>> groupedCards = cards.stream()
                .collect(Collectors.groupingBy(Card::getRank));

        List<List<Card>> threeOfAKinds = groupedCards.values().stream()
                .filter(group -> group.size() >= 3)
                .sorted(Comparator.comparingInt(
                        (List<Card> group) ->
                                group.getFirst().getRank().getStrength()
                ).reversed())
                .toList();

        if (threeOfAKinds.isEmpty()) {
            return Optional.empty();
        }

        List<Card> bestThreeOfAKind = threeOfAKinds.getFirst();

        Rank threeRank = bestThreeOfAKind.getFirst().getRank();

        List<Card> pair = groupedCards.values().stream()
                .filter(group ->
                        !group.getFirst().getRank().equals(threeRank)
                                && group.size() >= 2
                )
                .max(Comparator.comparingInt(
                        group -> group.getFirst().getRank().getStrength()
                ))
                .orElse(null);

        if (pair == null) {
            return Optional.empty();
        }

        Rank pairRank = pair.getFirst().getRank();

        List<Card> bestFiveCards = new ArrayList<>();

        bestFiveCards.addAll(bestThreeOfAKind.subList(0, 3));
        bestFiveCards.addAll(pair.subList(0, 2));

        List<Integer> tieBreakValues = List.of(
                threeRank.getStrength(),
                pairRank.getStrength()
        );

        return Optional.of(new HandEvaluation(
                HandRank.FULL_HOUSE,
                tieBreakValues,
                bestFiveCards
        ));
    }
}