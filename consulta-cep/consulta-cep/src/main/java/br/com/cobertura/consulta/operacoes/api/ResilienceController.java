package br.com.cobertura.consulta.operacoes.api;


import br.com.cobertura.consulta.shared.resilience.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/resilience/circuit-breakers")
@RequiredArgsConstructor
public class ResilienceController {

    private final CircuitBreakerRegistry registry;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar() {
        var lista = registry.getAll().stream()
                .map(cb -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("nome", cb.getName());
                    map.put("estado", cb.getState().name());
                    map.put("taxaFalha", cb.getFailureRate());
                    map.put("tamanhoJanela", cb.getSlidingWindowSize());
                    return map;
                })
                .toList();
        return ResponseEntity.ok(lista);
    }
}
