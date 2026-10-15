package br.com.cobertura.consulta.auditoria.infrastructure.kafka;

import br.com.cobertura.consulta.auditoria.domain.model.ConsultaLog;
import br.com.cobertura.consulta.auditoria.domain.repository.ConsultaLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsultaLogConsumer {

    private final ConsultaLogRepository repository;

    @KafkaListener(
            topics = "${app.kafka.topics.consulta-log}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumir(ConsultaRealizadaEvent event) {
        try {
            log.info("Evento recebido do Kafka: eventId={}, cep={}", event.eventId(), event.cep());

            ConsultaLog consultaLog = ConsultaLog.builder()
                    .id(UUID.randomUUID())
                    .eventId(event.eventId())
                    .cep(event.cep())
                    .origem(event.origem() != null ? event.origem().name() : null)
                    .uf(event.uf())
                    .cobertura(event.cobertura())
                    .sucesso(event.sucesso())
                    .payload(event.payload())
                    .erro(event.erro())
                    .traceId(event.traceId())
                    .consultadoEm(event.consultadoEm())
                    .criadoEm(LocalDateTime.now())
                    .build();

            repository.save(consultaLog);
            log.info("Log de consulta persistido com sucesso: eventId={}", event.eventId());
        } catch (Exception ex) {
            log.error("Erro ao persistir log de consulta: {}", ex.getMessage(), ex);
        }
    }
}
