package com.project.riverbed.domain.round.entitys;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RoundPlayerCardId implements Serializable {

    private UUID round;
    private UUID player;
    private Short position;
}