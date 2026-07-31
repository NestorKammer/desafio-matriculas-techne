package br.com.techne.matriculas.turma.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TurmaRequest(
    @NotNull(message = "disciplinaId e obrigatorio")
    Long disciplinaId,

    @NotBlank(message = "Codigo e obrigatorio")
    @Size(max = 30, message = "Codigo deve ter no maximo 30 caracteres")
    String codigo,

    @NotBlank(message = "Periodo e obrigatorio")
    @Size(max = 20, message = "Periodo deve ter no maximo 20 caracteres")
    String periodo,

    @NotNull(message = "vagasTotais e obrigatorio")
    @Min(value = 0, message = "vagasTotais deve ser >= 0")
    Integer vagasTotais
) {
}
