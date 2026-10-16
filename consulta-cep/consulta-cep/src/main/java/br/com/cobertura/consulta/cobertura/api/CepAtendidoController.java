package br.com.cobertura.consulta.cobertura.api;


import br.com.cobertura.consulta.cobertura.application.dto.CepAtendidoRequest;
import br.com.cobertura.consulta.cobertura.application.dto.CepAtendidoResponse;
import br.com.cobertura.consulta.cobertura.application.port.in.CadastrarCepAtendidoUseCase;
import br.com.cobertura.consulta.cobertura.application.port.in.ListarCepsAtendidosUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cobertura/ceps")
@RequiredArgsConstructor
public class CepAtendidoController {

    private final CadastrarCepAtendidoUseCase cadastrarUseCase;
    private final ListarCepsAtendidosUseCase listarUseCase;

    @PostMapping
    public ResponseEntity<CepAtendidoResponse> cadastrar(@RequestBody @Valid CepAtendidoRequest request) {
        CepAtendidoResponse response = cadastrarUseCase.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<CepAtendidoResponse>> listar(Pageable pageable) {
        return ResponseEntity.ok(listarUseCase.listar(pageable));
    }
}
