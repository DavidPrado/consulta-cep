package br.com.cobertura.consulta.cobertura.application.dto;

import br.com.cobertura.consulta.cobertura.domain.model.ItemStatus;
import br.com.cobertura.consulta.cobertura.domain.model.LoteCepItem;

import java.time.LocalDateTime;
import java.util.UUID;

public record LoteCepItemResponse(
        UUID id,
        String cep,
        ItemStatus status,
        String dados,
        String erro,
        Integer tentativas,
        LocalDateTime ultimaTentativa
) {
    public static LoteCepItemResponse fromEntity(LoteCepItem entity) {
        return new LoteCepItemResponse(
                entity.getId(),
                entity.getCep(),
                entity.getStatus(),
                entity.getDados(),
                entity.getErro(),
                entity.getTentativas(),
                entity.getUltimaTentativa()
        );
    }
}