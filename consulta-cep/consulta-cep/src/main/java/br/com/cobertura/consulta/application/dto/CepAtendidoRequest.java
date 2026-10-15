package br.com.cobertura.consulta.application.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CepAtendidoRequest(

        @NotBlank(message = "O CEP é obrigatório")
        @Pattern(regexp = "\\d{8}", message = "O CEP deve conter exatamente 8 dígitos numéricos")
        String cep,

        @Size(max = 150, message = "Logradouro não pode exceder 150 caracteres")
        String logradouro,

        @Size(max = 100, message = "Bairro não pode exceder 100 caracteres")
        String bairro,

        @Size(max = 100, message = "Cidade não pode exceder 100 caracteres")
        String cidade,

        @NotBlank(message = "A UF é obrigatória")
        @Pattern(regexp = "[A-Za-z]{2}", message = "A UF deve conter 2 letras")
        String uf,

        @NotNull(message = "O prazo em dias é obrigatório")
        @Min(value = 1, message = "O prazo mínimo é 1 dia")
        @Max(value = 365, message = "O prazo máximo é 365 dias")
        Integer prazoDias,

        @NotNull(message = "A taxa é obrigatória")
        @DecimalMin(value = "0.00", message = "A taxa não pode ser negativa")
        BigDecimal taxa) {
}
