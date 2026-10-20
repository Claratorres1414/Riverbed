package com.project.riverbed.domain.card.entitys;

import com.project.riverbed.domain.card.enums.Rank;
import com.project.riverbed.domain.card.enums.Suit;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "cards",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cards_suit_rank",
                        columnNames = {"suit", "rank"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Suit suit;

    @Enumerated(EnumType.STRING)
    @Column(name = "rank", nullable = false, length = 10)
    private Rank rank;
}
