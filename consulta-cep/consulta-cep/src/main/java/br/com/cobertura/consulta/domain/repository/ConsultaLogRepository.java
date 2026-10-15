package br.com.cobertura.consulta.domain.repository;

import br.com.cobertura.consulta.domain.model.ConsultaLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConsultaLogRepository  extends JpaRepository<ConsultaLog, UUID> {
}
