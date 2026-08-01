package br.com.techne.matriculas.matricula.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

  boolean existsByAlunoIdAndTurmaId(Long alunoId, Long turmaId);

  @Query("""
      select m from Matricula m
      join fetch m.aluno
      join fetch m.turma t
      join fetch t.disciplina
      where m.id = :id
      """)
  Optional<Matricula> findByIdWithDetalhes(@Param("id") Long id);

  @Query("""
      select m from Matricula m
      join fetch m.aluno
      join fetch m.turma t
      join fetch t.disciplina
      where m.aluno.id = :alunoId
      order by m.criadoEm desc
      """)
  List<Matricula> findByAlunoIdWithDetalhes(@Param("alunoId") Long alunoId);

  @Query("""
      select m from Matricula m
      join fetch m.aluno
      join fetch m.turma t
      join fetch t.disciplina
      where m.turma.id = :turmaId
      order by m.criadoEm desc
      """)
  List<Matricula> findByTurmaIdWithDetalhes(@Param("turmaId") Long turmaId);

  @Query("""
      select m from Matricula m
      join fetch m.aluno
      join fetch m.turma t
      join fetch t.disciplina
      where (:alunoId is null or m.aluno.id = :alunoId)
        and (:turmaId is null or m.turma.id = :turmaId)
      order by m.criadoEm desc
      """)
  List<Matricula> findByFiltro(
      @Param("alunoId") Long alunoId,
      @Param("turmaId") Long turmaId
  );
}
