package br.com.techne.matriculas.shared.exception;

import br.com.techne.matriculas.shared.api.ErroResposta;
import br.com.techne.matriculas.shared.api.ErroResposta.CampoInvalido;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(RecursoNaoEncontradoException.class)
  public ResponseEntity<ErroResposta> handleNaoEncontrado(
      RecursoNaoEncontradoException ex,
      HttpServletRequest request
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
        ErroResposta.of(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            "recurso.nao.encontrado",
            ex.getMessage(),
            request.getRequestURI(),
            List.of()
        )
    );
  }

  @ExceptionHandler(NegocioException.class)
  public ResponseEntity<ErroResposta> handleNegocio(
      NegocioException ex,
      HttpServletRequest request
  ) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(
        ErroResposta.of(
            HttpStatus.CONFLICT.value(),
            "Conflict",
            ex.getCodigo(),
            ex.getMessage(),
            request.getRequestURI(),
            List.of()
        )
    );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErroResposta> handleValidacao(
      MethodArgumentNotValidException ex,
      HttpServletRequest request
  ) {
    List<CampoInvalido> campos = ex.getBindingResult().getFieldErrors().stream()
        .map(this::toCampo)
        .toList();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        ErroResposta.of(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            "validacao.entrada",
            "Dados de entrada invalidos",
            request.getRequestURI(),
            campos
        )
    );
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErroResposta> handleArgumentoInvalido(
      IllegalArgumentException ex,
      HttpServletRequest request
  ) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        ErroResposta.of(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            "requisicao.invalida",
            ex.getMessage(),
            request.getRequestURI(),
            List.of()
        )
    );
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErroResposta> handleIntegridade(
      DataIntegrityViolationException ex,
      HttpServletRequest request
  ) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(
        ErroResposta.of(
            HttpStatus.CONFLICT.value(),
            "Conflict",
            "integridade.dados",
            "Violacao de integridade de dados (unique/FK)",
            request.getRequestURI(),
            List.of()
        )
    );
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErroResposta> handleGenerico(
      Exception ex,
      HttpServletRequest request
  ) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
        ErroResposta.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "erro.interno",
            "Erro inesperado ao processar a requisicao",
            request.getRequestURI(),
            List.of()
        )
    );
  }

  private CampoInvalido toCampo(FieldError error) {
    return new CampoInvalido(error.getField(), error.getDefaultMessage());
  }
}
