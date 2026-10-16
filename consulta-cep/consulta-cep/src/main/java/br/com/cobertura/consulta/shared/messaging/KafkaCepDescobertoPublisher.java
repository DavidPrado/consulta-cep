package br.com.cobertura.consulta.shared.messaging;

import br.com.cobertura.consulta.cobertura.infrastructure.kafka.CepDescobertoEvent;
import br.com.cobertura.consulta.shared.port.out.CepDescobertoPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCepDescobertoPublisher implements CepDescobertoPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.cep-descoberto}")
    private String topico;

    @Override
    @Async("taskExecutor")
    public void publicar(CepDescobertoEvent event) {
        try {
            kafkaTemplate.send(topico, event.cep(), event);
            log.info("[async-{}] Evento CepDescoberto publicado: cep={}, uf={}",
                    Thread.currentThread().getName(), event.cep(), event.uf());
        } catch (Exception ex) {
            log.error("Falha ao publicar CepDescoberto: {}", ex.getMessage(), ex);
        }
    }
}