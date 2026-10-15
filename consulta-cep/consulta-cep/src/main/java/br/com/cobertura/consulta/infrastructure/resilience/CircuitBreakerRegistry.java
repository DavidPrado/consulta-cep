package br.com.cobertura.consulta.infrastructure.resilience;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class CircuitBreakerRegistry {

    private final ResilienceProperties properties;
    private final Map<String, CircuitBreaker> breakers = new ConcurrentHashMap<>();


    @PostConstruct
    public void init() {
        properties.getCircuitBreakers().forEach((name, cfg) -> {
            CircuitBreakerConfig config = new CircuitBreakerConfig(
                    cfg.getSlidingWindowSize(),
                    cfg.getMinimumNumberOfCalls(),
                    cfg.getFailureRateThreshold(),
                    cfg.getWaitDurationInOpenState(),
                    cfg.getPermittedNumberOfCallsInHalfOpenState()
            );
            breakers.put(name, new CircuitBreaker(name, config));
            log.info("CircuitBreaker registrado: {} | janela={} | threshold={}% | openStateWait={}",
                    name, config.slidingWindowSize(), config.failureRateThreshold(), config.waitDurationInOpenState());
        });
    }

    public CircuitBreaker getOrCreate(String name) {
        return breakers.computeIfAbsent(name, n -> {
            log.warn("CircuitBreaker [{}] não estava configurado. Usando valores padrão.", n);
            return new CircuitBreaker(n, new CircuitBreakerConfig(10, 5, 50f,
                    java.time.Duration.ofSeconds(30), 3));
        });
    }

    public Collection<CircuitBreaker> getAll() {
        return breakers.values();
    }
}
