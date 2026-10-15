package br.com.cobertura.consulta.application.service;


import br.com.cobertura.consulta.application.dto.UfCoberturaRequest;
import br.com.cobertura.consulta.application.dto.UfCoberturaResponse;
import br.com.cobertura.consulta.application.port.in.AtualizarUfCoberturaUseCase;
import br.com.cobertura.consulta.application.port.in.CadastrarUfCoberturaUseCase;
import br.com.cobertura.consulta.application.port.in.ListarUfCoberturaUseCase;
import br.com.cobertura.consulta.domain.exception.UfInvalidaException;
import br.com.cobertura.consulta.domain.model.UfCobertura;
import br.com.cobertura.consulta.domain.repository.UfCoberturaRepository;
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
public class UfCoberturaService implements CadastrarUfCoberturaUseCase, AtualizarUfCoberturaUseCase, ListarUfCoberturaUseCase {

    private final UfCoberturaRepository repository;
    private final Sanitizador sanitizador;


    @Override
    @Transactional
    public UfCoberturaResponse cadastrar(UfCoberturaRequest request) {
        String ufSanitizada = sanitizador.sanitizarUf(request.uf());

        if (repository.findByUf(ufSanitizada).isPresent()) {
            log.warn("Tentativa de cadastrar UF duplicada: {}", ufSanitizada);
            throw new UfInvalidaException("A UF " + ufSanitizada + " já está cadastrada.");
        }

        UfCobertura entity = UfCobertura.builder()
                .id(UUID.randomUUID())
                .uf(ufSanitizada)
                .atendida(request.atendida())
                .prazoPadrao(request.prazoPadrao())
                .taxaPadrao(request.taxaPadrao())
                .criadoEm(LocalDateTime.now())
                .build();

        UfCobertura salvo = repository.save(entity);
        log.info("UF cadastrada com sucesso: {}", salvo.getUf());

        return UfCoberturaResponse.fromEntity(salvo);
    }

    @Override
    @Transactional
    public UfCoberturaResponse atualizar(String uf, UfCoberturaRequest request) {
        String ufSanitizada = sanitizador.sanitizarUf(uf);

        UfCobertura existente = repository.findByUf(ufSanitizada)
                .orElseThrow(() -> new UfInvalidaException("UF " + ufSanitizada + " não encontrada para atualização."));

        existente.setAtendida(request.atendida());
        existente.setPrazoPadrao(request.prazoPadrao());
        existente.setTaxaPadrao(request.taxaPadrao());
        existente.setAtualizadoEm(LocalDateTime.now());

        UfCobertura atualizado = repository.save(existente);
        log.info("UF atualizada com sucesso: {}", atualizado.getUf());

        return UfCoberturaResponse.fromEntity(atualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UfCoberturaResponse> listar() {
        return repository.findAll()
                .stream()
                .map(UfCoberturaResponse::fromEntity)
                .toList();
    }
}
