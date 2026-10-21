
package com.project.riverbed.domain.partida;

import java.util.UUID;

public record PartidaDisponivelResponse(
        UUID id,
        Long jogadoresAtuais,
        Integer maxParticipantes
) {
}
