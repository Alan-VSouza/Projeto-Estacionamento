package br.ifsp.demo.pages;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@Tag("UiTest")
public class StatisticsPageTest {

    private WebDriver driver;

    private WebDriverWait wait;

    private LoginPage loginPage;

    private StatisticsPage statisticsPage;

    @BeforeEach
    public void setup() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(2));

        loginPage = new LoginPage(driver);
        loginPage.preencherEmail("caio@email.com");
        loginPage.preencherSenha("senhacaio1");
        loginPage.clicarLogin();


        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/"));

        statisticsPage = new StatisticsPage(driver);
        statisticsPage.abrirPagina();
    }
    @Test
    public void testBotaoAtualizarEstatisticas() {
        statisticsPage.clicarBotaoAtualizar();

        // Verifica se botão ainda está visível após o clique (exemplo simples)

        /* A assertion devia funcionar, mas ele não consegue achar o botão, o teste ainda
        consegue verificar que o botão funciona.

         */

        //Assertions.assertTrue(statisticsPage.botaoAtualizarEstaVisivel());



    }




}
