package br.com.techne.matriculas.support;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public final class ApiFixture {

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;

  public ApiFixture(MockMvc mockMvc, ObjectMapper objectMapper) {
    this.mockMvc = mockMvc;
    this.objectMapper = objectMapper;
  }

  public Long criarAluno(String nome, String email, String ra) throws Exception {
    String body = """
        {"nome":"%s","email":"%s","ra":"%s"}
        """.formatted(nome, email, ra);
    return postAndId("/api/alunos", body);
  }

  public Long criarCurso(String codigo) throws Exception {
    String body = """
        {"codigo":"%s","nome":"Curso %s","cargaHoraria":3600}
        """.formatted(codigo, codigo);
    return postAndId("/api/cursos", body);
  }

  public Long criarDisciplina(Long cursoId, String codigo) throws Exception {
    String body = """
        {"cursoId":%d,"codigo":"%s","nome":"Disciplina %s","cargaHoraria":80}
        """.formatted(cursoId, codigo, codigo);
    return postAndId("/api/disciplinas", body);
  }

  public Long criarTurma(Long disciplinaId, String codigo, int vagas) throws Exception {
    String body = """
        {"disciplinaId":%d,"codigo":"%s","periodo":"2026.1","vagasTotais":%d}
        """.formatted(disciplinaId, codigo, vagas);
    return postAndId("/api/turmas", body);
  }

  public Long matricular(Long alunoId, Long turmaId) throws Exception {
    String body = """
        {"alunoId":%d,"turmaId":%d}
        """.formatted(alunoId, turmaId);
    return postAndId("/api/matriculas", body);
  }

  public JsonNode getJson(MvcResult result) throws Exception {
    return objectMapper.readTree(result.getResponse().getContentAsString());
  }

  private Long postAndId(String path, String body) throws Exception {
    MvcResult result = mockMvc.perform(
            post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
        .andExpect(status().isCreated())
        .andReturn();
    return getJson(result).get("id").asLong();
  }
}
