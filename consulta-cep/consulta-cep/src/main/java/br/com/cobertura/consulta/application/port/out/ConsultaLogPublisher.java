package br.com.cobertura.consulta.application.port.out;

import br.com.cobertura.consulta.infrastructure.messaging.ConsultaRealizadaEvent;

public interface ConsultaLogPublisher {
    void publicar(ConsultaRealizadaEvent event);
}
