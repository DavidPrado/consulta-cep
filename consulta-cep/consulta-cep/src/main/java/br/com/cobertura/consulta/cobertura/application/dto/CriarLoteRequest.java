package br.com.cobertura.consulta.cobertura.application.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CriarLoteRequest(

        @NotEmpty(message = "A lista de CEPs não pode estar vazia")
        @Size(max = 10000, message = "Um lote não pode ter mais de 10.000 CEPs")
        List<String> ceps
) {
}