package br.com.cobertura.consulta.infrastructure.resilience;

public enum CircuitBreakerState {
    CLOSED,
    OPEN,
    HALF_OPEN
}
