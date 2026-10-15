package br.com.cobertura.consulta.infrastructure.client.viacep;

import br.com.cobertura.consulta.infrastructure.client.viacep.dto.ViaCepResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local")
class ViaCepProviderIT {

    @Autowired
    private ViaCepProvider viaCepProvider;

    @Test
    void deveBuscarCepValidoNoViaCep() {
        ViaCepResponse response = viaCepProvider.buscar("01310100");

        assertThat(response).isNotNull();
        assertThat(response.cep()).isEqualTo("01310-100");
        assertThat(response.uf()).isEqualTo("SP");
        assertThat(response.localidade()).isEqualTo("São Paulo");
    }
}
