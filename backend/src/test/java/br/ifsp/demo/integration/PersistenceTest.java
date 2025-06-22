package br.ifsp.demo.integration;


import br.ifsp.demo.dto.VeiculoComVagaDTO;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("PersistenceTest")
@Tag("IntegrationTest")
public class PersistenceTest extends BaseApiIntegrationTest {
    private String token;

    @BeforeEach
    public void setupToken() {
        String email = "caio@email.com";
        String password = "senhacaio";

        // Registrar usuário (ignorar se já existir)
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "caio",
                            "lastname": "soares",
                            "email": "%s",
                            "password": "%s"
                        }
                        """.formatted(email, password))
                .when()
                .post("/api/v1/register")
                .then()
                .statusCode(anyOf(is(201), is(409))); // 201 criado ou 409 se o email já existir

        // Autenticar
        this.token = given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "username": "%s",
                            "password": "%s"
                        }
                        """.formatted(email, password))
                .when()
                .post("/api/v1/authenticate")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    @Test
    public void NaoPermitirDuasEntradasComMesmaPlacaAtivaTest() {
        VeiculoComVagaDTO entrada = new VeiculoComVagaDTO(
                "XYZ-1234",
                "CARRO",
                "Modelo X",
                "Preto",
                1
        );

        // Primeira entrada deve funcionar
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(entrada)
                .when()
                .post("/estacionamento/registar-entrada")
                .then()
                .statusCode(200);

        // Segunda entrada com mesma placa deve falhar (servidor sempre retorna 401 pros erros)
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(entrada)
                .when()
                .post("/estacionamento/registar-entrada")
                .then()
                .statusCode(401);
    }

    @Test
    public void ContagemVagasOcupadasTest() {
        // Registra uma entrada para ocupar vaga
        VeiculoComVagaDTO entrada = new VeiculoComVagaDTO(
                "TEST-0001",
                "CARRO",
                "Gol",
                "Prata",
                1
        );

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(entrada)
                .when()
                .post("/estacionamento/registar-entrada")
                .then()
                .statusCode(200);

        // Consulta quantidade de vagas ocupadas (espera ao menos 1)
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/estacionamento/vagas-ocupadas")
                .then()
                .statusCode(200)
                .body(greaterThanOrEqualTo(1));
    }
}
