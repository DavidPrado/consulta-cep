package br.com.cobertura.consulta.interfaces.rest;

import br.com.cobertura.consulta.application.dto.CepAtendidoRequest;
import br.com.cobertura.consulta.application.dto.CepAtendidoResponse;
import br.com.cobertura.consulta.application.port.in.CadastrarCepAtendidoUseCase;
import br.com.cobertura.consulta.application.port.in.ListarCepsAtendidosUseCase;
import br.com.cobertura.consulta.domain.exception.CepDuplicadoException;
import br.com.cobertura.consulta.domain.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CepAtendidoController.class)
@Import(GlobalExceptionHandler.class)
class CepAtendidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CadastrarCepAtendidoUseCase cadastrarUseCase;

    @MockitoBean
    private ListarCepsAtendidosUseCase listarUseCase;

    @Test
    @DisplayName("POST deve retornar 201 quando cadastro com sucesso")
    void deveRetornar201AoCadastrar() throws Exception {
        CepAtendidoRequest request = new CepAtendidoRequest(
                "01310100", "Avenida Paulista", "Bela Vista",
                "São Paulo", "SP", 2, new BigDecimal("15.90")
        );
        CepAtendidoResponse response = new CepAtendidoResponse(
                UUID.randomUUID(), "01310100", "Avenida Paulista", "Bela Vista",
                "São Paulo", "SP", 2, new BigDecimal("15.90"), LocalDateTime.now()
        );

        when(cadastrarUseCase.cadastrar(any(CepAtendidoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/ceps-atendidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cep").value("01310100"))
                .andExpect(jsonPath("$.uf").value("SP"));
    }

    @Test
    @DisplayName("POST deve retornar 400 quando CEP inválido")
    void deveRetornar400QuandoCepInvalido() throws Exception {
        CepAtendidoRequest request = new CepAtendidoRequest(
                "abc", null, null, null, "SP", 2, new BigDecimal("15.90")
        );

        mockMvc.perform(post("/api/v1/ceps-atendidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Erro de validação"));
    }

    @Test
    @DisplayName("POST deve retornar 409 quando CEP duplicado")
    void deveRetornar409QuandoDuplicado() throws Exception {
        CepAtendidoRequest request = new CepAtendidoRequest(
                "01310100", "Avenida Paulista", "Bela Vista",
                "São Paulo", "SP", 2, new BigDecimal("15.90")
        );

        Mockito.when(cadastrarUseCase.cadastrar(any())).thenThrow(new CepDuplicadoException("01310100"));

        mockMvc.perform(post("/api/v1/ceps-atendidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("CEP duplicado"));
    }

    @Test
    @DisplayName("GET deve retornar lista de CEPs")
    void deveListar() throws Exception {
        CepAtendidoResponse response = new CepAtendidoResponse(
                UUID.randomUUID(), "01310100", "Avenida Paulista", "Bela Vista",
                "São Paulo", "SP", 2, new BigDecimal("15.90"), LocalDateTime.now()
        );

        when(listarUseCase.listar()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/ceps-atendidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cep").value("01310100"));
    }
}