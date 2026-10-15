package br.com.cobertura.consulta.application.port.in;

import br.com.cobertura.consulta.application.dto.CepAtendidoRequest;
import br.com.cobertura.consulta.application.dto.CepAtendidoResponse;

public interface CadastrarCepAtendidoUseCase {
    CepAtendidoResponse cadastrar(CepAtendidoRequest request);
}
