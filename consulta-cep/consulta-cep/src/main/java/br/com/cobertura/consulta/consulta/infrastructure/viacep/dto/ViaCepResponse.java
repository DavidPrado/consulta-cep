package br.com.cobertura.consulta.consulta.infrastructure.viacep.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ViaCepResponse(
        String cep,
        String logradouro,
        String complemento,
        String unidade,
        String bairro,
        String localidade,
        String uf,
        String estado,
        String regiao,
        String ibge,
        String gia,
        String ddd,
        String siafi,
        @JsonProperty("erro") Boolean erro
) {

    public boolean isNaoEncontrado() {
        return Boolean.TRUE.equals(erro);
    }
}
