package br.ifsp.demo.pages;

import br.ifsp.demo.pages.ReportsPage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("UiTest")
public class ReportsPageTest {
    private WebDriver driver;
    private ReportsPage reportsPage;

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

        reportsPage = new ReportsPage(driver);
        reportsPage.abrirPagina();
    }

    @Test
    public void testTabRelatorioMensal() {
        reportsPage.clicarTabRelatorioMensal();


        assertTrue(reportsPage.estaMostrandoRelatorioMensal());
    }

    @Test
    public void testTabReceitaDiaria() {
        reportsPage.clicarTabReceitaDiaria();


        assertTrue(reportsPage.estaMostrandoReceitaDiaria());
    }

    @Test
    public void testExportarPDF() {
        reportsPage.clicarTabReceitaDiaria();

        // Força o clique com JS
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", reportsPage.botaoExportarPDF());

        // Espera o toast de sucesso aparecer
        WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".Toastify__toast--success")
        ));

        assertTrue(toast.getText().contains("PDF exportado com sucesso"));
    }

    @Test
    public void testExportarCSV() {
        reportsPage.clicarTabReceitaDiaria();

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", reportsPage.botaoExportarCSV());

        WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".Toastify__toast--success")
        ));

        assertTrue(toast.getText().contains("CSV exportado com sucesso"));
    }

   @Test
    public void testVirarCardReceita() {
        reportsPage.clicarTabReceitaDiaria();


        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'flip-card')]//h3[contains(text(),'Receita Total')]/ancestor::div[contains(@class,'flip-card')]")
        ));


        Assertions.assertFalse(reportsPage.cardReceitaEstaFlipped());


        reportsPage.clicarCardReceita();
        Assertions.assertTrue(reportsPage.cardReceitaEstaFlipped());


        reportsPage.clicarCardReceita();
        Assertions.assertFalse(reportsPage.cardReceitaEstaFlipped());
    }


    @Test
    public void testVirarCardVehicles() {
        reportsPage.clicarTabReceitaDiaria();
        Assertions.assertFalse(reportsPage.cardVehiclesEstaFlipped());
        reportsPage.clicarCardVehicles();
        Assertions.assertTrue(reportsPage.cardVehiclesEstaFlipped());
        reportsPage.clicarCardVehicles();
        Assertions.assertFalse(reportsPage.cardVehiclesEstaFlipped());
    }

    @Test
    public void testVirarCardAvgTime() {
        reportsPage.clicarTabReceitaDiaria();
        Assertions.assertFalse(reportsPage.cardAvgTimeEstaFlipped());
        reportsPage.clicarCardAvgTime();
        Assertions.assertTrue(reportsPage.cardAvgTimeEstaFlipped());
        reportsPage.clicarCardAvgTime();
        Assertions.assertFalse(reportsPage.cardAvgTimeEstaFlipped());
    }

    @Test
    public void testVirarCardOccupancy() {
        reportsPage.clicarTabReceitaDiaria();
        Assertions.assertFalse(reportsPage.cardOccupancyEstaFlipped());
        reportsPage.clicarCardOccupancy();
        Assertions.assertTrue(reportsPage.cardOccupancyEstaFlipped());
        reportsPage.clicarCardOccupancy();
        Assertions.assertFalse(reportsPage.cardOccupancyEstaFlipped());
    }

    @Test
    public void testVirarCardMensalReceita() {
        reportsPage.clicarTabRelatorioMensal();

        Assertions.assertFalse(reportsPage.cardMensalReceitaEstaFlipped());

        reportsPage.clicarCardMensalReceita();
        Assertions.assertTrue(reportsPage.cardMensalReceitaEstaFlipped());

        reportsPage.clicarCardMensalReceita();
        Assertions.assertFalse(reportsPage.cardMensalReceitaEstaFlipped());
    }

    @Test
    public void testVirarCardMensalVeiculos() {
        reportsPage.clicarTabRelatorioMensal();

        Assertions.assertFalse(reportsPage.cardMensalVeiculosEstaFlipped());

        reportsPage.clicarCardMensalVeiculos();
        Assertions.assertTrue(reportsPage.cardMensalVeiculosEstaFlipped());

        reportsPage.clicarCardMensalVeiculos();
        Assertions.assertFalse(reportsPage.cardMensalVeiculosEstaFlipped());
    }

    @Test
    public void testVirarCardMensalTempoMedio() {
        reportsPage.clicarTabRelatorioMensal();

        Assertions.assertFalse(reportsPage.cardMensalAvgTimeEstaFlipped());

        reportsPage.clicarCardMensalAvgTime();
        Assertions.assertTrue(reportsPage.cardMensalAvgTimeEstaFlipped());

        reportsPage.clicarCardMensalAvgTime();
        Assertions.assertFalse(reportsPage.cardAvgTimeEstaFlipped());
    }

    @Test
    public void testVirarCardMensalMelhorDia() {
        reportsPage.clicarTabRelatorioMensal();

        Assertions.assertFalse(reportsPage.cardMensalMelhorDiaEstaFlipped());

        reportsPage.clicarCardMensalMelhorDia();
        Assertions.assertTrue(reportsPage.cardMensalMelhorDiaEstaFlipped());

        reportsPage.clicarCardMensalMelhorDia();
        Assertions.assertFalse(reportsPage.cardMensalMelhorDiaEstaFlipped());
    }








    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}

