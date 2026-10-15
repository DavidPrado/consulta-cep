package br.com.cobertura.consulta.application.service;

import br.com.cobertura.consulta.consulta.application.BuscarCepService;
import br.com.cobertura.consulta.consulta.application.dto.CepConsultaResponse;
import br.com.cobertura.consulta.application.port.out.CepProvider;
import br.com.cobertura.consulta.application.port.out.ConsultaLogPublisher;
import br.com.cobertura.consulta.domain.exception.CepInvalidoException;
import br.com.cobertura.consulta.cobertura.domain.model.CepAtendido;
import br.com.cobertura.consulta.domain.model.OrigemConsulta;
import br.com.cobertura.consulta.cobertura.domain.model.UfCobertura;
import br.com.cobertura.consulta.cobertura.domain.repository.CepAtendidoRepository;
import br.com.cobertura.consulta.cobertura.domain.repository.UfCoberturaRepository;
import br.com.cobertura.consulta.consulta.infrastructure.viacep.dto.ViaCepResponse;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class BuscarCepServiceTest {

    @Mock
    private CepAtendidoRepository cepAtendidoRepository;

    @Mock
    private UfCoberturaRepository ufCoberturaRepository;

    @Mock
    private CepProvider cepProvider;

    @Mock
    private ConsultaLogPublisher logPublisher;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Sanitizador sanitizador;

    @InjectMocks
    private BuscarCepService service;

    @Test
    @DisplayName("Deve retornar origem INTERNO quando CEP existe na base interna")
    void deveRetornarInternoQuandoCepExiste() {
        String cep = "01310100";
        CepAtendido entity = CepAtendido.builder()
                .id(UUID.randomUUID())
                .cep(cep)
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .uf("SP")
                .prazoDias(2)
                .taxa(new BigDecimal("15.90"))
                .criadoEm(LocalDateTime.now())
                .build();

        when(sanitizador.sanitizarCep(cep)).thenReturn(cep);
        when(cepAtendidoRepository.findByCep(cep)).thenReturn(Optional.of(entity));

        CepConsultaResponse response = service.buscar(cep);

        assertThat(response.origem()).isEqualTo(OrigemConsulta.INTERNO);
        assertThat(response.cobertura()).isTrue();
        assertThat(response.prazoDias()).isEqualTo(2);
        verify(cepProvider, never()).buscar(anyString());
        verify(logPublisher, never()).publicar(any());
    }

    @Test
    @DisplayName("Deve retornar origem EXTERNO quando CEP não existe na base interna")
    void deveRetornarExternoQuandoCepNaoExiste() {
        String cep = "20040002";
        ViaCepResponse viaCep = new ViaCepResponse(
                "20040-002", "Avenida Rio Branco", "de 156 a 296 - lado par",
                null, "Centro", "Rio de Janeiro", "RJ", "Rio de Janeiro",
                "Sudeste", "3304557", null, "21", "6001", null
        );

        when(sanitizador.sanitizarCep(cep)).thenReturn(cep);
        when(cepAtendidoRepository.findByCep(cep)).thenReturn(Optional.empty());
        when(cepProvider.buscar(cep)).thenReturn(viaCep);
        when(ufCoberturaRepository.findByUf("RJ"))
                .thenReturn(Optional.of(UfCobertura.builder()
                        .id(UUID.randomUUID())
                        .uf("RJ")
                        .atendida(true)
                        .criadoEm(LocalDateTime.now())
                        .build()));

        CepConsultaResponse response = service.buscar(cep);

        assertThat(response.origem()).isEqualTo(OrigemConsulta.EXTERNO);
        assertThat(response.uf()).isEqualTo("RJ");
        assertThat(response.cobertura()).isTrue();
        verify(cepProvider).buscar(cep);
    }

    @Test
    @DisplayName("Deve retornar cobertura=false quando UF não é atendida")
    void deveRetornarCoberturaFalseQuandoUfNaoAtendida() {
        String cep = "20040002";
        ViaCepResponse viaCep = new ViaCepResponse(
                "20040-002", "Avenida Rio Branco", null,
                null, "Centro", "Rio de Janeiro", "RJ", "Rio de Janeiro",
                "Sudeste", "3304557", null, "21", "6001", null
        );

        when(sanitizador.sanitizarCep(cep)).thenReturn(cep);
        when(cepAtendidoRepository.findByCep(cep)).thenReturn(Optional.empty());
        when(cepProvider.buscar(cep)).thenReturn(viaCep);
        when(ufCoberturaRepository.findByUf("RJ")).thenReturn(Optional.empty());

        CepConsultaResponse response = service.buscar(cep);

        assertThat(response.origem()).isEqualTo(OrigemConsulta.EXTERNO);
        assertThat(response.cobertura()).isFalse();
    }

    @Test
    @DisplayName("Deve lançar CepInvalidoException quando CEP tem formato inválido")
    void deveLancarExcecaoQuandoCepInvalido() {
        when(sanitizador.sanitizarCep("abc")).thenReturn("abc");

        assertThatThrownBy(() -> service.buscar("abc"))
                .isInstanceOf(CepInvalidoException.class)
                .hasMessageContaining("8 dígitos");

        verify(cepProvider, never()).buscar(anyString());
    }
}
