package br.com.cobertura.consulta.cobertura.api;

import br.com.cobertura.consulta.cobertura.application.dto.UfCoberturaRequest;
import br.com.cobertura.consulta.cobertura.application.dto.UfCoberturaResponse;
import br.com.cobertura.consulta.cobertura.application.port.in.AtualizarUfCoberturaUseCase;
import br.com.cobertura.consulta.cobertura.application.port.in.CadastrarUfCoberturaUseCase;
import br.com.cobertura.consulta.cobertura.application.port.in.ListarUfCoberturaUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cobertura/ufs")
@RequiredArgsConstructor
public class UfCoberturaController {

    private final CadastrarUfCoberturaUseCase cadastrarUseCase;
    private final AtualizarUfCoberturaUseCase atualizarUseCase;
    private final ListarUfCoberturaUseCase listarUseCase;

    @PostMapping
    public ResponseEntity<UfCoberturaResponse> cadastrar(@RequestBody @Valid UfCoberturaRequest request) {
        UfCoberturaResponse response = cadastrarUseCase.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{uf}")
    public ResponseEntity<UfCoberturaResponse> atualizar(
            @PathVariable String uf,
            @RequestBody @Valid UfCoberturaRequest request) {
        UfCoberturaResponse response = atualizarUseCase.atualizar(uf, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<UfCoberturaResponse>> listar(Pageable pageable) {
        return ResponseEntity.ok(listarUseCase.listar(pageable));
    }
}
