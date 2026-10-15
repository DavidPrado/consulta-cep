package br.com.cobertura.consulta.application.port.out;

import br.com.cobertura.consulta.auditoria.infrastructure.kafka.ConsultaRealizadaEvent;

public interface ConsultaLogPublisher {
    void publicar(ConsultaRealizadaEvent event);
}
