package br.com.cobertura.consulta.application.service;


import br.com.cobertura.consulta.application.dto.CepConsultaResponse;
import br.com.cobertura.consulta.application.port.in.BuscarCepUseCase;
import br.com.cobertura.consulta.application.port.out.CepProvider;
import br.com.cobertura.consulta.domain.exception.CepInvalidoException;
import br.com.cobertura.consulta.domain.model.CepAtendido;
import br.com.cobertura.consulta.domain.model.OrigemConsulta;
import br.com.cobertura.consulta.domain.model.UfCobertura;
import br.com.cobertura.consulta.domain.repository.CepAtendidoRepository;
import br.com.cobertura.consulta.domain.repository.UfCoberturaRepository;
import br.com.cobertura.consulta.infrastructure.client.viacep.dto.ViaCepResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuscarCepService implements BuscarCepUseCase {

    private final CepAtendidoRepository cepAtendidoRepository;
    private final UfCoberturaRepository ufCoberturaRepository;
    private final CepProvider cepProvider;
    private final Sanitizador sanitizador;

    @Override
    @Transactional(readOnly = true)
    public CepConsultaResponse buscar(String cep) {
        String cepSanitizado = sanitizador.sanitizarCep(cep);

        if (cepSanitizado == null || !cepSanitizado.matches("\\d{8}")) {
            throw new CepInvalidoException("O CEP deve conter exatamente 8 dígitos numéricos.");
        }

        log.info("Iniciando busca de CEP: {}", cepSanitizado);

        Optional<CepAtendido> internoOpt = cepAtendidoRepository.findByCep(cepSanitizado);
        if (internoOpt.isPresent()) {
            log.info("CEP {} encontrado na base interna (origem=INTERNO)", cepSanitizado);
            return toResponseFromInterno(internoOpt.get());
        }

        log.info("CEP {} não encontrado na base interna, consultando ViaCEP", cepSanitizado);
        ViaCepResponse externo = cepProvider.buscar(cepSanitizado);

        boolean cobertura = verificarCoberturaUf(externo.uf());

        log.info("CEP {} retornado da API externa (origem=EXTERNO, cobertura={})", cepSanitizado, cobertura);
        return toResponseFromExterno(externo, cobertura);
    }

    private boolean verificarCoberturaUf(String uf) {
        if (uf == null) {
            return false;
        }
        return ufCoberturaRepository.findByUf(uf.toUpperCase())
                .map(UfCobertura::getAtendida)
                .orElse(false);
    }

    private CepConsultaResponse toResponseFromInterno(CepAtendido entity) {
        return new CepConsultaResponse(
                entity.getCep(),
                entity.getLogradouro(),
                null,
                entity.getBairro(),
                entity.getCidade(),
                entity.getUf(),
                OrigemConsulta.INTERNO,
                true,
                entity.getPrazoDias(),
                entity.getTaxa()
        );
    }

    private CepConsultaResponse toResponseFromExterno(ViaCepResponse externo, boolean cobertura) {
        return new CepConsultaResponse(
                externo.cep() != null ? externo.cep().replaceAll("\\D", "") : null,
                externo.logradouro(),
                externo.complemento(),
                externo.bairro(),
                externo.localidade(),
                externo.uf(),
                OrigemConsulta.EXTERNO,
                cobertura,
                null,
                null
        );
    }
}
