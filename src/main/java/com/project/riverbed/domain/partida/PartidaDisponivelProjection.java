
package com.project.riverbed.domain.partida;

import java.util.UUID;

public interface PartidaDisponivelProjection {

    UUID getId();

    Long getJogadoresAtuais();

    Integer getMaxParticipantes();
}
