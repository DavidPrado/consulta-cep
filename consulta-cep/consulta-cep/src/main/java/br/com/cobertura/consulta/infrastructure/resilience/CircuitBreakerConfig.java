package br.com.cobertura.consulta.infrastructure.resilience;

import java.time.Duration;

public record CircuitBreakerConfig(
        int slidingWindowSize,
        int minimumNumberOfCalls,
        float failureRateThreshold,
        Duration waitDurationInOpenState,
        int permittedNumberOfCallsInHalfOpenState
) {
}
