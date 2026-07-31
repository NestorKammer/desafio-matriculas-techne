package br.com.techne.matriculas.aluno.api;

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
class AlunoApiTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private ApiFixture fixture;

  @BeforeEach
  void setUp() {
    fixture = new ApiFixture(mockMvc, objectMapper);
  }

  @Test
  void deveCadastrarEBuscarAluno() throws Exception {
    Long id = fixture.criarAluno("Ana Souza", "ana@teste.com", "RA001");

    mockMvc.perform(get("/api/alunos/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nome").value("Ana Souza"))
        .andExpect(jsonPath("$.email").value("ana@teste.com"))
        .andExpect(jsonPath("$.ra").value("RA001"))
        .andExpect(jsonPath("$.ativo").value(true));
  }

  @Test
  void deveRejeitarEntradaInvalida() throws Exception {
    mockMvc.perform(
            post("/api/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"nome":"","email":"invalido","ra":""}
                    """)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("validacao.entrada"))
        .andExpect(jsonPath("$.campos").isArray());
  }

  @Test
  void deveRejeitarEmailDuplicado() throws Exception {
    fixture.criarAluno("Ana", "dup@teste.com", "RA010");

    mockMvc.perform(
            post("/api/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"nome":"Outra","email":"dup@teste.com","ra":"RA011"}
                    """)
        )
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.codigo").value("aluno.email.duplicado"));
  }

  @Test
  void deveRetornar404QuandoNaoExiste() throws Exception {
    mockMvc.perform(get("/api/alunos/999999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.codigo").value("recurso.nao.encontrado"));
  }
}
