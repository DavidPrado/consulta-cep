package br.com.cobertura.consulta.auditoria.infrastructure.kafka;

import br.com.cobertura.consulta.shared.model.OrigemConsulta;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConsultaRealizadaEvent(
        UUID eventId,
        String cep,
        OrigemConsulta origem,
        String uf,
        Boolean cobertura,
        Boolean sucesso,
        String payload,
        String erro,
        String traceId,
        LocalDateTime consultadoEm
) {
}
