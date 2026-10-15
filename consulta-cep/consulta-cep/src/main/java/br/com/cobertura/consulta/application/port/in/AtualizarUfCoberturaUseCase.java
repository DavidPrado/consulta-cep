package br.com.cobertura.consulta.application.port.in;

import br.com.cobertura.consulta.application.dto.UfCoberturaRequest;
import br.com.cobertura.consulta.application.dto.UfCoberturaResponse;

public interface AtualizarUfCoberturaUseCase {
    UfCoberturaResponse atualizar(String uf, UfCoberturaRequest request);
}
