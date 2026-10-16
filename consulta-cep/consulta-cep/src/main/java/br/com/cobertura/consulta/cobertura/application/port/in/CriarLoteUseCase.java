package br.com.cobertura.consulta.cobertura.application.port.in;

import br.com.cobertura.consulta.cobertura.application.dto.CriarLoteRequest;
import br.com.cobertura.consulta.cobertura.application.dto.LoteCepResponse;

public interface CriarLoteUseCase {
    LoteCepResponse criar(CriarLoteRequest request);
}