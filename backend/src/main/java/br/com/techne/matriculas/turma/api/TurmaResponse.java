package br.com.techne.matriculas.turma.api;

import br.com.techne.matriculas.turma.domain.StatusTurma;
import br.com.techne.matriculas.turma.domain.Turma;
import java.time.LocalDateTime;

public record TurmaResponse(
    Long id,
    Long disciplinaId,
    String disciplinaCodigo,
    String codigo,
    String periodo,
    Integer vagasTotais,
    Integer vagasOcupadas,
    Integer vagasDisponiveis,
    StatusTurma status,
    Long version,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {

  public static TurmaResponse from(Turma turma) {
    return new TurmaResponse(
        turma.getId(),
        turma.getDisciplina().getId(),
        turma.getDisciplina().getCodigo(),
        turma.getCodigo(),
        turma.getPeriodo(),
        turma.getVagasTotais(),
        turma.getVagasOcupadas(),
        turma.vagasDisponiveis(),
        turma.getStatus(),
        turma.getVersion(),
        turma.getCriadoEm(),
        turma.getAtualizadoEm()
    );
  }
}
