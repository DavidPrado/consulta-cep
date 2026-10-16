package br.com.cobertura.consulta.cobertura.application.port.in;

import br.com.cobertura.consulta.cobertura.application.dto.UfCoberturaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface  ListarUfCoberturaUseCase {
    Page<UfCoberturaResponse> listar(Pageable pageable);
}
