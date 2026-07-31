package br.com.techne.matriculas.shared.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class EntidadeAuditavel {

  @Column(name = "criado_em", nullable = false, updatable = false)
  private LocalDateTime criadoEm;

  @Column(name = "atualizado_em")
  private LocalDateTime atualizadoEm;

  @PrePersist
  protected void prePersist() {
    LocalDateTime agora = LocalDateTime.now();
    if (criadoEm == null) {
      criadoEm = agora;
    }
    atualizadoEm = agora;
  }

  @PreUpdate
  protected void preUpdate() {
    atualizadoEm = LocalDateTime.now();
  }

  public LocalDateTime getCriadoEm() {
    return criadoEm;
  }

  public LocalDateTime getAtualizadoEm() {
    return atualizadoEm;
  }
}
