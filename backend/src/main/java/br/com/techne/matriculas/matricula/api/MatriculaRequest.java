package br.com.techne.matriculas.matricula.api;

import jakarta.validation.constraints.NotNull;

public record MatriculaRequest(
    @NotNull(message = "alunoId e obrigatorio")
    Long alunoId,

    @NotNull(message = "turmaId e obrigatorio")
    Long turmaId
) {
}
