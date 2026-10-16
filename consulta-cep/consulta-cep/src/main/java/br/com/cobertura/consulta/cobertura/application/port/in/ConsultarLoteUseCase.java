package br.com.cobertura.consulta.cobertura.application.port.in;

import br.com.cobertura.consulta.cobertura.application.dto.LoteDetalheResponse;

import java.util.UUID;

public interface ConsultarLoteUseCase {
    LoteDetalheResponse consultar(UUID loteId);
}