package br.com.cobertura.consulta.shared.port.out;

import br.com.cobertura.consulta.cobertura.infrastructure.kafka.CepDescobertoEvent;

public interface CepDescobertoPublisher {
    void publicar(CepDescobertoEvent event);
}