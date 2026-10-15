package br.com.cobertura.consulta.shared.resilience;


import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;

@Slf4j
public class CircuitBreaker {

    private final String name;
    private final CircuitBreakerConfig config;

    private final Deque<Boolean> slidingWindow = new ArrayDeque<>();

    private CircuitBreakerState state = CircuitBreakerState.CLOSED;
    private Instant openedAt;
    private int halfOpenCallsCount = 0;
    private int halfOpenSuccessCount = 0;


    public CircuitBreaker(String name, CircuitBreakerConfig config) {
        this.name = name;
        this.config = config;
    }


    public boolean allowRequest() {
        synchronized (this) {
            return switch (state) {
                case CLOSED -> true;
                case OPEN -> {
                    if (Duration.between(openedAt, Instant.now()).compareTo(config.waitDurationInOpenState()) >= 0) {
                        transitionTo(CircuitBreakerState.HALF_OPEN);
                        yield true;
                    }
                    yield false;
                }
                case HALF_OPEN -> {
                    if (halfOpenCallsCount < config.permittedNumberOfCallsInHalfOpenState()) {
                        halfOpenCallsCount++;
                        yield true;
                    }
                    yield false;
                }
            };
        }
    }

    public void recordSuccess() {
        synchronized (this) {
            if (state == CircuitBreakerState.HALF_OPEN) {
                halfOpenSuccessCount++;
                if (halfOpenSuccessCount >= config.permittedNumberOfCallsInHalfOpenState()) {
                    transitionTo(CircuitBreakerState.CLOSED);
                }
            } else if (state == CircuitBreakerState.CLOSED) {
                addToWindow(true);
            }
        }
    }

    public void recordFailure() {
        synchronized (this) {
            if (state == CircuitBreakerState.HALF_OPEN) {
                transitionTo(CircuitBreakerState.OPEN);
            } else if (state == CircuitBreakerState.CLOSED) {
                addToWindow(false);
                if (shouldOpen()) {
                    transitionTo(CircuitBreakerState.OPEN);
                }
            }
        }
    }

    private void addToWindow(boolean success) {
        slidingWindow.addLast(success);
        if (slidingWindow.size() > config.slidingWindowSize()) {
            slidingWindow.removeFirst();
        }
    }

    private boolean shouldOpen() {
        if (slidingWindow.size() < config.minimumNumberOfCalls()) {
            return false;
        }
        long failures = slidingWindow.stream().filter(s -> !s).count();
        float rate = (failures * 100f) / slidingWindow.size();
        return rate >= config.failureRateThreshold();
    }

    private void transitionTo(CircuitBreakerState newState) {
        CircuitBreakerState old = this.state;
        this.state = newState;
        log.warn("CircuitBreaker [{}] transição: {} -> {}", name, old, newState);

        switch (newState) {
            case OPEN -> openedAt = Instant.now();
            case HALF_OPEN -> {
                halfOpenCallsCount = 0;
                halfOpenSuccessCount = 0;
            }
            case CLOSED -> {
                slidingWindow.clear();
                openedAt = null;
                halfOpenCallsCount = 0;
                halfOpenSuccessCount = 0;
            }
        }
    }

    public CircuitBreakerState getState() {
        synchronized (this) {
            return state;
        }
    }

    public String getName() {
        return name;
    }

    public int getSlidingWindowSize() {
        synchronized (this) {
            return slidingWindow.size();
        }
    }

    public float getFailureRate() {
        synchronized (this) {
            if (slidingWindow.isEmpty()) {
                return -1f;
            }
            long failures = slidingWindow.stream().filter(s -> !s).count();
            return (failures * 100f) / slidingWindow.size();
        }
    }


}
