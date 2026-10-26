package com.project.riverbed.domain.round.entitys;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "round_player_cards",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_round_player_cards_card",
                        columnNames = {"round_id", "card_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(RoundPlayerCardId.class)
public class RoundPlayerCard {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private User player;

    @Id
    @Column(name = "position", nullable = false)
    private Short position;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;
}