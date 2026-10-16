package br.com.cobertura.consulta.cobertura.infrastructure.worker;

import br.com.cobertura.consulta.cobertura.domain.model.ItemStatus;
import br.com.cobertura.consulta.cobertura.domain.model.LoteCep;
import br.com.cobertura.consulta.cobertura.domain.model.LoteCepItem;
import br.com.cobertura.consulta.cobertura.domain.model.LoteStatus;
import br.com.cobertura.consulta.cobertura.domain.repository.LoteCepItemRepository;
import br.com.cobertura.consulta.cobertura.domain.repository.LoteCepRepository;
import br.com.cobertura.consulta.consulta.application.BuscarCepService;
import br.com.cobertura.consulta.consulta.application.dto.CepConsultaResponse;
import br.com.cobertura.consulta.shared.exception.CepInvalidoException;
import br.com.cobertura.consulta.shared.exception.ExternalApiException;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoteProcessamentoWorker {

    private final LoteCepItemRepository itemRepository;
    private final LoteCepRepository loteRepository;
    private final BuscarCepService buscarCepService;
    private final ObjectMapper objectMapper;

    @Qualifier("loteExecutor")
    private final ExecutorService loteExecutor;

    @Value("${app.lote.worker.batch-size:50}")
    private int batchSize;

    @Value("${app.lote.worker.max-tentativas:3}")
    private int maxTentativas;

    @Value("${app.lote.worker.paralelismo:20}")
    private int paralelismo;

    @Scheduled(fixedDelayString = "${app.lote.worker.intervalo-ms:10000}")
    public void processarPendentes() {
        long inicio = System.currentTimeMillis();

        List<LoteCepItem> pendentes = itemRepository.findPendentes(PageRequest.of(0, batchSize));
        if (pendentes.isEmpty()) {
            return;
        }

        log.info("Worker: {} itens pendentes para processar (paralelismo={})", pendentes.size(), paralelismo);

        List<LoteCepItem> processados = processarEmParalelo(pendentes);

        itemRepository.saveAll(processados);

        Set<UUID> lotesAfetados = new HashSet<>();
        processados.forEach(p -> lotesAfetados.add(p.getLoteId()));
        lotesAfetados.forEach(this::atualizarContadoresLote);

        long duracao = System.currentTimeMillis() - inicio;
        double throughput = processados.size() / (duracao / 1000.0);

        log.info("Worker: ciclo concluído em {}ms | {} itens | {} CEPs/s | {} lotes atualizados",
                duracao, processados.size(), String.format("%.1f", throughput), lotesAfetados.size());
    }

    private List<LoteCepItem> processarEmParalelo(List<LoteCepItem> itens) {
        List<Future<LoteCepItem>> futures = itens.stream().map(item -> loteExecutor.submit(() -> processarItem(item))).toList();

        return futures.stream().map(this::obterResultado).filter(Objects::nonNull).toList();
    }

    private LoteCepItem obterResultado(Future<LoteCepItem> future) {
        try {
            return future.get();
        } catch (Exception ex) {
            log.error("Falha ao obter resultado de Virtual Thread: {}", ex.getMessage());
            return null;
        }
    }

    private LoteCepItem processarItem(LoteCepItem item) {
        item.setTentativas(item.getTentativas() + 1);
        item.setUltimaTentativa(LocalDateTime.now());
        item.setAtualizadoEm(LocalDateTime.now());

        try {
            CepConsultaResponse response = buscarCepService.buscar(item.getCep());

            item.setDados(objectMapper.writeValueAsString(response));
            item.setStatus(ItemStatus.PROCESSADO);
            item.setErro(null);

            log.debug("[{}] Item processado: cep={}, origem={}", Thread.currentThread().getName(), item.getCep(), response.origem());

        } catch (CepInvalidoException ex) {
            item.setStatus(ItemStatus.FALHOU);
            item.setErro("CEP inválido: " + ex.getMessage());
            log.warn("Item falhou permanentemente: cep={}", item.getCep());

        } catch (ExternalApiException ex) {
            if (item.getTentativas() >= maxTentativas) {
                item.setStatus(ItemStatus.FALHOU);
                item.setErro("Máximo de tentativas atingido: " + truncar(ex.getMessage(), 400));
                log.warn("Item falhou após {} tentativas: cep={}", item.getTentativas(), item.getCep());
            } else {
                log.debug("Item aguardando retry ({}/{}): cep={}", item.getTentativas(), maxTentativas, item.getCep());
            }

        } catch (Exception ex) {
            item.setStatus(ItemStatus.FALHOU);
            item.setErro(truncar("Erro inesperado: " + ex.getMessage(), 400));
            log.error("Erro inesperado no item cep={}: {}", item.getCep(), ex.getMessage());
        }

        return item;
    }

    private void atualizarContadoresLote(UUID loteId) {
        long processados = itemRepository.countByLoteIdAndStatus(loteId, ItemStatus.PROCESSADO);
        long falhas = itemRepository.countByLoteIdAndStatus(loteId, ItemStatus.FALHOU);
        long pendentes = itemRepository.countByLoteIdAndStatus(loteId, ItemStatus.PENDENTE);

        LoteCep lote = loteRepository.findById(loteId).orElse(null);
        if (lote == null) {
            return;
        }

        lote.setProcessados((int) processados);
        lote.setFalhas((int) falhas);
        lote.setPendentes((int) pendentes);
        lote.setAtualizadoEm(LocalDateTime.now());

        if (pendentes == 0) {
            if (falhas == 0) {
                lote.setStatus(LoteStatus.CONCLUIDO);
            } else if (processados > 0) {
                lote.setStatus(LoteStatus.PARCIAL);
            } else {
                lote.setStatus(LoteStatus.FALHOU);
            }
        }

        loteRepository.save(lote);
    }

    private String truncar(String texto, int max) {
        if (texto == null) {
            return null;
        }
        return texto.length() > max ? texto.substring(0, max) : texto;
    }
}