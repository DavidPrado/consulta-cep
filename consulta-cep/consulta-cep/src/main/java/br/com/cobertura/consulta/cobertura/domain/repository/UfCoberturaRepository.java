package br.com.cobertura.consulta.cobertura.domain.repository;

import br.com.cobertura.consulta.cobertura.domain.model.UfCobertura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UfCoberturaRepository extends JpaRepository<UfCobertura, UUID> {
    Optional<UfCobertura> findByUf(String uf);
}
