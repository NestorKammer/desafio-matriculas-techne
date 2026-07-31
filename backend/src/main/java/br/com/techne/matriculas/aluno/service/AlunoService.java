package br.com.techne.matriculas.aluno.service;

import br.com.techne.matriculas.aluno.domain.Aluno;
import br.com.techne.matriculas.aluno.domain.AlunoRepository;
import br.com.techne.matriculas.shared.exception.NegocioException;
import br.com.techne.matriculas.shared.exception.RecursoNaoEncontradoException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlunoService {

  private final AlunoRepository alunoRepository;

  public AlunoService(AlunoRepository alunoRepository) {
    this.alunoRepository = alunoRepository;
  }

  @Transactional(readOnly = true)
  public List<Aluno> listar() {
    return alunoRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Aluno buscar(Long id) {
    return alunoRepository.findById(id)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno", id));
  }

  @Transactional
  public Aluno criar(String nome, String email, String ra) {
    validarUnicidade(email, ra, null);
    Aluno aluno = new Aluno(nome.trim(), email.trim().toLowerCase(), ra.trim());
    return alunoRepository.save(aluno);
  }

  @Transactional
  public Aluno atualizar(Long id, String nome, String email, String ra) {
    Aluno aluno = buscar(id);
    validarUnicidade(email, ra, id);
    aluno.atualizarDados(nome.trim(), email.trim().toLowerCase(), ra.trim());
    return alunoRepository.save(aluno);
  }

  @Transactional
  public void excluir(Long id) {
    Aluno aluno = buscar(id);
    alunoRepository.delete(aluno);
  }

  @Transactional
  public Aluno inativar(Long id) {
    Aluno aluno = buscar(id);
    aluno.inativar();
    return alunoRepository.save(aluno);
  }

  private void validarUnicidade(String email, String ra, Long idAtual) {
    boolean emailDuplicado = idAtual == null
        ? alunoRepository.existsByEmailIgnoreCase(email)
        : alunoRepository.existsByEmailIgnoreCaseAndIdNot(email, idAtual);
    if (emailDuplicado) {
      throw new NegocioException("aluno.email.duplicado", "Ja existe aluno com este email");
    }

    boolean raDuplicado = idAtual == null
        ? alunoRepository.existsByRaIgnoreCase(ra)
        : alunoRepository.existsByRaIgnoreCaseAndIdNot(ra, idAtual);
    if (raDuplicado) {
      throw new NegocioException("aluno.ra.duplicado", "Ja existe aluno com este RA");
    }
  }
}
