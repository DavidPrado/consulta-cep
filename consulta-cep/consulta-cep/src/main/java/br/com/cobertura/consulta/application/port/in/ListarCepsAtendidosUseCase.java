package br.com.cobertura.consulta.application.port.in;

import br.com.cobertura.consulta.application.dto.CepAtendidoResponse;

import java.util.List;

public interface ListarCepsAtendidosUseCase {
    List<CepAtendidoResponse> listar();
}
