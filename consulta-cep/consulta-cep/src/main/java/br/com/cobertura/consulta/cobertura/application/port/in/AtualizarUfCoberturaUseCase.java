package br.com.cobertura.consulta.cobertura.application.port.in;

import br.com.cobertura.consulta.cobertura.application.dto.UfCoberturaRequest;
import br.com.cobertura.consulta.cobertura.application.dto.UfCoberturaResponse;

public interface AtualizarUfCoberturaUseCase {
    UfCoberturaResponse atualizar(String uf, UfCoberturaRequest request);
}
