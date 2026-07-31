package br.com.techne.matriculas.disciplina.service;

import br.com.techne.matriculas.curso.domain.Curso;
import br.com.techne.matriculas.curso.domain.CursoRepository;
import br.com.techne.matriculas.disciplina.domain.Disciplina;
import br.com.techne.matriculas.disciplina.domain.DisciplinaRepository;
import br.com.techne.matriculas.shared.exception.NegocioException;
import br.com.techne.matriculas.shared.exception.RecursoNaoEncontradoException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DisciplinaService {

  private final DisciplinaRepository disciplinaRepository;
  private final CursoRepository cursoRepository;

  public DisciplinaService(DisciplinaRepository disciplinaRepository, CursoRepository cursoRepository) {
    this.disciplinaRepository = disciplinaRepository;
    this.cursoRepository = cursoRepository;
  }

  @Transactional(readOnly = true)
  public List<Disciplina> listar() {
    return disciplinaRepository.findAllWithCurso();
  }

  @Transactional(readOnly = true)
  public List<Disciplina> listarPorCurso(Long cursoId) {
    if (!cursoRepository.existsById(cursoId)) {
      throw new RecursoNaoEncontradoException("Curso", cursoId);
    }
    return disciplinaRepository.findByCursoIdWithCurso(cursoId);
  }

  @Transactional(readOnly = true)
  public Disciplina buscar(Long id) {
    return disciplinaRepository.findByIdWithCurso(id)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Disciplina", id));
  }

  @Transactional
  public Disciplina criar(Long cursoId, String codigo, String nome, Integer cargaHoraria) {
    Curso curso = cursoRepository.findById(cursoId)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Curso", cursoId));
    if (disciplinaRepository.existsByCursoIdAndCodigoIgnoreCase(cursoId, codigo)) {
      throw new NegocioException("disciplina.codigo.duplicado", "Ja existe disciplina com este codigo no curso");
    }
    Disciplina salva = disciplinaRepository.save(
        new Disciplina(curso, codigo.trim(), nome.trim(), cargaHoraria)
    );
    return buscar(salva.getId());
  }

  @Transactional
  public Disciplina atualizar(Long id, Long cursoId, String codigo, String nome, Integer cargaHoraria) {
    Disciplina disciplina = buscar(id);
    Curso curso = cursoRepository.findById(cursoId)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Curso", cursoId));
    if (disciplinaRepository.existsByCursoIdAndCodigoIgnoreCaseAndIdNot(cursoId, codigo, id)) {
      throw new NegocioException("disciplina.codigo.duplicado", "Ja existe disciplina com este codigo no curso");
    }
    disciplina.atualizar(curso, codigo.trim(), nome.trim(), cargaHoraria);
    disciplinaRepository.save(disciplina);
    return buscar(id);
  }

  @Transactional
  public void excluir(Long id) {
    disciplinaRepository.delete(buscar(id));
  }
}
