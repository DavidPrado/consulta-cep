package br.com.cobertura.consulta.cobertura.domain.repository;

import br.com.cobertura.consulta.cobertura.domain.model.ItemStatus;
import br.com.cobertura.consulta.cobertura.domain.model.LoteCepItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface LoteCepItemRepository extends JpaRepository<LoteCepItem, UUID> {

    List<LoteCepItem> findByLoteId(UUID loteId);

    long countByLoteIdAndStatus(UUID loteId, ItemStatus status);

    @Query("SELECT i FROM LoteCepItem i WHERE i.status = 'PENDENTE' ORDER BY i.criadoEm ASC")
    List<LoteCepItem> findPendentes(Pageable pageable);

    long countByStatus(ItemStatus status);
}