package br.com.techne.matriculas.aluno.domain;

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
    name = "aluno",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_aluno_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_aluno_ra", columnNames = "ra")
    }
)
public class Aluno extends EntidadeAuditavel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "nome", nullable = false, length = 150)
  private String nome;

  @Column(name = "email", nullable = false, length = 180)
  private String email;

  /** Registro Academico do aluno. */
  @Column(name = "ra", nullable = false, length = 30)
  private String ra;

  @Column(name = "ativo", nullable = false)
  private boolean ativo = true;

  protected Aluno() {
  }

  public Aluno(String nome, String email, String ra) {
    this.nome = nome;
    this.email = email;
    this.ra = ra;
    this.ativo = true;
  }

  public void atualizarDados(String nome, String email, String ra) {
    this.nome = nome;
    this.email = email;
    this.ra = ra;
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

  public String getNome() {
    return nome;
  }

  public String getEmail() {
    return email;
  }

  public String getRa() {
    return ra;
  }

  public boolean isAtivo() {
    return ativo;
  }
}
