package br.com.techne.matriculas.matricula.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.techne.matriculas.aluno.domain.Aluno;
import br.com.techne.matriculas.curso.domain.Curso;
import br.com.techne.matriculas.disciplina.domain.Disciplina;
import br.com.techne.matriculas.shared.exception.NegocioException;
import br.com.techne.matriculas.turma.domain.Turma;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MatriculaRegrasTest {

  private Aluno aluno;
  private Turma turma;

  @BeforeEach
  void setUp() {
    aluno = new Aluno("Maria Silva", "maria@email.com", "RA001");
    Curso curso = new Curso("ENG", "Engenharia", 3600);
    Disciplina disciplina = new Disciplina(curso, "CAL1", "Calculo I", 80);
    turma = new Turma(disciplina, "T1", "2026.1", 2);
  }

  @Test
  void criarPendenteNaoConsomeVaga() {
    Matricula matricula = Matricula.criarPendente(aluno, turma);

    assertEquals(StatusMatricula.PENDENTE, matricula.getStatus());
    assertEquals(0, turma.getVagasOcupadas());
  }

  @Test
  void confirmarConsomeVaga() {
    Matricula matricula = Matricula.criarPendente(aluno, turma);

    matricula.confirmar();

    assertEquals(StatusMatricula.CONFIRMADA, matricula.getStatus());
    assertEquals(1, turma.getVagasOcupadas());
  }

  @Test
  void cancelarConfirmadaLiberaVaga() {
    Matricula matricula = Matricula.criarPendente(aluno, turma);
    matricula.confirmar();

    matricula.cancelar();

    assertEquals(StatusMatricula.CANCELADA, matricula.getStatus());
    assertEquals(0, turma.getVagasOcupadas());
  }

  @Test
  void cancelarPendenteNaoAlteraVagas() {
    Matricula matricula = Matricula.criarPendente(aluno, turma);

    matricula.cancelar();

    assertEquals(StatusMatricula.CANCELADA, matricula.getStatus());
    assertEquals(0, turma.getVagasOcupadas());
  }

  @Test
  void naoPermiteMatricularEmTurmaFechada() {
    turma.fechar();

    assertThrows(NegocioException.class, () -> Matricula.criarPendente(aluno, turma));
  }

  @Test
  void naoPermiteConfirmarSemVaga() {
    Matricula m1 = Matricula.criarPendente(aluno, turma);
    m1.confirmar();

    Aluno aluno2 = new Aluno("Joao", "joao@email.com", "RA002");
    Matricula m2 = Matricula.criarPendente(aluno2, turma);
    m2.confirmar();

    Aluno aluno3 = new Aluno("Ana", "ana@email.com", "RA003");
    Matricula m3 = Matricula.criarPendente(aluno3, turma);

    NegocioException ex = assertThrows(NegocioException.class, m3::confirmar);
    assertEquals("turma.sem.vaga", ex.getCodigo());
  }

  @Test
  void alunoInativoNaoMatricula() {
    aluno.inativar();
    assertThrows(NegocioException.class, () -> Matricula.criarPendente(aluno, turma));
  }
}
