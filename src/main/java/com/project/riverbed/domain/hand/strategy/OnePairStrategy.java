package com.project.riverbed.domain.hand.strategy;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.card.enums.Rank;
import com.project.riverbed.domain.hand.enums.HandRank;
import com.project.riverbed.domain.hand.result.HandEvaluation;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class OnePairStrategy implements HandEvaluationStrategy {

    @Override
    public HandRank getRank() {
        return HandRank.ONE_PAIR;
    }

    @Override
    public Optional<HandEvaluation> evaluate(List<Card> cards) {

        if (cards.size() < 5) {
            return Optional.empty();
        }

        Map<Rank, List<Card>> groupedCards = cards.stream()
                .collect(Collectors.groupingBy(Card::getRank));

        long pairCount = groupedCards.values().stream()
                .filter(group -> group.size() == 2)
                .count();

        boolean hasInvalidGroup = groupedCards.values().stream()
                .anyMatch(group -> group.size() != 1 && group.size() != 2);

        if (pairCount != 1 || hasInvalidGroup) {
            return Optional.empty();
        }

        List<Card> pair = groupedCards.values().stream()
                .filter(group -> group.size() == 2)
                .findFirst()
                .orElseThrow();

        Rank pairRank = pair.getFirst().getRank();

        List<Card> kickers = groupedCards.values().stream()
                .filter(group -> !group.getFirst().getRank().equals(pairRank))
                .flatMap(Collection::stream)
                .sorted(Comparator.comparingInt(
                        (Card card) -> card.getRank().getStrength()
                ).reversed())
                .limit(3)
                .toList();

        if (kickers.size() < 3) {
            return Optional.empty();
        }

        List<Card> bestFiveCards = new ArrayList<>(pair);
        bestFiveCards.addAll(kickers);

        List<Integer> tieBreakValues = new ArrayList<>();
        tieBreakValues.add(pairRank.getStrength());

        kickers.stream()
                .map(card -> card.getRank().getStrength())
                .forEach(tieBreakValues::add);

        return Optional.of(new HandEvaluation(
                HandRank.ONE_PAIR,
                tieBreakValues,
                bestFiveCards
        ));
    }
}
