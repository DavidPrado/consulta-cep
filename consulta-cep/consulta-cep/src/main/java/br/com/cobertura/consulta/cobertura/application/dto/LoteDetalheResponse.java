package br.com.cobertura.consulta.cobertura.application.dto;

import java.util.List;

public record LoteDetalheResponse(
        LoteCepResponse lote,
        List<LoteCepItemResponse> itens
) {
}