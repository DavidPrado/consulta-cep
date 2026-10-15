package br.com.cobertura.consulta.application.service;


import br.com.cobertura.consulta.application.dto.CepAtendidoRequest;
import br.com.cobertura.consulta.application.dto.CepAtendidoResponse;
import br.com.cobertura.consulta.application.port.in.CadastrarCepAtendidoUseCase;
import br.com.cobertura.consulta.application.port.in.ListarCepsAtendidosUseCase;
import br.com.cobertura.consulta.domain.exception.CepDuplicadoException;
import br.com.cobertura.consulta.domain.model.CepAtendido;
import br.com.cobertura.consulta.domain.repository.CepAtendidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CepAtendidoService implements CadastrarCepAtendidoUseCase, ListarCepsAtendidosUseCase {

    private final CepAtendidoRepository repository;
    private final Sanitizador sanitizador;

    @Override
    @Transactional
    public CepAtendidoResponse cadastrar(CepAtendidoRequest request) {
        String cepSanitizado = sanitizador.sanitizarCep(request.cep());

        if (repository.existsByCep(cepSanitizado)) {
            log.warn("Tentativa de cadastrar CEP duplicado: {}", cepSanitizado);
            throw new CepDuplicadoException(cepSanitizado);
        }

        CepAtendido entity = CepAtendido.builder()
                .id(UUID.randomUUID())
                .cep(cepSanitizado)
                .logradouro(sanitizador.sanitizarTexto(request.logradouro(), 150))
                .bairro(sanitizador.sanitizarTexto(request.bairro(), 100))
                .cidade(sanitizador.sanitizarTexto(request.cidade(), 100))
                .uf(sanitizador.sanitizarUf(request.uf()))
                .prazoDias(request.prazoDias())
                .taxa(request.taxa())
                .criadoEm(LocalDateTime.now())
                .build();

        CepAtendido salvo = repository.save(entity);
        log.info("CEP cadastrado com sucesso: {}", salvo.getCep());

        return CepAtendidoResponse.fromEntity(salvo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CepAtendidoResponse> listar() {
        return repository.findAll()
                .stream()
                .map(CepAtendidoResponse::fromEntity)
                .toList();
    }
}
