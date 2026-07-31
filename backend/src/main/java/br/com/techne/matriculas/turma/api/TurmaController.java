package br.com.techne.matriculas.turma.api;

import br.com.techne.matriculas.turma.domain.Turma;
import br.com.techne.matriculas.turma.service.TurmaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/turmas")
@Tag(name = "Turmas")
public class TurmaController {

  private final TurmaService turmaService;

  public TurmaController(TurmaService turmaService) {
    this.turmaService = turmaService;
  }

  @GetMapping
  @Operation(summary = "Listar turmas (opcional: filtrar por disciplinaId)")
  public List<TurmaResponse> listar(@RequestParam(required = false) Long disciplinaId) {
    List<Turma> lista = disciplinaId == null
        ? turmaService.listar()
        : turmaService.listarPorDisciplina(disciplinaId);
    return lista.stream().map(TurmaResponse::from).toList();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Buscar turma por id")
  public TurmaResponse buscar(@PathVariable Long id) {
    return TurmaResponse.from(turmaService.buscar(id));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Cadastrar turma")
  public TurmaResponse criar(@Valid @RequestBody TurmaRequest request) {
    return TurmaResponse.from(
        turmaService.criar(
            request.disciplinaId(),
            request.codigo(),
            request.periodo(),
            request.vagasTotais()
        )
    );
  }

  @PutMapping("/{id}")
  @Operation(summary = "Atualizar turma")
  public TurmaResponse atualizar(
      @PathVariable Long id,
      @Valid @RequestBody TurmaUpdateRequest request
  ) {
    return TurmaResponse.from(
        turmaService.atualizar(id, request.codigo(), request.periodo(), request.vagasTotais())
    );
  }

  @PostMapping("/{id}/abrir")
  @Operation(summary = "Abrir turma para matriculas")
  public TurmaResponse abrir(@PathVariable Long id) {
    return TurmaResponse.from(turmaService.abrir(id));
  }

  @PostMapping("/{id}/fechar")
  @Operation(summary = "Fechar turma")
  public TurmaResponse fechar(@PathVariable Long id) {
    return TurmaResponse.from(turmaService.fechar(id));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Excluir turma")
  public void excluir(@PathVariable Long id) {
    turmaService.excluir(id);
  }
}
