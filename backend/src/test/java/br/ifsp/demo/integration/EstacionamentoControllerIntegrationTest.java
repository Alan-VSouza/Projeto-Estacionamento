package br.ifsp.demo.integration;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class EstacionamentoControllerIntegrationTest extends BaseApiIntegrationTest {

    @Test
    public void testCadastrarVeiculoComVaga() {
        String jsonBody = """
            {
                "placa": "ABC-1234",
                "modelo": "Fiat Uno",
                "cor": "Vermelho",
                "vagaId": 1
            }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .post("/estacionamento/veiculos")
                .then()
                .statusCode(201)
                .body("placa", equalTo("ABC-1234"))
                .body("modelo", equalTo("Fiat Uno"))
                .body("cor", equalTo("Vermelho"))
                .body("vagaId", equalTo(1));
    }

    @Test
    public void testListarVeiculos() {
        given()
                .when()
                .get("/estacionamento/veiculos")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }
}
