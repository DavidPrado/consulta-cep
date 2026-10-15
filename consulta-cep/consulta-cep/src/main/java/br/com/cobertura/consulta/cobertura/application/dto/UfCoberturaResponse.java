package br.com.cobertura.consulta.cobertura.application.dto;

import br.com.cobertura.consulta.cobertura.domain.model.UfCobertura;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record UfCoberturaResponse(
        UUID id,
        String uf,
        Boolean atendida,
        Integer prazoPadrao,
        BigDecimal taxaPadrao,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {

    public static UfCoberturaResponse fromEntity(UfCobertura entity) {
        return new UfCoberturaResponse(
                entity.getId(),
                entity.getUf(),
                entity.getAtendida(),
                entity.getPrazoPadrao(),
                entity.getTaxaPadrao(),
                entity.getCriadoEm(),
                entity.getAtualizadoEm()
        );
    }
}
