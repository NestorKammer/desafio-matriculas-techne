package br.com.techne.matriculas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI matriculasOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("API de Matriculas Academicas")
            .description("Gestao de alunos, cursos, disciplinas, turmas e matriculas - Desafio Techne")
            .version("v1")
            .contact(new Contact().name("Desafio Techne - Tribe Lyceum")));
  }
}
