package br.com.techne.matriculas.aluno.api;

import br.com.techne.matriculas.aluno.service.AlunoService;
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
@RequestMapping("/api/alunos")
@Tag(name = "Alunos")
public class AlunoController {

  private final AlunoService alunoService;

  public AlunoController(AlunoService alunoService) {
    this.alunoService = alunoService;
  }

  @GetMapping
  @Operation(summary = "Listar alunos")
  public List<AlunoResponse> listar() {
    return alunoService.listar().stream().map(AlunoResponse::from).toList();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Buscar aluno por id")
  public AlunoResponse buscar(@PathVariable Long id) {
    return AlunoResponse.from(alunoService.buscar(id));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Cadastrar aluno")
  public AlunoResponse criar(@Valid @RequestBody AlunoRequest request) {
    return AlunoResponse.from(alunoService.criar(request.nome(), request.email(), request.ra()));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Atualizar aluno")
  public AlunoResponse atualizar(@PathVariable Long id, @Valid @RequestBody AlunoRequest request) {
    return AlunoResponse.from(
        alunoService.atualizar(id, request.nome(), request.email(), request.ra())
    );
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Excluir aluno")
  public void excluir(@PathVariable Long id) {
    alunoService.excluir(id);
  }

  @PostMapping("/{id}/inativar")
  @Operation(summary = "Inativar aluno")
  public AlunoResponse inativar(@PathVariable Long id) {
    return AlunoResponse.from(alunoService.inativar(id));
  }
}
