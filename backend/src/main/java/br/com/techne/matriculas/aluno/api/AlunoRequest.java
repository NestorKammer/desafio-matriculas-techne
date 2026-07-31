package br.com.techne.matriculas.aluno.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlunoRequest(
    @NotBlank(message = "Nome e obrigatorio")
    @Size(max = 150, message = "Nome deve ter no maximo 150 caracteres")
    String nome,

    @NotBlank(message = "Email e obrigatorio")
    @Email(message = "Email invalido")
    @Size(max = 180, message = "Email deve ter no maximo 180 caracteres")
    String email,

    @NotBlank(message = "RA e obrigatorio")
    @Size(max = 30, message = "RA deve ter no maximo 30 caracteres")
    String ra
) {
}
