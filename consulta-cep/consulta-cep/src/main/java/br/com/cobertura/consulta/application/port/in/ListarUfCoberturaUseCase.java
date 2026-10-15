package br.com.cobertura.consulta.application.port.in;

import br.com.cobertura.consulta.application.dto.UfCoberturaResponse;

import java.util.List;

public interface  ListarUfCoberturaUseCase {
    List<UfCoberturaResponse> listar();
}
