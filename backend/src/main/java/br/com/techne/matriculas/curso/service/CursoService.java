package br.com.techne.matriculas.curso.service;

import br.com.techne.matriculas.curso.domain.Curso;
import br.com.techne.matriculas.curso.domain.CursoRepository;
import br.com.techne.matriculas.shared.exception.NegocioException;
import br.com.techne.matriculas.shared.exception.RecursoNaoEncontradoException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CursoService {

  private final CursoRepository cursoRepository;

  public CursoService(CursoRepository cursoRepository) {
    this.cursoRepository = cursoRepository;
  }

  @Transactional(readOnly = true)
  public List<Curso> listar() {
    return cursoRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Curso buscar(Long id) {
    return cursoRepository.findById(id)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Curso", id));
  }

  @Transactional
  public Curso criar(String codigo, String nome, Integer cargaHoraria) {
    if (cursoRepository.existsByCodigoIgnoreCase(codigo)) {
      throw new NegocioException("curso.codigo.duplicado", "Ja existe curso com este codigo");
    }
    return cursoRepository.save(new Curso(codigo.trim(), nome.trim(), cargaHoraria));
  }

  @Transactional
  public Curso atualizar(Long id, String codigo, String nome, Integer cargaHoraria) {
    Curso curso = buscar(id);
    if (cursoRepository.existsByCodigoIgnoreCaseAndIdNot(codigo, id)) {
      throw new NegocioException("curso.codigo.duplicado", "Ja existe curso com este codigo");
    }
    curso.atualizar(codigo.trim(), nome.trim(), cargaHoraria);
    return cursoRepository.save(curso);
  }

  @Transactional
  public void excluir(Long id) {
    cursoRepository.delete(buscar(id));
  }
}
