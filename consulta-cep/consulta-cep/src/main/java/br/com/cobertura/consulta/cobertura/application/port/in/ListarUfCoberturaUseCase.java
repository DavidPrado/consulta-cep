package br.com.cobertura.consulta.cobertura.application.port.in;

import br.com.cobertura.consulta.cobertura.application.dto.UfCoberturaResponse;

import java.util.List;

public interface  ListarUfCoberturaUseCase {
    List<UfCoberturaResponse> listar();
}
