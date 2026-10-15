package br.com.cobertura.consulta.application.port.out;

import br.com.cobertura.consulta.consulta.infrastructure.viacep.dto.ViaCepResponse;

public interface CepProvider {

    ViaCepResponse buscar(String cep);
}
