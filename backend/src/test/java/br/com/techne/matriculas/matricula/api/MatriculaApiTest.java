package br.com.techne.matriculas.matricula.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.techne.matriculas.support.ApiFixture;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MatriculaApiTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private ApiFixture fixture;
  private Long alunoId;
  private Long turmaId;

  @BeforeEach
  void setUp() throws Exception {
    fixture = new ApiFixture(mockMvc, objectMapper);
    Long cursoId = fixture.criarCurso("ENG");
    Long disciplinaId = fixture.criarDisciplina(cursoId, "CAL1");
    turmaId = fixture.criarTurma(disciplinaId, "T1", 1);
    alunoId = fixture.criarAluno("Maria Silva", "maria@teste.com", "RA100");
  }

  @Test
  void deveMatricularComoPendenteSemConsumirVaga() throws Exception {
    Long matriculaId = fixture.matricular(alunoId, turmaId);

    mockMvc.perform(get("/api/matriculas/" + matriculaId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("PENDENTE"));

    mockMvc.perform(get("/api/turmas/" + turmaId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.vagasOcupadas").value(0));
  }

  @Test
  void deveConfirmarEConsumirVaga() throws Exception {
    Long matriculaId = fixture.matricular(alunoId, turmaId);

    mockMvc.perform(post("/api/matriculas/" + matriculaId + "/confirmar"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CONFIRMADA"));

    mockMvc.perform(get("/api/turmas/" + turmaId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.vagasOcupadas").value(1))
        .andExpect(jsonPath("$.vagasDisponiveis").value(0));
  }

  @Test
  void deveCancelarConfirmadaELiberarVaga() throws Exception {
    Long matriculaId = fixture.matricular(alunoId, turmaId);
    mockMvc.perform(post("/api/matriculas/" + matriculaId + "/confirmar"))
        .andExpect(status().isOk());

    mockMvc.perform(post("/api/matriculas/" + matriculaId + "/cancelar"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CANCELADA"));

    mockMvc.perform(get("/api/turmas/" + turmaId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.vagasOcupadas").value(0));
  }

  @Test
  void deveImpedirMatriculaDuplicada() throws Exception {
    fixture.matricular(alunoId, turmaId);

    mockMvc.perform(
            post("/api/matriculas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"alunoId":%d,"turmaId":%d}
                    """.formatted(alunoId, turmaId))
        )
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.codigo").value("matricula.duplicada"));
  }

  @Test
  void deveImpedirConfirmacaoSemVaga() throws Exception {
    Long matricula1 = fixture.matricular(alunoId, turmaId);
    mockMvc.perform(post("/api/matriculas/" + matricula1 + "/confirmar"))
        .andExpect(status().isOk());

    Long aluno2 = fixture.criarAluno("Joao", "joao@teste.com", "RA101");
    Long matricula2 = fixture.matricular(aluno2, turmaId);

    mockMvc.perform(post("/api/matriculas/" + matricula2 + "/confirmar"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.codigo").value("turma.sem.vaga"));
  }

  @Test
  void deveImpedirMatriculaEmTurmaFechada() throws Exception {
    mockMvc.perform(post("/api/turmas/" + turmaId + "/fechar"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("FECHADA"));

    mockMvc.perform(
            post("/api/matriculas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"alunoId":%d,"turmaId":%d}
                    """.formatted(alunoId, turmaId))
        )
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.codigo").value("turma.fechada"));
  }

  @Test
  void deveConsultarPorAlunoEPorTurma() throws Exception {
    Long matriculaId = fixture.matricular(alunoId, turmaId);

    mockMvc.perform(get("/api/matriculas").param("alunoId", alunoId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(matriculaId));

    mockMvc.perform(get("/api/matriculas").param("turmaId", turmaId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(matriculaId));
  }

  @Test
  void deveConsultarComFiltrosCombinadosComE() throws Exception {
    Long turma2 = fixture.criarTurma(
        fixture.criarDisciplina(fixture.criarCurso("ADS"), "ALG1"),
        "T2",
        5
    );
    Long aluno2 = fixture.criarAluno("Joao Souza", "joao@teste.com", "RA200");
    Long matriculaAlvo = fixture.matricular(alunoId, turmaId);
    fixture.matricular(alunoId, turma2);
    fixture.matricular(aluno2, turmaId);

    mockMvc.perform(get("/api/matriculas")
            .param("alunoId", alunoId.toString())
            .param("turmaId", turmaId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(matriculaAlvo));
  }
}
