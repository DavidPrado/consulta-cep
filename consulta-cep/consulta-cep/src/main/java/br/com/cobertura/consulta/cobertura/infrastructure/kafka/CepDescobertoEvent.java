package br.com.cobertura.consulta.cobertura.infrastructure.kafka;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CepDescobertoEvent(
        UUID eventId,
        String cep,
        String logradouro,
        String bairro,
        String cidade,
        String uf,
        Integer prazoDias,
        BigDecimal taxa,
        LocalDateTime descobertoEm
) {
}