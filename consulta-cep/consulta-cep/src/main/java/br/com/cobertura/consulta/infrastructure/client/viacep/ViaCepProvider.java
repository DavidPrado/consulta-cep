package br.com.cobertura.consulta.infrastructure.client.viacep;

import br.com.cobertura.consulta.application.port.out.CepProvider;
import br.com.cobertura.consulta.domain.exception.CepNaoEncontradoException;
import br.com.cobertura.consulta.domain.exception.ExternalApiException;
import br.com.cobertura.consulta.infrastructure.client.viacep.dto.ViaCepResponse;
import br.com.cobertura.consulta.infrastructure.resilience.CircuitBreaker;
import br.com.cobertura.consulta.infrastructure.resilience.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViaCepProvider implements CepProvider {

    private static final String BREAKER_NAME = "viaCep";

    private final RestClient viaCepRestClient;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Override
    public ViaCepResponse buscar(String cep) {

        CircuitBreaker breaker = circuitBreakerRegistry.getOrCreate(BREAKER_NAME);

        if (!breaker.allowRequest()) {
            log.warn("CircuitBreaker [{}] está OPEN. Retornando fallback para CEP {}",
                    BREAKER_NAME, cep);
            return fallback(cep, "Circuit breaker OPEN");
        }

        return executarComRetry(breaker, cep);
    }

    private ViaCepResponse executarComRetry(CircuitBreaker breaker, String cep) {

        int maxTentativas = 3;
        long esperaMs = 1000;

        for (int tentativa = 1; tentativa <= maxTentativas; tentativa++) {
            try {
                log.info("Consultando ViaCEP para o CEP: {} (tentativa {}/{})",
                        cep, tentativa, maxTentativas);

                ViaCepResponse response = viaCepRestClient.get()
                        .uri("/{cep}/json/", cep)
                        .retrieve()
                        .body(ViaCepResponse.class);

                if (response == null || response.isNaoEncontrado()) {
                    breaker.recordSuccess();
                    log.warn("CEP não encontrado no ViaCEP: {}", cep);
                    throw new CepNaoEncontradoException(cep);
                }

                breaker.recordSuccess();
                return response;

            } catch (CepNaoEncontradoException ex) {
                throw ex;
            } catch (Exception ex) {
                log.error("Falha na tentativa {} para o CEP {}: {}",
                        tentativa, cep, ex.getMessage());

                if (tentativa < maxTentativas) {
                    try {
                        Thread.sleep(esperaMs);
                        esperaMs *= 2;
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    breaker.recordFailure();
                    return fallback(cep, ex.getMessage());
                }
            }
        }
        return fallback(cep, "Tentativas esgotadas");
    }

    private ViaCepResponse fallback(String cep, String motivo) {
        log.warn("Fallback para CEP {}: {}", cep, motivo);
        return new ViaCepResponse(
                cep, null, null, null, null, null, null, null, null, null, null, null, null,
                Boolean.TRUE
        );
    }


}
