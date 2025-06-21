package br.ifsp.demo.integration;

import br.ifsp.demo.dto.CriarEstacionamentoDTO;
import br.ifsp.demo.dto.VeiculoComVagaDTO;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EstacionamentoControllerIntegrationTest extends BaseApiIntegrationTest {

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
    public void testRegistrarEntrada_comVagaId() {
        VeiculoComVagaDTO dto = new VeiculoComVagaDTO("ABC-1234", "CARRO", "Fiat Uno", "Azul", 1);

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/estacionamento/registar-entrada")
                .then()
                .statusCode(200)
                .body("veiculo.placa", equalTo("ABC-1234"))
                .body("vagaId", equalTo(1));
    }

    @Test
    public void testRegistrarEntrada_semVagaId_usaProximaDisponivel() {
        VeiculoComVagaDTO dto = new VeiculoComVagaDTO("XYZ-9876", "MOTO", "Honda CG", "Preta", null);

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/estacionamento/registar-entrada")
                .then()
                .statusCode(200)
                .body("veiculo.placa", equalTo("XYZ-9876"))
                .body("vagaId", notNullValue());
    }

    @Test
    public void testCancelarEntrada() {
        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("placa", "ABC-1234")
                .when()
                .post("/estacionamento/cancelar-entrada")
                .then()
                .statusCode(200);
    }

    @Test
    public void testRegistrarSaida() {
        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("placa", "ABC-1234")
                .when()
                .post("/estacionamento/registrar-saida")
                .then()
                .statusCode(200)
                .body("placa", equalTo("ABC-1234"))
                .body("valor", greaterThan(0f));
    }

    @Test
    public void testBuscarEntrada_existente() {
        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("placa", "ABC-1234")
                .when()
                .get("/estacionamento/buscar-entrada")
                .then()
                .statusCode(200)
                .body("veiculo.placa", equalTo("ABC-1234"));
    }

    @Test
    public void testBuscarEntrada_inexistente() {
        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("placa", "NAOEXISTE")
                .when()
                .get("/estacionamento/buscar-entrada")
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetAllEntries() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/estacionamento/entradas")
                .then()
                .statusCode(200);
    }

    @Test
    public void testCriarEstacionamento() {
        CriarEstacionamentoDTO dto = new CriarEstacionamentoDTO("Estacionamento Central", "Endereço Teste", 200);

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/estacionamento/criar-estacionamento")
                .then()
                .statusCode(201)
                .body("nome", equalTo("Estacionamento Central"))
                .body("totalVagas", equalTo(200));
    }

    @Test
    public void testBuscarEstacionamentoAtual() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/estacionamento/buscar-atual-estacionamento")
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    public void testGetAvailableSpots() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/estacionamento/vagas-disponiveis")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetOccupiedSpots() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/estacionamento/vagas-ocupadas")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGerarRelatorioDesempenho() {
        String data = LocalDate.now().toString();

        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("data", data)
                .when()
                .get("/estacionamento/relatorios/desempenho")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetVehicleHistory_found() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/estacionamento/relatorios/historico/ABC-1234")
                .then()
                .statusCode(anyOf(is(200), is(404)));
    }

    @Test
    public void testGetVehicleHistory_notFound() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/estacionamento/relatorios/historico/NAOEXISTE")
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetEstatisticasTempoReal() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/estacionamento/relatorios/estatisticas")
                .then()
                .statusCode(200)
                .body("vagasDisponiveis", greaterThanOrEqualTo(0))
                .body("totalVagas", equalTo(200));
    }

    @Test
    public void testGetEstatisticasSemanais() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/estacionamento/relatorios/estatisticas/semanal")
                .then()
                .statusCode(200);
    }

    @Test
    public void testExportarRelatorioCSV() {
        String data = LocalDate.now().toString();

        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("data", data)
                .when()
                .get("/estacionamento/relatorios/desempenho/export/csv")
                .then()
                .statusCode(200);
    }

    @Test
    public void testExportarRelatorioPDF() {
        String data = LocalDate.now().toString();

        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("data", data)
                .when()
                .get("/estacionamento/relatorios/desempenho/export/pdf")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGerarRelatorioMensal() {
        int mes = LocalDate.now().getMonthValue();
        int ano = LocalDate.now().getYear();

        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("mes", mes)
                .queryParam("ano", ano)
                .when()
                .get("/estacionamento/relatorios/mensal")
                .then()
                .statusCode(200);
    }

    @Test
    public void testExportarRelatorioMensalPDF() {
        int mes = LocalDate.now().getMonthValue();
        int ano = LocalDate.now().getYear();

        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("mes", mes)
                .queryParam("ano", ano)
                .when()
                .get("/estacionamento/relatorios/mensal/export/pdf")
                .then()
                .statusCode(200);
    }
}
