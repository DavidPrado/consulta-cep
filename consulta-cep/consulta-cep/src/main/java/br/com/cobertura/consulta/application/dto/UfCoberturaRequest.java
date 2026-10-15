package br.com.cobertura.consulta.application.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UfCoberturaRequest(

        @NotBlank(message = "A UF é obrigatória")
        @Pattern(regexp = "[A-Za-z]{2}", message = "A UF deve conter 2 letras")
        String uf,

        @NotNull(message = "O campo 'atendida' é obrigatório")
        Boolean atendida,

        @Min(value = 1, message = "O prazo mínimo é 1 dia")
        @Max(value = 365, message = "O prazo máximo é 365 dias")
        Integer prazoPadrao,

        @DecimalMin(value = "0.00", message = "A taxa não pode ser negativa")
        BigDecimal taxaPadrao
) {
}
