package br.com.cobertura.consulta.cobertura.application.port.in;

import br.com.cobertura.consulta.cobertura.application.dto.CepAtendidoResponse;

import java.util.List;

public interface ListarCepsAtendidosUseCase {
    List<CepAtendidoResponse> listar();
}
