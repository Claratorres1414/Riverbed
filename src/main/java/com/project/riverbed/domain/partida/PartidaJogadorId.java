
package com.project.riverbed.domain.partida;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class PartidaJogadorId implements Serializable {

    @Column(name = "partida_id")
    private UUID partidaId;

    @Column(name = "jogador_id")
    private UUID jogadorId;
}
