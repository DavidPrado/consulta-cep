package br.com.cobertura.consulta.interfaces.rest;

import br.com.cobertura.consulta.application.dto.CepConsultaResponse;
import br.com.cobertura.consulta.application.port.in.BuscarCepUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cep")
@RequiredArgsConstructor
public class CepConsultaController {

    private final BuscarCepUseCase buscarCepUseCase;

    @GetMapping("/{cep}")
    public ResponseEntity<CepConsultaResponse> buscar(@PathVariable String cep) {
        return ResponseEntity.ok(buscarCepUseCase.buscar(cep));
    }
}
