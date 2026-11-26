package com.project.riverbed.domain.hand.service;

import com.project.riverbed.domain.hand.strategy.HandEvaluationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HandEvaluatorService {

    private final List<HandEvaluationStrategy> strategies;

    private List<HandEvaluationStrategy> getOrderedStrategies() {
        return strategies.stream()
                .sorted(Comparator.comparingInt(
                        (HandEvaluationStrategy strategy) -> strategy.getRank().getStrength()
                ).reversed())
                .toList();
    }
}
