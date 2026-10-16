package br.com.cobertura.consulta.cobertura.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "lote_cep_item")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoteCepItem {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "lote_id", nullable = false)
    private UUID loteId;

    @Column(name = "cep", nullable = false, length = 8)
    private String cep;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ItemStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dados", columnDefinition = "jsonb")
    private String dados;

    @Column(name = "erro", length = 500)
    private String erro;

    @Column(name = "tentativas", nullable = false)
    private Integer tentativas;

    @Column(name = "ultima_tentativa")
    private LocalDateTime ultimaTentativa;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}