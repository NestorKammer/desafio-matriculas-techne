package br.com.techne.matriculas.shared.exception;

public class RecursoNaoEncontradoException extends RuntimeException {

  private final String recurso;
  private final Object identificador;

  public RecursoNaoEncontradoException(String recurso, Object identificador) {
    super(recurso + " nao encontrado: " + identificador);
    this.recurso = recurso;
    this.identificador = identificador;
  }

  public String getRecurso() {
    return recurso;
  }

  public Object getIdentificador() {
    return identificador;
  }
}
