package br.com.cobertura.consulta.application.service;

import br.com.cobertura.consulta.cobertura.application.CepAtendidoService;
import br.com.cobertura.consulta.cobertura.application.dto.CepAtendidoRequest;
import br.com.cobertura.consulta.cobertura.application.dto.CepAtendidoResponse;
import br.com.cobertura.consulta.domain.exception.CepDuplicadoException;
import br.com.cobertura.consulta.cobertura.domain.model.CepAtendido;
import br.com.cobertura.consulta.cobertura.domain.repository.CepAtendidoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CepAtendidoServiceTest {

    @Mock
    private CepAtendidoRepository repository;

    @Mock
    private Sanitizador sanitizador;

    @InjectMocks
    private CepAtendidoService service;

    @Test
    @DisplayName("Deve cadastrar CEP com sucesso")
    void deveCadastrarComSucesso() {
        CepAtendidoRequest request = new CepAtendidoRequest(
                "01310100", "Avenida Paulista", "Bela Vista",
                "São Paulo", "SP", 2, new BigDecimal("15.90")
        );

        when(sanitizador.sanitizarCep(request.cep())).thenReturn("01310100");
        when(sanitizador.sanitizarTexto(anyString(), any(Integer.class))).thenAnswer(i -> i.getArgument(0));
        when(sanitizador.sanitizarUf(anyString())).thenReturn("SP");
        when(repository.existsByCep("01310100")).thenReturn(false);
        when(repository.save(any(CepAtendido.class))).thenAnswer(i -> {
            CepAtendido e = i.getArgument(0);
            e.setCriadoEm(LocalDateTime.now());
            return e;
        });

        CepAtendidoResponse response = service.cadastrar(request);

        assertThat(response.cep()).isEqualTo("01310100");
        assertThat(response.uf()).isEqualTo("SP");
        verify(repository).save(any(CepAtendido.class));
    }

    @Test
    @DisplayName("Deve lançar CepDuplicadoException quando CEP já existe")
    void deveLancarExcecaoQuandoCepDuplicado() {
        CepAtendidoRequest request = new CepAtendidoRequest(
                "01310100", "Avenida Paulista", "Bela Vista",
                "São Paulo", "SP", 2, new BigDecimal("15.90")
        );

        when(sanitizador.sanitizarCep(request.cep())).thenReturn("01310100");
        when(repository.existsByCep("01310100")).thenReturn(true);

        assertThatThrownBy(() -> service.cadastrar(request))
                .isInstanceOf(CepDuplicadoException.class)
                .hasMessageContaining("01310100");

        verify(repository, never()).save(any());
    }
}