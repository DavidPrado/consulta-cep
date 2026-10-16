package br.com.cobertura.consulta.cobertura.infrastructure.kafka;

import br.com.cobertura.consulta.cobertura.domain.model.CepAtendido;
import br.com.cobertura.consulta.cobertura.domain.repository.CepAtendidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CepDescobertoConsumer {

    private final CepAtendidoRepository repository;

    @KafkaListener(
            topics = "${app.kafka.topics.cep-descoberto}",
            groupId = "${spring.kafka.consumer.group-id}-cep-descoberto"
    )
    public void consumir(CepDescobertoEvent event) {
        try {
            log.info("CepDescoberto recebido: cep={}, uf={}", event.cep(), event.uf());

            if (repository.existsByCep(event.cep())) {
                log.debug("CEP já cadastrado, ignorando: {}", event.cep());
                return;
            }

            CepAtendido entity = CepAtendido.builder()
                    .id(UUID.randomUUID())
                    .cep(event.cep())
                    .logradouro(event.logradouro())
                    .bairro(event.bairro())
                    .cidade(event.cidade())
                    .uf(event.uf())
                    .prazoDias(event.prazoDias() != null ? event.prazoDias() : 5)
                    .taxa(event.taxa() != null ? event.taxa() : java.math.BigDecimal.ZERO)
                    .criadoEm(LocalDateTime.now())
                    .build();

            repository.save(entity);
            log.info("CEP auto-populado com sucesso: {}", event.cep());
        } catch (Exception ex) {
            log.error("Erro ao persistir CepDescoberto: {}", ex.getMessage(), ex);
        }
    }
}