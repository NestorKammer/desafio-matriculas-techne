package br.com.techne.matriculas.curso.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CursoRequest(
    @NotBlank(message = "Codigo e obrigatorio")
    @Size(max = 30, message = "Codigo deve ter no maximo 30 caracteres")
    String codigo,

    @NotBlank(message = "Nome e obrigatorio")
    @Size(max = 150, message = "Nome deve ter no maximo 150 caracteres")
    String nome,

    @NotNull(message = "Carga horaria e obrigatoria")
    @Min(value = 1, message = "Carga horaria deve ser >= 1")
    Integer cargaHoraria
) {
}
