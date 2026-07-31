package br.com.techne.matriculas.curso.api;

import br.com.techne.matriculas.curso.domain.Curso;
import java.time.LocalDateTime;

public record CursoResponse(
    Long id,
    String codigo,
    String nome,
    Integer cargaHoraria,
    boolean ativo,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {

  public static CursoResponse from(Curso curso) {
    return new CursoResponse(
        curso.getId(),
        curso.getCodigo(),
        curso.getNome(),
        curso.getCargaHoraria(),
        curso.isAtivo(),
        curso.getCriadoEm(),
        curso.getAtualizadoEm()
    );
  }
}
