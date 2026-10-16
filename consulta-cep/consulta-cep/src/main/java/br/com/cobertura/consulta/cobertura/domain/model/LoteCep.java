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

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lote_cep")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoteCep {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LoteStatus status;

    @Column(name = "total", nullable = false)
    private Integer total;

    @Column(name = "processados", nullable = false)
    private Integer processados;

    @Column(name = "pendentes", nullable = false)
    private Integer pendentes;

    @Column(name = "falhas", nullable = false)
    private Integer falhas;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}