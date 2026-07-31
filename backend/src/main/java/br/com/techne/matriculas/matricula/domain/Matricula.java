package br.com.techne.matriculas.matricula.domain;

import br.com.techne.matriculas.aluno.domain.Aluno;
import br.com.techne.matriculas.shared.domain.EntidadeAuditavel;
import br.com.techne.matriculas.shared.exception.NegocioException;
import br.com.techne.matriculas.turma.domain.Turma;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "matricula",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_matricula_aluno_turma", columnNames = {"aluno_id", "turma_id"})
    }
)
public class Matricula extends EntidadeAuditavel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "aluno_id", nullable = false)
  private Aluno aluno;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "turma_id", nullable = false)
  private Turma turma;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private StatusMatricula status = StatusMatricula.PENDENTE;

  @Column(name = "confirmado_em")
  private LocalDateTime confirmadoEm;

  @Column(name = "cancelado_em")
  private LocalDateTime canceladoEm;

  protected Matricula() {
  }

  private Matricula(Aluno aluno, Turma turma) {
    this.aluno = aluno;
    this.turma = turma;
    this.status = StatusMatricula.PENDENTE;
  }

  /**
   * Cria matricula em PENDENTE. Nao consome vaga.
   * Premissa: turma deve estar ABERTA (validacao de negocio no servico/dominio).
   */
  public static Matricula criarPendente(Aluno aluno, Turma turma) {
    if (aluno == null || !aluno.isAtivo()) {
      throw new NegocioException("aluno.inativo", "Aluno inativo nao pode se matricular");
    }
    if (turma == null || !turma.isAberta()) {
      throw new NegocioException("turma.fechada", "Nao e permitido matricular em turma FECHADA");
    }
    return new Matricula(aluno, turma);
  }

  public void confirmar() {
    if (status != StatusMatricula.PENDENTE) {
      throw new NegocioException(
          "matricula.status.invalido",
          "Somente matricula PENDENTE pode ser confirmada (status atual: " + status + ")"
      );
    }
    turma.consumirVaga();
    this.status = StatusMatricula.CONFIRMADA;
    this.confirmadoEm = LocalDateTime.now();
  }

  public void cancelar() {
    if (status == StatusMatricula.CANCELADA) {
      throw new NegocioException("matricula.ja.cancelada", "Matricula ja esta CANCELADA");
    }
    if (status == StatusMatricula.CONFIRMADA) {
      turma.liberarVaga();
    }
    this.status = StatusMatricula.CANCELADA;
    this.canceladoEm = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public Aluno getAluno() {
    return aluno;
  }

  public Turma getTurma() {
    return turma;
  }

  public StatusMatricula getStatus() {
    return status;
  }

  public LocalDateTime getConfirmadoEm() {
    return confirmadoEm;
  }

  public LocalDateTime getCanceladoEm() {
    return canceladoEm;
  }
}
