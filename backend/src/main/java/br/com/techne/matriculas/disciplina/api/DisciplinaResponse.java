package br.com.techne.matriculas.disciplina.api;

import br.com.techne.matriculas.disciplina.domain.Disciplina;
import java.time.LocalDateTime;

public record DisciplinaResponse(
    Long id,
    Long cursoId,
    String cursoCodigo,
    String codigo,
    String nome,
    Integer cargaHoraria,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {

  public static DisciplinaResponse from(Disciplina disciplina) {
    return new DisciplinaResponse(
        disciplina.getId(),
        disciplina.getCurso().getId(),
        disciplina.getCurso().getCodigo(),
        disciplina.getCodigo(),
        disciplina.getNome(),
        disciplina.getCargaHoraria(),
        disciplina.getCriadoEm(),
        disciplina.getAtualizadoEm()
    );
  }
}
