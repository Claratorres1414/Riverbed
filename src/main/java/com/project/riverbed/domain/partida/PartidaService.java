
package com.project.riverbed.domain.partida;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartidaService {

    private final PartidaRepository partidaRepository;

    @Transactional(readOnly = true)
    public List<PartidaDisponivelResponse> buscarPartidasDisponiveis() {
        return partidaRepository.buscarPartidasDisponiveis()
                .stream()
                .map(partida -> new PartidaDisponivelResponse(
                        partida.getId(),
                        partida.getJogadoresAtuais(),
                        partida.getMaxParticipantes()
                ))
                .toList();
    }
}
