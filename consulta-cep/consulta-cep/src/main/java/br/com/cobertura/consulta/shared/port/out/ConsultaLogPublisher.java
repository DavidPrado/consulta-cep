package br.com.cobertura.consulta.shared.port.out;

import br.com.cobertura.consulta.auditoria.infrastructure.kafka.ConsultaRealizadaEvent;

public interface ConsultaLogPublisher {
    void publicar(ConsultaRealizadaEvent event);
}
