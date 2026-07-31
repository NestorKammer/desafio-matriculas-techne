package br.com.techne.matriculas.aluno.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

  boolean existsByEmailIgnoreCase(String email);

  boolean existsByRaIgnoreCase(String ra);

  Optional<Aluno> findByRaIgnoreCase(String ra);

  boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

  boolean existsByRaIgnoreCaseAndIdNot(String ra, Long id);
}
