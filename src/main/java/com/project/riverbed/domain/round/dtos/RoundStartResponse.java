package com.project.riverbed.domain.round.dtos;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.round.enums.RoundStatus;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record RoundStartResponse(
        UUID roundId,
        RoundStatus status,
        Map<UUID, List<Card>> distribution
) {}