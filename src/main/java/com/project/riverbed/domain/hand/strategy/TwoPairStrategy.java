package com.project.riverbed.domain.hand.strategy;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.card.enums.Rank;
import com.project.riverbed.domain.hand.enums.HandRank;
import com.project.riverbed.domain.hand.result.HandEvaluation;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class TwoPairStrategy implements HandEvaluationStrategy{
    @Override
    public HandRank getRank() {
        return HandRank.TWO_PAIR;
    }

    @Override
    public Optional<HandEvaluation> evaluate(List<Card> cards) {
        if (cards.size() < 5) {
            return Optional.empty();
        }

        Map<Rank, List<Card>> groupedCards = cards.stream()
                .collect(Collectors.groupingBy(Card::getRank));

        if (groupedCards.values().stream()
                .anyMatch(group -> group.size() >= 3)) {
            return Optional.empty();
        }

        List<List<Card>> pairs = groupedCards.values().stream()
                .filter(group -> group.size() == 2)
                .sorted(Comparator.comparingInt(
                        (List<Card> group) ->
                                group.getFirst().getRank().getStrength()
                ).reversed())
                .toList();

        if (pairs.size() < 2) {
            return Optional.empty();
        }

        List<List<Card>> bestPairs = pairs.subList(0, 2);

        Set<Rank> pairRanks = bestPairs.stream()
                .map(group -> group.getFirst().getRank())
                .collect(Collectors.toSet());

        List<Card> kicker = groupedCards.values().stream()
                .filter(group -> !pairRanks.contains(group.getFirst().getRank()))
                .flatMap(Collection::stream)
                .sorted(Comparator.comparingInt(
                                (Card card) -> card.getRank().getStrength()
                ).reversed())
                .limit(1)
                .toList();

        if (kicker.isEmpty()) {
            return Optional.empty();
        }

        List<Card> bestFiveCards = new ArrayList<>();

        bestPairs.forEach(bestFiveCards::addAll);
        bestFiveCards.addAll(kicker);

        List<Integer> tieBreakValues = new ArrayList<>();

        bestPairs.forEach(pair ->
                tieBreakValues.add(pair.getFirst().getRank().getStrength())
        );

        tieBreakValues.add(
                kicker.getFirst().getRank().getStrength()
        );

        return Optional.of(new HandEvaluation(
                HandRank.TWO_PAIR,
                tieBreakValues,
                bestFiveCards
        ));
    }
}
