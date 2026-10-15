package br.com.cobertura.consulta.consulta.application.dto;

import br.com.cobertura.consulta.domain.model.OrigemConsulta;

import java.math.BigDecimal;

public record CepConsultaResponse(String cep,
                                  String logradouro,
                                  String complemento,
                                  String bairro,
                                  String cidade,
                                  String uf,
                                  OrigemConsulta origem,
                                  Boolean cobertura,
                                  Integer prazoDias,
                                  BigDecimal taxa) {
}
