package br.com.techne.matriculas.aluno.api;

import br.com.techne.matriculas.aluno.domain.Aluno;
import java.time.LocalDateTime;

public record AlunoResponse(
    Long id,
    String nome,
    String email,
    String ra,
    boolean ativo,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {

  public static AlunoResponse from(Aluno aluno) {
    return new AlunoResponse(
        aluno.getId(),
        aluno.getNome(),
        aluno.getEmail(),
        aluno.getRa(),
        aluno.isAtivo(),
        aluno.getCriadoEm(),
        aluno.getAtualizadoEm()
    );
  }
}
