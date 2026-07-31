package br.com.techne.matriculas.matricula.service;

import br.com.techne.matriculas.aluno.domain.Aluno;
import br.com.techne.matriculas.aluno.domain.AlunoRepository;
import br.com.techne.matriculas.matricula.domain.Matricula;
import br.com.techne.matriculas.matricula.domain.MatriculaRepository;
import br.com.techne.matriculas.shared.exception.NegocioException;
import br.com.techne.matriculas.shared.exception.RecursoNaoEncontradoException;
import br.com.techne.matriculas.turma.domain.Turma;
import br.com.techne.matriculas.turma.domain.TurmaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MatriculaService {

  private final MatriculaRepository matriculaRepository;
  private final AlunoRepository alunoRepository;
  private final TurmaRepository turmaRepository;

  public MatriculaService(
      MatriculaRepository matriculaRepository,
      AlunoRepository alunoRepository,
      TurmaRepository turmaRepository
  ) {
    this.matriculaRepository = matriculaRepository;
    this.alunoRepository = alunoRepository;
    this.turmaRepository = turmaRepository;
  }

  @Transactional
  public Matricula matricular(Long alunoId, Long turmaId) {
    Aluno aluno = alunoRepository.findById(alunoId)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno", alunoId));
    Turma turma = turmaRepository.findByIdWithDisciplina(turmaId)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Turma", turmaId));

    if (matriculaRepository.existsByAlunoIdAndTurmaId(alunoId, turmaId)) {
      throw new NegocioException(
          "matricula.duplicada",
          "Aluno ja possui matricula nesta turma"
      );
    }

    Matricula matricula = Matricula.criarPendente(aluno, turma);
    Matricula salva = matriculaRepository.save(matricula);
    return buscar(salva.getId());
  }

  @Transactional
  public Matricula confirmar(Long matriculaId) {
    Matricula referencia = buscar(matriculaId);
    Long turmaId = referencia.getTurma().getId();

    // Serializa confirmacoes da mesma turma (protege limite de vagas)
    turmaRepository.findByIdForUpdate(turmaId)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Turma", turmaId));

    // Rele apos o lock para evitar double-confirm concorrente
    Matricula matricula = buscar(matriculaId);
    matricula.confirmar();
    matriculaRepository.save(matricula);
    return buscar(matriculaId);
  }

  @Transactional
  public Matricula cancelar(Long matriculaId) {
    Matricula referencia = buscar(matriculaId);
    Long turmaId = referencia.getTurma().getId();

    turmaRepository.findByIdForUpdate(turmaId)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Turma", turmaId));

    Matricula matricula = buscar(matriculaId);
    matricula.cancelar();
    matriculaRepository.save(matricula);
    return buscar(matriculaId);
  }

  @Transactional(readOnly = true)
  public Matricula buscar(Long id) {
    return matriculaRepository.findByIdWithDetalhes(id)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Matricula", id));
  }

  @Transactional(readOnly = true)
  public List<Matricula> listarPorAluno(Long alunoId) {
    if (!alunoRepository.existsById(alunoId)) {
      throw new RecursoNaoEncontradoException("Aluno", alunoId);
    }
    return matriculaRepository.findByAlunoIdWithDetalhes(alunoId);
  }

  @Transactional(readOnly = true)
  public List<Matricula> listarPorTurma(Long turmaId) {
    if (!turmaRepository.existsById(turmaId)) {
      throw new RecursoNaoEncontradoException("Turma", turmaId);
    }
    return matriculaRepository.findByTurmaIdWithDetalhes(turmaId);
  }
}
