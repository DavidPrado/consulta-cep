package br.com.cobertura.consulta.infrastructure.resilience;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "resilience")
public class ResilienceProperties {

    private Map<String, BreakerConfig> circuitBreakers = new HashMap<>();

    @Data
    public static class BreakerConfig {
        private int slidingWindowSize = 10;
        private int minimumNumberOfCalls = 5;
        private float failureRateThreshold = 50f;
        private Duration waitDurationInOpenState = Duration.ofSeconds(30);
        private int permittedNumberOfCallsInHalfOpenState = 3;
    }
}
