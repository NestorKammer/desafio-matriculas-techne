package br.com.techne.matriculas.matricula.api;

import br.com.techne.matriculas.matricula.domain.Matricula;
import br.com.techne.matriculas.matricula.domain.StatusMatricula;
import java.time.LocalDateTime;

public record MatriculaResponse(
    Long id,
    Long alunoId,
    String alunoNome,
    String alunoRa,
    Long turmaId,
    String turmaCodigo,
    String turmaPeriodo,
    StatusMatricula status,
    LocalDateTime criadoEm,
    LocalDateTime confirmadoEm,
    LocalDateTime canceladoEm,
    LocalDateTime atualizadoEm
) {

  public static MatriculaResponse from(Matricula matricula) {
    return new MatriculaResponse(
        matricula.getId(),
        matricula.getAluno().getId(),
        matricula.getAluno().getNome(),
        matricula.getAluno().getRa(),
        matricula.getTurma().getId(),
        matricula.getTurma().getCodigo(),
        matricula.getTurma().getPeriodo(),
        matricula.getStatus(),
        matricula.getCriadoEm(),
        matricula.getConfirmadoEm(),
        matricula.getCanceladoEm(),
        matricula.getAtualizadoEm()
    );
  }
}
