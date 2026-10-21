
package com.project.riverbed.domain.partida;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/poker")
@RequiredArgsConstructor
public class PartidaController {

    private final PartidaService partidaService;

    @GetMapping("/partidasAbertas")
    public List<PartidaDisponivelResponse> buscarPartidasDisponiveis() {
        return partidaService.buscarPartidasDisponiveis();
    }
}
