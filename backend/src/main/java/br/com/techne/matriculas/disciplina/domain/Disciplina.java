package br.com.techne.matriculas.disciplina.domain;

import br.com.techne.matriculas.curso.domain.Curso;
import br.com.techne.matriculas.shared.domain.EntidadeAuditavel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "disciplina",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_disciplina_curso_codigo", columnNames = {"curso_id", "codigo"})
    }
)
public class Disciplina extends EntidadeAuditavel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "curso_id", nullable = false)
  private Curso curso;

  @Column(name = "codigo", nullable = false, length = 30)
  private String codigo;

  @Column(name = "nome", nullable = false, length = 150)
  private String nome;

  @Column(name = "carga_horaria", nullable = false)
  private Integer cargaHoraria;

  protected Disciplina() {
  }

  public Disciplina(Curso curso, String codigo, String nome, Integer cargaHoraria) {
    this.curso = curso;
    this.codigo = codigo;
    this.nome = nome;
    this.cargaHoraria = cargaHoraria;
  }

  public void atualizar(Curso curso, String codigo, String nome, Integer cargaHoraria) {
    this.curso = curso;
    this.codigo = codigo;
    this.nome = nome;
    this.cargaHoraria = cargaHoraria;
  }

  public Long getId() {
    return id;
  }

  public Curso getCurso() {
    return curso;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getNome() {
    return nome;
  }

  public Integer getCargaHoraria() {
    return cargaHoraria;
  }
}
