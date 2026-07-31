package br.com.techne.matriculas.turma.domain;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TurmaRepository extends JpaRepository<Turma, Long> {

  @Query("select t from Turma t join fetch t.disciplina d join fetch d.curso")
  List<Turma> findAllWithDisciplina();

  @Query("select t from Turma t join fetch t.disciplina d join fetch d.curso where t.id = :id")
  Optional<Turma> findByIdWithDisciplina(@Param("id") Long id);

  @Query("select t from Turma t join fetch t.disciplina d join fetch d.curso where t.disciplina.id = :disciplinaId")
  List<Turma> findByDisciplinaIdWithDisciplina(@Param("disciplinaId") Long disciplinaId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select t from Turma t where t.id = :id")
  Optional<Turma> findByIdForUpdate(@Param("id") Long id);
}
