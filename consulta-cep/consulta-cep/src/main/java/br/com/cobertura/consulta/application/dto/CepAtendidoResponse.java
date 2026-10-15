package br.com.cobertura.consulta.application.dto;

import br.com.cobertura.consulta.domain.model.CepAtendido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CepAtendidoResponse(
        UUID id,
        String cep,
        String logradouro,
        String bairro,
        String cidade,
        String uf,
        Integer prazoDias,
        BigDecimal taxa,
        LocalDateTime criadoEm
) {

    public static CepAtendidoResponse fromEntity(CepAtendido entity) {
        return new CepAtendidoResponse(
                entity.getId(),
                entity.getCep(),
                entity.getLogradouro(),
                entity.getBairro(),
                entity.getCidade(),
                entity.getUf(),
                entity.getPrazoDias(),
                entity.getTaxa(),
                entity.getCriadoEm()
        );
    }
}
