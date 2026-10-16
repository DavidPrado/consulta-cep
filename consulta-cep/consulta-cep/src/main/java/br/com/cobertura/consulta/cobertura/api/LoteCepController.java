package br.com.cobertura.consulta.cobertura.api;

import br.com.cobertura.consulta.cobertura.application.dto.CriarLoteRequest;
import br.com.cobertura.consulta.cobertura.application.dto.LoteCepResponse;
import br.com.cobertura.consulta.cobertura.application.dto.LoteDetalheResponse;
import br.com.cobertura.consulta.cobertura.application.port.in.ConsultarLoteUseCase;
import br.com.cobertura.consulta.cobertura.application.port.in.CriarLoteUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lotes")
@RequiredArgsConstructor
public class LoteCepController {

    private final CriarLoteUseCase criarLoteUseCase;
    private final ConsultarLoteUseCase consultarLoteUseCase;

    @PostMapping
    public ResponseEntity<LoteCepResponse> criar(@RequestBody @Valid CriarLoteRequest request) {
        LoteCepResponse response = criarLoteUseCase.criar(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{loteId}")
    public ResponseEntity<LoteDetalheResponse> consultar(@PathVariable UUID loteId) {
        return ResponseEntity.ok(consultarLoteUseCase.consultar(loteId));
    }
}