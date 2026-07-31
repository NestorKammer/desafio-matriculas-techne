package br.com.techne.matriculas.turma.service;

import br.com.techne.matriculas.disciplina.domain.Disciplina;
import br.com.techne.matriculas.disciplina.domain.DisciplinaRepository;
import br.com.techne.matriculas.shared.exception.RecursoNaoEncontradoException;
import br.com.techne.matriculas.turma.domain.Turma;
import br.com.techne.matriculas.turma.domain.TurmaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TurmaService {

  private final TurmaRepository turmaRepository;
  private final DisciplinaRepository disciplinaRepository;

  public TurmaService(TurmaRepository turmaRepository, DisciplinaRepository disciplinaRepository) {
    this.turmaRepository = turmaRepository;
    this.disciplinaRepository = disciplinaRepository;
  }

  @Transactional(readOnly = true)
  public List<Turma> listar() {
    return turmaRepository.findAllWithDisciplina();
  }

  @Transactional(readOnly = true)
  public List<Turma> listarPorDisciplina(Long disciplinaId) {
    if (!disciplinaRepository.existsById(disciplinaId)) {
      throw new RecursoNaoEncontradoException("Disciplina", disciplinaId);
    }
    return turmaRepository.findByDisciplinaIdWithDisciplina(disciplinaId);
  }

  @Transactional(readOnly = true)
  public Turma buscar(Long id) {
    return turmaRepository.findByIdWithDisciplina(id)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Turma", id));
  }

  @Transactional
  public Turma criar(Long disciplinaId, String codigo, String periodo, Integer vagasTotais) {
    Disciplina disciplina = disciplinaRepository.findByIdWithCurso(disciplinaId)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Disciplina", disciplinaId));
    Turma salva = turmaRepository.save(new Turma(disciplina, codigo.trim(), periodo.trim(), vagasTotais));
    return buscar(salva.getId());
  }

  @Transactional
  public Turma atualizar(Long id, String codigo, String periodo, Integer vagasTotais) {
    Turma turma = buscar(id);
    turma.atualizar(codigo.trim(), periodo.trim(), vagasTotais);
    turmaRepository.save(turma);
    return buscar(id);
  }

  @Transactional
  public Turma abrir(Long id) {
    Turma turma = buscar(id);
    turma.abrir();
    turmaRepository.save(turma);
    return buscar(id);
  }

  @Transactional
  public Turma fechar(Long id) {
    Turma turma = buscar(id);
    turma.fechar();
    turmaRepository.save(turma);
    return buscar(id);
  }

  @Transactional
  public void excluir(Long id) {
    turmaRepository.delete(buscar(id));
  }
}
