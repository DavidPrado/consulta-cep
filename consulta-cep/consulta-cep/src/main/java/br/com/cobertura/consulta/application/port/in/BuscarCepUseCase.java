package br.com.cobertura.consulta.application.port.in;

import br.com.cobertura.consulta.application.dto.CepConsultaResponse;

public interface BuscarCepUseCase {
    CepConsultaResponse buscar(String cep);
}
