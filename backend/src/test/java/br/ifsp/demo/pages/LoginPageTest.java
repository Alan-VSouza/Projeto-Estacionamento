package br.ifsp.demo.pages;

import com.github.javafaker.Faker;
import br.ifsp.demo.pages.LoginPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginPageTest {

    private WebDriver driver;
    private Faker faker;

    @BeforeAll
    void setupAll() {
        WebDriverManager.chromedriver().setup();  // baixa e configura driver automaticamente
        faker = new Faker();
    }

    @BeforeEach
    void setup() {
        driver = new ChromeDriver();  // instancia o driver
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();  // fecha o browser e limpa recursos
        }
    }


    @Test
    @Tag("UiTest")
    void deveMostrarErroComEmailInvalido() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.preencherEmail("invalido-email");
        loginPage.preencherSenha("senhaSegura123");

        // Verifica se o botão está desabilitado
        assertFalse(loginPage.botaoLoginHabilitado());

        // Verifica se a mensagem de erro apareceu
        assertTrue(loginPage.erroPresente());
        assertTrue(loginPage.obterMensagemErro().contains("inválido"));
    }


    @Test
    @Tag("UiTest")
    void deveMostrarErroComSenhaCurta() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.preencherEmail("teste@email.com");
        loginPage.preencherSenha("123");

        // Verifica se o botão está desabilitado
        assertFalse(loginPage.botaoLoginHabilitado());

        // Verifica a mensagem de erro
        assertTrue(loginPage.erroPresente());
        assertTrue(loginPage.obterMensagemErro().contains("pelo menos 8 caracteres"));
    }


    @Test
    @Tag("UiTest")
    void devePermitirLoginComCredenciaisValidas() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.preencherEmail("admin@email.com");
        loginPage.preencherSenha("senhaAdmin123");
        loginPage.clicarLogin();
    }

}

