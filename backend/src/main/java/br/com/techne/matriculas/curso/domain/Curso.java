package br.com.techne.matriculas.curso.domain;

import br.com.techne.matriculas.shared.domain.EntidadeAuditavel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "curso",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_curso_codigo", columnNames = "codigo")
    }
)
public class Curso extends EntidadeAuditavel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "codigo", nullable = false, length = 30)
  private String codigo;

  @Column(name = "nome", nullable = false, length = 150)
  private String nome;

  @Column(name = "carga_horaria", nullable = false)
  private Integer cargaHoraria;

  @Column(name = "ativo", nullable = false)
  private boolean ativo = true;

  protected Curso() {
  }

  public Curso(String codigo, String nome, Integer cargaHoraria) {
    this.codigo = codigo;
    this.nome = nome;
    this.cargaHoraria = cargaHoraria;
    this.ativo = true;
  }

  public void atualizar(String codigo, String nome, Integer cargaHoraria) {
    this.codigo = codigo;
    this.nome = nome;
    this.cargaHoraria = cargaHoraria;
  }

  public void ativar() {
    this.ativo = true;
  }

  public void inativar() {
    this.ativo = false;
  }

  public Long getId() {
    return id;
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

  public boolean isAtivo() {
    return ativo;
  }
}
