package br.com.cobertura.consulta.operacoes.api;

import br.com.cobertura.consulta.cobertura.infrastructure.worker.LoteProcessamentoWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/operacoes/worker")
@RequiredArgsConstructor
public class WorkerController {

    private final LoteProcessamentoWorker worker;

    @PostMapping("/executar")
    public ResponseEntity<String> executar() {
        worker.processarPendentes();
        return ResponseEntity.ok("Worker executado manualmente com sucesso");
    }
}