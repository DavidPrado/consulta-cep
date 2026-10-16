package br.com.cobertura.consulta.cobertura.application.port.in;

import br.com.cobertura.consulta.cobertura.application.dto.CepAtendidoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarCepsAtendidosUseCase {
    Page<CepAtendidoResponse> listar(Pageable pageable);
}
