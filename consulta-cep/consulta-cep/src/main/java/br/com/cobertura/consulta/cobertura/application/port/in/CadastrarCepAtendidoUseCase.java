package br.com.cobertura.consulta.cobertura.application.port.in;

import br.com.cobertura.consulta.cobertura.application.dto.CepAtendidoRequest;
import br.com.cobertura.consulta.cobertura.application.dto.CepAtendidoResponse;

public interface CadastrarCepAtendidoUseCase {
    CepAtendidoResponse cadastrar(CepAtendidoRequest request);
}
