package br.com.cobertura.consulta.cobertura.application;

import br.com.cobertura.consulta.cobertura.application.dto.CriarLoteRequest;
import br.com.cobertura.consulta.cobertura.application.dto.LoteCepItemResponse;
import br.com.cobertura.consulta.cobertura.application.dto.LoteCepResponse;
import br.com.cobertura.consulta.cobertura.application.dto.LoteDetalheResponse;
import br.com.cobertura.consulta.cobertura.application.port.in.ConsultarLoteUseCase;
import br.com.cobertura.consulta.cobertura.application.port.in.CriarLoteUseCase;
import br.com.cobertura.consulta.cobertura.domain.model.ItemStatus;
import br.com.cobertura.consulta.cobertura.domain.model.LoteCep;
import br.com.cobertura.consulta.cobertura.domain.model.LoteCepItem;
import br.com.cobertura.consulta.cobertura.domain.model.LoteStatus;
import br.com.cobertura.consulta.cobertura.domain.repository.LoteCepItemRepository;
import br.com.cobertura.consulta.cobertura.domain.repository.LoteCepRepository;
import br.com.cobertura.consulta.shared.exception.LoteNaoEncontradoException;
import br.com.cobertura.consulta.shared.service.Sanitizador;
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
public class LoteCepService implements CriarLoteUseCase, ConsultarLoteUseCase {

    private final LoteCepRepository loteRepository;
    private final LoteCepItemRepository itemRepository;
    private final Sanitizador sanitizador;

    @Override
    @Transactional
    public LoteCepResponse criar(CriarLoteRequest request) {
        List<String> cepsSanitizados = request.ceps().stream()
                .map(sanitizador::sanitizarCep)
                .filter(cep -> cep != null && cep.matches("\\d{8}"))
                .distinct()
                .toList();

        if (cepsSanitizados.isEmpty()) {
            throw new br.com.cobertura.consulta.shared.exception.CepInvalidoException(
                    "Nenhum CEP válido foi fornecido no lote.");
        }

        UUID loteId = UUID.randomUUID();
        LocalDateTime agora = LocalDateTime.now();

        LoteCep lote = LoteCep.builder()
                .id(loteId)
                .status(LoteStatus.PROCESSANDO)
                .total(cepsSanitizados.size())
                .processados(0)
                .pendentes(cepsSanitizados.size())
                .falhas(0)
                .criadoEm(agora)
                .build();

        loteRepository.save(lote);

        List<LoteCepItem> itens = cepsSanitizados.stream()
                .map(cep -> LoteCepItem.builder()
                        .id(UUID.randomUUID())
                        .loteId(loteId)
                        .cep(cep)
                        .status(ItemStatus.PENDENTE)
                        .tentativas(0)
                        .criadoEm(agora)
                        .build())
                .toList();

        itemRepository.saveAll(itens);

        log.info("Lote criado: id={}, total de CEPs={}", loteId, cepsSanitizados.size());

        return LoteCepResponse.fromEntity(lote);
    }

    @Override
    @Transactional(readOnly = true)
    public LoteDetalheResponse consultar(UUID loteId) {
        LoteCep lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new LoteNaoEncontradoException(loteId.toString()));

        List<LoteCepItemResponse> itens = itemRepository.findByLoteId(loteId)
                .stream()
                .map(LoteCepItemResponse::fromEntity)
                .toList();

        return new LoteDetalheResponse(LoteCepResponse.fromEntity(lote), itens);
    }
}