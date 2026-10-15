package br.com.cobertura.consulta.application.port.in;

import br.com.cobertura.consulta.application.dto.UfCoberturaRequest;
import br.com.cobertura.consulta.application.dto.UfCoberturaResponse;

public interface  CadastrarUfCoberturaUseCase {
    UfCoberturaResponse cadastrar(UfCoberturaRequest request);
}
