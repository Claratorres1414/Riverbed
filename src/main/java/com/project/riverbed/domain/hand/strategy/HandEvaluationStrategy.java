package com.project.riverbed.domain.hand.strategy;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.hand.enums.HandRank;
import com.project.riverbed.domain.hand.result.HandEvaluation;

import java.util.List;
import java.util.Optional;

public interface HandEvaluationStrategy {
    HandRank getRank();

    Optional<HandEvaluation> evaluate(List<Card> cards);
}
