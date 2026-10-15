package br.com.cobertura.consulta.shared.port.out;

import br.com.cobertura.consulta.consulta.infrastructure.viacep.dto.ViaCepResponse;

public interface CepProvider {

    ViaCepResponse buscar(String cep);
}
