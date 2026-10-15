package br.com.cobertura.consulta.cobertura.domain.repository;

import br.com.cobertura.consulta.cobertura.domain.model.CepAtendido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CepAtendidoRepository extends JpaRepository<CepAtendido, UUID> {

    Optional<CepAtendido> findByCep(String cep);

    boolean existsByCep(String cep);
}
