package br.com.cobertura.consulta.shared.messaging;


import br.com.cobertura.consulta.shared.port.out.ConsultaLogPublisher;
import br.com.cobertura.consulta.auditoria.infrastructure.kafka.ConsultaRealizadaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsultaLogPublisher implements ConsultaLogPublisher {

    private final KafkaTemplate<String, ConsultaRealizadaEvent> kafkaTemplate;

    @Value("${app.kafka.topics.consulta-log}")
    private String topico;

    @Override
    public void publicar(ConsultaRealizadaEvent event) {
        try {
            kafkaTemplate.send(topico, event.cep(), event);
            log.info("Evento publicado no Kafka: eventId={}, cep={}", event.eventId(), event.cep());
        } catch (Exception ex) {
            log.error("Falha ao publicar evento no Kafka: {}", ex.getMessage(), ex);
        }
    }
}
