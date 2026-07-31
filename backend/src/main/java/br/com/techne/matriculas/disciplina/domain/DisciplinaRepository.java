package br.com.techne.matriculas.disciplina.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {

  @Query("select d from Disciplina d join fetch d.curso")
  List<Disciplina> findAllWithCurso();

  @Query("select d from Disciplina d join fetch d.curso where d.id = :id")
  Optional<Disciplina> findByIdWithCurso(@Param("id") Long id);

  @Query("select d from Disciplina d join fetch d.curso where d.curso.id = :cursoId")
  List<Disciplina> findByCursoIdWithCurso(@Param("cursoId") Long cursoId);

  boolean existsByCursoIdAndCodigoIgnoreCase(Long cursoId, String codigo);

  boolean existsByCursoIdAndCodigoIgnoreCaseAndIdNot(Long cursoId, String codigo, Long id);
}
