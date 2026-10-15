package br.com.cobertura.consulta.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "consulta_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ConsultaLog {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "cep", nullable = false, length = 8)
    private String cep;

    @Column(name = "origem", nullable = false, length = 20)
    private String origem;

    @Column(name = "uf", length = 2)
    private String uf;

    @Column(name = "cobertura")
    private Boolean cobertura;

    @Column(name = "sucesso", nullable = false)
    private Boolean sucesso;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb")
    private String payload;

    @Column(name = "erro", length = 500)
    private String erro;

    @Column(name = "trace_id", length = 64)
    private String traceId;

    @Column(name = "consultado_em", nullable = false)
    private LocalDateTime consultadoEm;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;
}
