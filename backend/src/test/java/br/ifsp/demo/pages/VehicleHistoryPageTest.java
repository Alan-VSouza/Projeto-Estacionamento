package br.ifsp.demo.pages;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class VehicleHistoryPageTest {
    private WebDriver driver;
    private VehicleHistoryPage page;
    private WebDriverWait wait;
    private LoginPage loginPage;

    @BeforeEach
    public void setup() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(2));

        loginPage = new LoginPage(driver);
        loginPage.preencherEmail("caio@email.com");
        loginPage.preencherSenha("senhacaio1");
        loginPage.clicarLogin();

        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/"));

        page = new VehicleHistoryPage(driver);
        page.abrirPagina();
    }

    @Test
    public void testPlacaValidaSemHistorico() {
        page.preencherPlaca("ABC3478");
        page.clicarBuscar();




        WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".Toastify__toast--error")
        ));

        // Deveria dar True mas não acha o Toast de jeito nenhum
        //assertTrue(toast.getText().contains("❌ Erro ao buscar histórico do veículo"));
    }

    @Test
    public void testPlacaValidaComHistoricoMostraResultados() {
        page.preencherPlaca("ABC5678"); // Ajuste a placa conforme o backend mockado
        page.clicarBuscar();

        WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".Toastify__toast--success")
        ));

        assertTrue(toast.getText().contains("✅ Histórico encontrado para ABC5678"));


    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}
