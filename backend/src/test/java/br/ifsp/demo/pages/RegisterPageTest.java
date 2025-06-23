package br.ifsp.demo.pages;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.RegisterPage;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("UITest")
public class RegisterPageTest {
    private WebDriver driver;
    private RegisterPage registerPage;


    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        registerPage = new RegisterPage(driver);
        registerPage.open();

    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    @Order(1)
    public void naoDeveRegistrarQuandoCamposEstaoVazios() throws InterruptedException {
        assertTrue(registerPage.registerButton().isDisplayed(), "O botão de registro deve estar visível");

        WebElement botao = registerPage.registerButton();
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botao);

        String toastTexto = "";
        try {
            toastTexto = registerPage.successMessage().getText();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            toastTexto = "";
        }

        assertFalse(toastTexto.contains("Administrador registrado com sucesso"),
                "Não deveria aparecer mensagem de sucesso com campos vazios");
    }

    @Test
    @Order(2)
    public void deveMostrarErroQuandoSenhasNaoCoincidem() {
        registerPage.nameField().sendKeys("Joao");
        registerPage.lastnameField().sendKeys("Silva");
        registerPage.emailField().sendKeys("joao@email.com");
        registerPage.passwordField().sendKeys("senha123");
        registerPage.confirmPasswordField().sendKeys("outrasenha");

        assertFalse(registerPage.registrerButtonEnabled());
        assertTrue(registerPage.fieldError("confirmPassword").getText().contains("Senhas não coincidem"));
    }

    @Test
    @Order(3)
    public void deveMostrarErroQuandoEmailInvalido() {
        registerPage.nameField().sendKeys("Joao");
        registerPage.lastnameField().sendKeys("Silva");
        registerPage.emailField().sendKeys("joao@errado");
        registerPage.passwordField().sendKeys("senha123");
        registerPage.confirmPasswordField().sendKeys("senha123");

        assertFalse(registerPage.registrerButtonEnabled());
        assertTrue(registerPage.fieldError("email").getText().contains("Formato de email inválido"));
    }

    @Test
    @Order(4)
    public void deveMostrarErroQuandoSenhaForFraca() {
        registerPage.nameField().sendKeys("Joao");
        registerPage.lastnameField().sendKeys("Silva");
        registerPage.emailField().sendKeys("joao@email.com");
        registerPage.passwordField().sendKeys("abc");
        registerPage.confirmPasswordField().sendKeys("abc");

        assertFalse(registerPage.registrerButtonEnabled());
        assertTrue(registerPage.fieldError("password").getText().contains("Senha deve ter pelo menos 8 caracteres"));
    }

    @Test
    @Order(5)
    public void deveRegistrarComDadosValidos() throws InterruptedException {
        registerPage.nameField().sendKeys("Joao");
        registerPage.lastnameField().sendKeys("Silva");
        registerPage.emailField().sendKeys("joao@email.com");
        registerPage.passwordField().sendKeys("senha123");
        registerPage.confirmPasswordField().sendKeys("senha123");

        assertTrue(registerPage.registrerButtonEnabled());

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", registerPage.registerButton());

        assertTrue(registerPage.successMessage().getText().contains("Administrador registrado com sucesso"));
    }

    @Test
    @Order(6)
    public void deveExibirErroAoRegistrarEmailJaExistente() throws InterruptedException {
        // Primeiro registro válido (caso ainda não exista no sistema)
        registerPage.nameField().sendKeys("Joao");
        registerPage.lastnameField().sendKeys("Silva");
        registerPage.emailField().sendKeys("joao@email.com");
        registerPage.passwordField().sendKeys("senha123");
        registerPage.confirmPasswordField().sendKeys("senha123");

        WebElement botao = driver.findElement(By.xpath("//button[@type='submit']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botao);


        driver.navigate().refresh();
        Thread.sleep(1000);

        registerPage.nameField().sendKeys("Joao");
        registerPage.lastnameField().sendKeys("Silva");
        registerPage.emailField().sendKeys("joao@email.com");
        registerPage.passwordField().sendKeys("senha123");
        registerPage.confirmPasswordField().sendKeys("senha123");

        WebElement botaoRepetido = driver.findElement(By.xpath("//button[@type='submit']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botaoRepetido);


        assertTrue(registerPage.errorMessage().getText().contains("já está em uso") ||
                registerPage.errorMessage().getText().contains("already registered"));
    }

}