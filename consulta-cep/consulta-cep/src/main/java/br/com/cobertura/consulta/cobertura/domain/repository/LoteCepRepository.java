package br.com.cobertura.consulta.cobertura.domain.repository;

import br.com.cobertura.consulta.cobertura.domain.model.LoteCep;
import br.com.cobertura.consulta.cobertura.domain.model.LoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoteCepRepository extends JpaRepository<LoteCep, UUID> {
    List<LoteCep> findByStatus(LoteStatus status);
}
