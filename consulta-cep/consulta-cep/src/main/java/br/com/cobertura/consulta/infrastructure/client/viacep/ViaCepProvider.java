package br.com.cobertura.consulta.infrastructure.client.viacep;

import br.com.cobertura.consulta.application.port.out.CepProvider;
import br.com.cobertura.consulta.domain.exception.CepNaoEncontradoException;
import br.com.cobertura.consulta.domain.exception.ExternalApiException;
import br.com.cobertura.consulta.infrastructure.client.viacep.dto.ViaCepResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViaCepProvider implements CepProvider {

    private final RestClient viaCepRestClient;

    @Override
    public ViaCepResponse buscar(String cep) {
        try {
            log.info("Consultando ViaCEP para o CEP: {}", cep);

            ViaCepResponse response = viaCepRestClient.get()
                    .uri("/{cep}/json/", cep)
                    .retrieve()
                    .body(ViaCepResponse.class);

            if (response == null || response.isNaoEncontrado()) {
                log.warn("CEP não encontrado no ViaCEP: {}", cep);
                throw new CepNaoEncontradoException(cep);
            }

            return response;

        } catch (CepNaoEncontradoException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Erro ao consultar ViaCEP para o CEP {}: {}", cep, ex.getMessage());
            throw new ExternalApiException(
                    "Não foi possível consultar o serviço externo de CEP. Tente novamente mais tarde.",
                    ex
            );
        }
    }
}
