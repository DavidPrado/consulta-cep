package br.com.cobertura.consulta.cobertura.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "uf_cobertura")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UfCobertura {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "uf", nullable = false, length = 2, unique = true)
    private String uf;

    @Column(name = "atendida", nullable = false)
    private Boolean atendida;

    @Column(name = "prazo_padrao")
    private Integer prazoPadrao;

    @Column(name = "taxa_padrao", precision = 10, scale = 2)
    private BigDecimal taxaPadrao;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}
