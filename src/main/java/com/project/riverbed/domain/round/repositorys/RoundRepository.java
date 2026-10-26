package com.project.riverbed.domain.round.repositorys;

import com.project.riverbed.domain.round.entitys.Round;
import com.project.riverbed.domain.round.enums.RoundStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoundRepository extends JpaRepository<Round, UUID> {
    Optional<Round> findByGameIdAndStatus(UUID gameId, RoundStatus status);
}
