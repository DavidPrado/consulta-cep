package br.com.cobertura.consulta.auditoria.api;

import br.com.cobertura.consulta.auditoria.domain.model.ConsultaLog;
import br.com.cobertura.consulta.auditoria.domain.repository.ConsultaLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
public class ConsultaLogController {
    private final ConsultaLogRepository repository;

    @GetMapping
    public ResponseEntity<List<ConsultaLog>> listar() {
        return ResponseEntity.ok(repository.findAll());
    }
}
