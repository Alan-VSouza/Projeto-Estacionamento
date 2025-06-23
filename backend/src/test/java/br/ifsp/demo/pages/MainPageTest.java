package br.ifsp.demo.pages;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("UITeste")
public class MainPageTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private MainPage mainPage;
    private VehicleEntryFormPage formPage;
    private Faker faker;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        faker = new Faker();

        // Login
        LoginPage loginPage = new LoginPage(driver);
        loginPage.preencherEmail("caio@gmail.com");
        loginPage.preencherSenha("bleble@12345");
        loginPage.clicarLogin();

        wait.until(webDriver -> driver.getCurrentUrl().equals("http://localhost:3000/"));

        mainPage = new MainPage(driver);
        formPage = new VehicleEntryFormPage(driver);
    }

    @Test
    @Order(1)
    public void deveExibirErroParaPlacaInvalidaFormato() {
        mainPage.clicarPrimeiraVagaDisponivel();

        formPage.preencherPlaca("1234567");
        formPage.preencherTipo("CARRO");
        formPage.preencherModelo("Civic");
        formPage.preencherCor("Preto");


        WebElement erro = driver.findElement(By.xpath("//*[contains(text(),'Formato de placa inválido')]"));
        assertTrue(erro.isDisplayed());
    }

    @Test
    @Order(1)
    public void deveExibirErroParaPlacaInvalidaMenos7Caracteres() {
        mainPage.clicarPrimeiraVagaDisponivel();

        formPage.preencherPlaca("ABC123");
        formPage.preencherTipo("CARRO");
        formPage.preencherModelo("Civic");
        formPage.preencherCor("Preto");


        WebElement erro = driver.findElement(By.xpath("//*[contains(text(),'Placa deve ter exatamente 7 caracteres')]"));
        assertTrue(erro.isDisplayed());
    }

    @Test
    @Order(2)
    public void deveExibirErroParaModeloInvalidoNumeros() {
        mainPage.clicarPrimeiraVagaDisponivel();

        formPage.preencherPlaca("ABC1234");
        formPage.preencherTipo("CARRO");
        formPage.preencherModelo("12345");
        formPage.preencherCor("Preto");

        WebElement erro = driver.findElement(By.xpath("//*[contains(text(),'Modelo não pode ser apenas números')]"));
        assertTrue(erro.isDisplayed());

    }
    @Test
    @Order(3)
    public void deveExibirErroParaModeloInvalidoMenos2Caracteres() {
        mainPage.clicarPrimeiraVagaDisponivel();

        formPage.preencherPlaca("ABC1234");
        formPage.preencherTipo("CARRO");
        formPage.preencherModelo("C");
        formPage.preencherCor("Preto");

        WebElement erro = driver.findElement(By.xpath("//*[contains(text(),'Modelo deve ter pelo menos 2 caracteres')]"));
        assertTrue(erro.isDisplayed());

    }


    @Test
    @Order(4)
    public void deveExibirErroParaCorInvalidaCaracteresEspeciais() {
        mainPage.clicarPrimeiraVagaDisponivel();

        formPage.preencherPlaca("ABC1234");
        formPage.preencherTipo("CARRO");
        formPage.preencherModelo("Civic");
        formPage.preencherCor("@#");

        WebElement erro = driver.findElement(By.xpath("//*[contains(text(),'Cor não pode conter caracteres especiais')]"));
        assertTrue(erro.isDisplayed());
    }

    @Test
    @Order(5)
    public void deveExibirErroParaCorInvalidaNumeros() {
        mainPage.clicarPrimeiraVagaDisponivel();

        formPage.preencherPlaca("ABC1234");
        formPage.preencherTipo("CARRO");
        formPage.preencherModelo("Civic");
        formPage.preencherCor("123");

        WebElement erro = driver.findElement(By.xpath("//*[contains(text(),'Cor não pode ser apenas números')]"));
        assertTrue(erro.isDisplayed());
    }

    @Test
    @Order(6)
    public void deveExibirErroParaCorInvalidaMenos3Caracteres() {
        mainPage.clicarPrimeiraVagaDisponivel();

        formPage.preencherPlaca("ABC1234");
        formPage.preencherTipo("CARRO");
        formPage.preencherModelo("Civic");
        formPage.preencherCor("AM");

        WebElement erro = driver.findElement(By.xpath("//*[contains(text(),'Cor deve ter pelo menos 3 caracteres')]"));
        assertTrue(erro.isDisplayed());
    }

    @Test
    @Order(7)
    public void deveRegistrarVeiculoComDadosValidos() {
        String placa = faker.bothify("???####").toUpperCase(); // ex: ABC1234
        String modelo = faker.letterify("??") + faker.number().digits(2); // ex: Ci22
        String cor = faker.color().name().replaceAll("[^a-zA-Z]", ""); // apenas letras

        System.out.println(cor);

        WebElement vaga = mainPage.clicarPrimeiraVagaDisponivel();

        formPage.preencherPlaca(placa);
        formPage.preencherTipo("MOTO");
        formPage.preencherModelo(modelo);
        formPage.preencherCor(cor);
        formPage.submeterFormulario();

        String classes = vaga.getAttribute("class");
        //assertTrue(classes.contains("occupied"));

    }

    @Test
    @Order(8)
    public void deveMostrarErroAoRegistrarVeiculoComPlacaDuplicada() {
        // Use uma placa já cadastrada, pode ser fixa ou pegar da sua base de teste
        String placaDuplicada = "ABC1234"; // substitua pela placa válida já registrada no sistema

        // Dados do veículo (modelo, cor, tipo)
        String modelo = "Fiesta";
        String cor = "Vermelho";

        // Clicar em uma vaga disponível para tentar registrar
        mainPage.clicarPrimeiraVagaDisponivel();

        formPage.preencherPlaca(placaDuplicada);
        formPage.preencherTipo("CARRO");
        formPage.preencherModelo(modelo);
        formPage.preencherCor(cor);
        formPage.submeterFormulario();

        // Verificar que apareceu o toast com texto de erro
       // assertTrue(mainPage.existeToastComTexto("Erro ao registrar"));
    }

    @Test
    @Order(9)
    public void testRegistrarSaidaEmVagaOcupada() {

        mainPage.clicarNaPrimeiraVagaOcupada();


        mainPage.clicarRegistrarSaida();

        //assertTrue(mainPage.existeToastComTexto(" desocupada com sucesso"));
    }

    @Test
    public void testCancelarEntradaEmVagaOcupada() {

        mainPage.clicarNaPrimeiraVagaOcupada();


        mainPage.clicarCancelarEntrada();

        //assertTrue(mainPage.existeToastComTexto("Entrada da vaga " + numeroVaga + " cancelada com sucesso"));
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}
