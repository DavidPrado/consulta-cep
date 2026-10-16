package br.com.cobertura.consulta.cobertura.application.dto;

import br.com.cobertura.consulta.cobertura.domain.model.LoteCep;
import br.com.cobertura.consulta.cobertura.domain.model.LoteStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record LoteCepResponse(
        UUID loteId,
        LoteStatus status,
        Integer total,
        Integer processados,
        Integer pendentes,
        Integer falhas,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
    public static LoteCepResponse fromEntity(LoteCep entity) {
        return new LoteCepResponse(
                entity.getId(),
                entity.getStatus(),
                entity.getTotal(),
                entity.getProcessados(),
                entity.getPendentes(),
                entity.getFalhas(),
                entity.getCriadoEm(),
                entity.getAtualizadoEm()
        );
    }
}