package com.project.riverbed.domain.round.services;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.deck.services.CardDistributionService;
import com.project.riverbed.domain.game.Game;
import com.project.riverbed.domain.game.repositorys.GamePlayerRepository;
import com.project.riverbed.domain.game.repositorys.GameRepository;
import com.project.riverbed.domain.game.GameStatus;
import com.project.riverbed.domain.game.exceptions.GameNotFoundException;
import com.project.riverbed.domain.game.exceptions.InsufficientPlayersException;
import com.project.riverbed.domain.round.dtos.PrivateCardsResponse;
import com.project.riverbed.domain.round.dtos.RoundStartResponse;
import com.project.riverbed.domain.round.entitys.Round;
import com.project.riverbed.domain.round.entitys.RoundPlayerCard;
import com.project.riverbed.domain.round.enums.RoundStatus;
import com.project.riverbed.domain.round.exceptions.PlayerNotInRoundException;
import com.project.riverbed.domain.round.exceptions.RoundNotFoundException;
import com.project.riverbed.domain.round.repositorys.RoundPlayerCardRepository;
import com.project.riverbed.domain.round.repositorys.RoundRepository;
import com.project.riverbed.domain.user.User;
import com.project.riverbed.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoundService {

    private final RoundRepository roundRepository;
    private final GameRepository gameRepository;
    private final GamePlayerRepository gamePlayerRepository;
    private final CardDistributionService cardDistributionService;
    private final RoundPlayerCardRepository roundPlayerCardRepository;
    private final UserRepository userRepository;

    @Transactional
    public RoundStartResponse startRound(UUID gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(GameNotFoundException::new);

        if (game.getStatus() != GameStatus.EM_ANDAMENTO) {
            throw new IllegalStateException(
                    "Não é possível iniciar uma rodada: a partida não está em andamento."
            );
        }

        if (roundRepository.existsByGameIdAndStatus(
                gameId, RoundStatus.EM_ANDAMENTO)) {
            throw new IllegalStateException(
                    "Já existe uma rodada em andamento para esta partida."
            );
        }

        List<UUID> playerIds =
                gamePlayerRepository.findPlayerIdsByGameId(gameId);

        if (playerIds.size() < 2) {
            throw new InsufficientPlayersException();
        }

        Round round = Round.builder()
                .game(game)
                .status(RoundStatus.EM_ANDAMENTO)
                .cardsDistributed(false)
                .build();

        round = roundRepository.save(round);

        Map<UUID, List<Card>> distribution =
                cardDistributionService.distribute(playerIds);

        persist(distribution, round);

        round.setCardsDistributed(true);
        roundRepository.save(round);

        return new RoundStartResponse(
                round.getId(),
                round.getStatus(),
                distribution
        );
    }

    @Transactional
    public PrivateCardsResponse getPrivateCards(UUID roundId, UUID playerId) {
        Round round = roundRepository.findById(roundId)
                .orElseThrow(() -> new RoundNotFoundException(roundId));

        boolean playerParticipates = gamePlayerRepository
                .existsByGameIdAndPlayerId(round.getGame().getId(), playerId);

        if (!playerParticipates) {
            throw new PlayerNotInRoundException(playerId, roundId);
        }

        List<RoundPlayerCard> playerCards =
                roundPlayerCardRepository
                        .findByRoundIdAndPlayerIdOrderByPosition(roundId, playerId);

        List<PrivateCardsResponse.PrivateCardResponse> cards =
                playerCards.stream()
                        .map(roundPlayerCard ->
                                PrivateCardsResponse.PrivateCardResponse.from(
                                        roundPlayerCard.getCard()
                                )
                        )
                        .toList();

        return new PrivateCardsResponse(cards);
    }

    private void persist(Map<UUID, List<Card>> distribution, Round round) {
        List<RoundPlayerCard> roundCards = new ArrayList<>();

        for (Map.Entry<UUID, List<Card>> entry : distribution.entrySet()) {
            UUID playerId = entry.getKey();
            List<Card> cards = entry.getValue();

            User player = userRepository.findById(playerId)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            for (int i = 0; i < cards.size(); i++) {
                RoundPlayerCard roundPlayerCard = RoundPlayerCard.builder()
                        .round(round)
                        .player(player)
                        .card(cards.get(i))
                        .position((short) (i + 1))
                        .build();

                roundCards.add(roundPlayerCard);
            }
        }

        roundPlayerCardRepository.saveAll(roundCards);
    }
}