package br.com.techne.matriculas.turma.domain;

import br.com.techne.matriculas.disciplina.domain.Disciplina;
import br.com.techne.matriculas.shared.domain.EntidadeAuditavel;
import br.com.techne.matriculas.shared.exception.NegocioException;
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
import jakarta.persistence.Version;

@Entity
@Table(
    name = "turma",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_turma_disciplina_codigo_periodo",
            columnNames = {"disciplina_id", "codigo", "periodo"}
        )
    }
)
public class Turma extends EntidadeAuditavel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "disciplina_id", nullable = false)
  private Disciplina disciplina;

  @Column(name = "codigo", nullable = false, length = 30)
  private String codigo;

  @Column(name = "periodo", nullable = false, length = 20)
  private String periodo;

  @Column(name = "vagas_totais", nullable = false)
  private Integer vagasTotais;

  @Column(name = "vagas_ocupadas", nullable = false)
  private Integer vagasOcupadas = 0;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private StatusTurma status = StatusTurma.ABERTA;

  @Version
  @Column(name = "version", nullable = false)
  private Long version;

  protected Turma() {
  }

  public Turma(Disciplina disciplina, String codigo, String periodo, Integer vagasTotais) {
    if (vagasTotais == null || vagasTotais < 0) {
      throw new NegocioException("turma.vagas.invalidas", "Vagas totais devem ser >= 0");
    }
    this.disciplina = disciplina;
    this.codigo = codigo;
    this.periodo = periodo;
    this.vagasTotais = vagasTotais;
    this.vagasOcupadas = 0;
    this.status = StatusTurma.ABERTA;
  }

  public void atualizar(String codigo, String periodo, Integer vagasTotais) {
    if (vagasTotais == null || vagasTotais < 0) {
      throw new NegocioException("turma.vagas.invalidas", "Vagas totais devem ser >= 0");
    }
    if (vagasTotais < this.vagasOcupadas) {
      throw new NegocioException(
          "turma.vagas.menores.que.ocupadas",
          "Vagas totais nao podem ser menores que as ocupadas (" + this.vagasOcupadas + ")"
      );
    }
    this.codigo = codigo;
    this.periodo = periodo;
    this.vagasTotais = vagasTotais;
  }

  public void abrir() {
    this.status = StatusTurma.ABERTA;
  }

  public void fechar() {
    this.status = StatusTurma.FECHADA;
  }

  public boolean isAberta() {
    return StatusTurma.ABERTA.equals(this.status);
  }

  public boolean temVagaDisponivel() {
    return vagasOcupadas < vagasTotais;
  }

  public int vagasDisponiveis() {
    return Math.max(0, vagasTotais - vagasOcupadas);
  }

  /**
   * Consome uma vaga no momento da confirmacao da matricula.
   * Deve ser chamado sob lock pessimista da turma.
   */
  public void consumirVaga() {
    if (!isAberta()) {
      throw new NegocioException("turma.fechada", "Nao e permitido matricular em turma FECHADA");
    }
    if (!temVagaDisponivel()) {
      throw new NegocioException("turma.sem.vaga", "Turma sem vagas disponiveis");
    }
    this.vagasOcupadas = this.vagasOcupadas + 1;
  }

  /**
   * Libera uma vaga ao cancelar uma matricula CONFIRMADA.
   */
  public void liberarVaga() {
    if (this.vagasOcupadas <= 0) {
      throw new NegocioException("turma.vagas.ocupadas.zero", "Nao ha vagas ocupadas para liberar");
    }
    this.vagasOcupadas = this.vagasOcupadas - 1;
  }

  public Long getId() {
    return id;
  }

  public Disciplina getDisciplina() {
    return disciplina;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getPeriodo() {
    return periodo;
  }

  public Integer getVagasTotais() {
    return vagasTotais;
  }

  public Integer getVagasOcupadas() {
    return vagasOcupadas;
  }

  public StatusTurma getStatus() {
    return status;
  }

  public Long getVersion() {
    return version;
  }
}
