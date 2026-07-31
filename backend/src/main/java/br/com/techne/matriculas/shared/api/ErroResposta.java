package br.com.techne.matriculas.shared.api;

import java.time.LocalDateTime;
import java.util.List;

public record ErroResposta(
    LocalDateTime timestamp,
    int status,
    String erro,
    String codigo,
    String mensagem,
    String path,
    List<CampoInvalido> campos
) {

  public record CampoInvalido(String campo, String mensagem) {
  }

  public static ErroResposta of(
      int status,
      String erro,
      String codigo,
      String mensagem,
      String path,
      List<CampoInvalido> campos
  ) {
    return new ErroResposta(
        LocalDateTime.now(),
        status,
        erro,
        codigo,
        mensagem,
        path,
        campos == null ? List.of() : campos
    );
  }
}
