package com.project.riverbed.domain.hand.service;

import com.project.riverbed.domain.hand.result.HandEvaluation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HandComparator {

    public int compare(HandEvaluation first, HandEvaluation second) {

        int rankComparison = Integer.compare(
                first.rank().getStrength(),
                second.rank().getStrength()
        );

        if (rankComparison != 0) {
            return rankComparison;
        }

        return compareTieBreakValues(
                first.tieBreakValues(),
                second.tieBreakValues()
        );
    }

    private int compareTieBreakValues(
            List<Integer> first,
            List<Integer> second
    ) {
        for (int i = 0; i < first.size(); i++) {
            int comparison = Integer.compare(
                    first.get(i),
                    second.get(i)
            );

            if (comparison != 0) {
                return comparison;
            }
        }

        return 0;
    }
}
