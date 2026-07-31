package br.com.techne.matriculas.matricula.api;

import br.com.techne.matriculas.matricula.service.MatriculaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matriculas")
@Tag(name = "Matriculas")
public class MatriculaController {

  private final MatriculaService matriculaService;

  public MatriculaController(MatriculaService matriculaService) {
    this.matriculaService = matriculaService;
  }

  @GetMapping
  @Operation(summary = "Consultar matriculas por alunoId ou turmaId")
  public List<MatriculaResponse> consultar(
      @RequestParam(required = false) Long alunoId,
      @RequestParam(required = false) Long turmaId
  ) {
    if (alunoId != null) {
      return matriculaService.listarPorAluno(alunoId).stream().map(MatriculaResponse::from).toList();
    }
    if (turmaId != null) {
      return matriculaService.listarPorTurma(turmaId).stream().map(MatriculaResponse::from).toList();
    }
    throw new IllegalArgumentException("Informe alunoId ou turmaId para consultar matriculas");
  }

  @GetMapping("/{id}")
  @Operation(summary = "Buscar matricula por id")
  public MatriculaResponse buscar(@PathVariable Long id) {
    return MatriculaResponse.from(matriculaService.buscar(id));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Matricular aluno em turma (status PENDENTE)")
  public MatriculaResponse matricular(@Valid @RequestBody MatriculaRequest request) {
    return MatriculaResponse.from(
        matriculaService.matricular(request.alunoId(), request.turmaId())
    );
  }

  @PostMapping("/{id}/confirmar")
  @Operation(summary = "Confirmar matricula e consumir vaga")
  public MatriculaResponse confirmar(@PathVariable Long id) {
    return MatriculaResponse.from(matriculaService.confirmar(id));
  }

  @PostMapping("/{id}/cancelar")
  @Operation(summary = "Cancelar matricula (libera vaga se CONFIRMADA)")
  public MatriculaResponse cancelar(@PathVariable Long id) {
    return MatriculaResponse.from(matriculaService.cancelar(id));
  }
}
