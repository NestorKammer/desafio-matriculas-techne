package br.com.techne.matriculas.disciplina.api;

import br.com.techne.matriculas.disciplina.domain.Disciplina;
import br.com.techne.matriculas.disciplina.service.DisciplinaService;
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
@RequestMapping("/api/disciplinas")
@Tag(name = "Disciplinas")
public class DisciplinaController {

  private final DisciplinaService disciplinaService;

  public DisciplinaController(DisciplinaService disciplinaService) {
    this.disciplinaService = disciplinaService;
  }

  @GetMapping
  @Operation(summary = "Listar disciplinas (opcional: filtrar por cursoId)")
  public List<DisciplinaResponse> listar(@RequestParam(required = false) Long cursoId) {
    List<Disciplina> lista = cursoId == null
        ? disciplinaService.listar()
        : disciplinaService.listarPorCurso(cursoId);
    return lista.stream().map(DisciplinaResponse::from).toList();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Buscar disciplina por id")
  public DisciplinaResponse buscar(@PathVariable Long id) {
    return DisciplinaResponse.from(disciplinaService.buscar(id));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Cadastrar disciplina")
  public DisciplinaResponse criar(@Valid @RequestBody DisciplinaRequest request) {
    return DisciplinaResponse.from(
        disciplinaService.criar(
            request.cursoId(),
            request.codigo(),
            request.nome(),
            request.cargaHoraria()
        )
    );
  }

  @PutMapping("/{id}")
  @Operation(summary = "Atualizar disciplina")
  public DisciplinaResponse atualizar(
      @PathVariable Long id,
      @Valid @RequestBody DisciplinaRequest request
  ) {
    return DisciplinaResponse.from(
        disciplinaService.atualizar(
            id,
            request.cursoId(),
            request.codigo(),
            request.nome(),
            request.cargaHoraria()
        )
    );
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Excluir disciplina")
  public void excluir(@PathVariable Long id) {
    disciplinaService.excluir(id);
  }
}
