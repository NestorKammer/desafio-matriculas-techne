package br.com.techne.matriculas.curso.api;

import br.com.techne.matriculas.curso.service.CursoService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cursos")
@Tag(name = "Cursos")
public class CursoController {

  private final CursoService cursoService;

  public CursoController(CursoService cursoService) {
    this.cursoService = cursoService;
  }

  @GetMapping
  @Operation(summary = "Listar cursos")
  public List<CursoResponse> listar() {
    return cursoService.listar().stream().map(CursoResponse::from).toList();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Buscar curso por id")
  public CursoResponse buscar(@PathVariable Long id) {
    return CursoResponse.from(cursoService.buscar(id));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Cadastrar curso")
  public CursoResponse criar(@Valid @RequestBody CursoRequest request) {
    return CursoResponse.from(
        cursoService.criar(request.codigo(), request.nome(), request.cargaHoraria())
    );
  }

  @PutMapping("/{id}")
  @Operation(summary = "Atualizar curso")
  public CursoResponse atualizar(@PathVariable Long id, @Valid @RequestBody CursoRequest request) {
    return CursoResponse.from(
        cursoService.atualizar(id, request.codigo(), request.nome(), request.cargaHoraria())
    );
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Excluir curso")
  public void excluir(@PathVariable Long id) {
    cursoService.excluir(id);
  }
}
