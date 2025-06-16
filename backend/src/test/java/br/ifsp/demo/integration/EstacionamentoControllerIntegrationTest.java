package br.ifsp.demo.integration;

import br.ifsp.demo.dto.VeiculoComVagaDTO;
import br.ifsp.demo.dto.CriarEstacionamentoDTO;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EstacionamentoControllerIntegrationTest extends BaseApiIntegrationTest {

    @Test
    public void testRegistrarEntrada_comVagaId() {
        VeiculoComVagaDTO dto = new VeiculoComVagaDTO(
                "ABC-1234",
                "CARRO",
                "Fiat Uno",
                "Azul",
                1
        );

        given()
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
        VeiculoComVagaDTO dto = new VeiculoComVagaDTO(
                "XYZ-9876",
                "MOTO",
                "Honda CG",
                "Preta",
                null
        );

        given()
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
        String placa = "ABC-1234";

        given()
                .queryParam("placa", placa)
                .when()
                .post("/estacionamento/cancelar-entrada")
                .then()
                .statusCode(200);
    }

    @Test
    public void testRegistrarSaida() {
        String placa = "ABC-1234";

        given()
                .queryParam("placa", placa)
                .when()
                .post("/estacionamento/registrar-saida")
                .then()
                .statusCode(200)
                .body("placa", equalTo(placa))
                .body("valor", greaterThan(0f));
    }

    @Test
    public void testBuscarEntrada_existente() {
        String placa = "ABC-1234";

        given()
                .queryParam("placa", placa)
                .when()
                .get("/estacionamento/buscar-entrada")
                .then()
                .statusCode(200)
                .body("veiculo.placa", equalTo(placa));
    }

    @Test
    public void testBuscarEntrada_inexistente() {
        String placa = "NAOEXISTE";

        given()
                .queryParam("placa", placa)
                .when()
                .get("/estacionamento/buscar-entrada")
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetAllEntries() {
        given()
                .when()
                .get("/estacionamento/entradas")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    public void testCriarEstacionamento() {
        CriarEstacionamentoDTO dto = new CriarEstacionamentoDTO(
                "Estacionamento Central",
                "Endereço Teste",
                200

        );

        given()
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
                .when()
                .get("/estacionamento/buscar-atual-estacionamento")
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    public void testGetAvailableSpots() {
        given()
                .when()
                .get("/estacionamento/vagas-disponiveis")
                .then()
                .statusCode(200)
                .body(greaterThanOrEqualTo(0));
    }

    @Test
    public void testGetOccupiedSpots() {
        given()
                .when()
                .get("/estacionamento/vagas-ocupadas")
                .then()
                .statusCode(200)
                .body(greaterThanOrEqualTo(0));
    }

    @Test
    public void testGerarRelatorioDesempenho() {
        String data = LocalDate.now().toString();

        given()
                .queryParam("data", data)
                .when()
                .get("/estacionamento/relatorios/desempenho")
                .then()
                .statusCode(200)
                .body("quantidade", greaterThanOrEqualTo(0))
                .body("receitaTotal", greaterThanOrEqualTo(0f));
    }

    @Test
    public void testGetVehicleHistory_found() {
        String placa = "ABC-1234";

        given()
                .when()
                .get("/estacionamento/relatorios/historico/{placa}", placa)
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    public void testGetVehicleHistory_notFound() {
        String placa = "NAOEXISTE";

        given()
                .when()
                .get("/estacionamento/relatorios/historico/{placa}", placa)
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetEstatisticasTempoReal() {
        given()
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
                .when()
                .get("/estacionamento/relatorios/estatisticas/semanal")
                .then()
                .statusCode(200)
                .body("receitaSemanal", greaterThanOrEqualTo(0f))
                .body("mediaDiariaVeiculos", greaterThanOrEqualTo(0f));
    }

    @Test
    public void testExportarRelatorioCSV() {
        String data = LocalDate.now().toString();

        given()
                .queryParam("data", data)
                .when()
                .get("/estacionamento/relatorios/desempenho/export/csv")
                .then()
                .statusCode(200)
                .header("Content-Disposition", containsString("relatorio-" + data + ".csv"))
                .header("Content-Type", containsString("text/csv"));
    }

    @Test
    public void testExportarRelatorioPDF() {
        String data = LocalDate.now().toString();

        given()
                .queryParam("data", data)
                .when()
                .get("/estacionamento/relatorios/desempenho/export/pdf")
                .then()
                .statusCode(200)
                .header("Content-Disposition", containsString("relatorio-" + data + ".pdf"))
                .header("Content-Type", containsString("application/pdf"));
    }

    @Test
    public void testGerarRelatorioMensal() {
        int mes = LocalDate.now().getMonthValue();
        int ano = LocalDate.now().getYear();

        given()
                .queryParam("mes", mes)
                .queryParam("ano", ano)
                .when()
                .get("/estacionamento/relatorios/mensal")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    public void testExportarRelatorioMensalPDF() {
        int mes = LocalDate.now().getMonthValue();
        int ano = LocalDate.now().getYear();

        given()
                .queryParam("mes", mes)
                .queryParam("ano", ano)
                .when()
                .get("/estacionamento/relatorios/mensal/export/pdf")
                .then()
                .statusCode(200)
                .header("Content-Disposition", containsString("relatorio-mensal-" + mes + "-" + ano + ".pdf"))
                .header("Content-Type", containsString("application/pdf"));
    }
}
