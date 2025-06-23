package br.ifsp.demo.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class VehicleHistoryPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By inputPlaca = By.cssSelector("input.search-input");
    private By botaoBuscar = By.cssSelector("button.search-button");
    private By erroPlaca = By.cssSelector(".placa-error-message");
    private By resultadoHistorico = By.cssSelector(".history-results");
    private By nenhumResultado = By.cssSelector(".no-results");
    private By erroGeral = By.cssSelector(".error-message");
    private By toastBody = By.cssSelector(".Toastify__toast-body");

    public VehicleHistoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void abrirPagina() {
        driver.get("http://localhost:3000/vehicle-history");
    }

    public void preencherPlaca(String placa) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(inputPlaca));
        input.clear();
        input.sendKeys(placa);
    }

    public void clicarBuscar() {
        WebElement botao = wait.until(ExpectedConditions.elementToBeClickable(botaoBuscar));
        botao.click();
    }

    public boolean erroDePlacaVisivel() {
        return !driver.findElements(erroPlaca).isEmpty();
    }

    public boolean resultadoHistoricoVisivel() {
        return !driver.findElements(resultadoHistorico).isEmpty();
    }

    public boolean nenhumResultadoVisivel() {
        return !driver.findElements(nenhumResultado).isEmpty();
    }

    public boolean erroGeralVisivel() {
        return !driver.findElements(erroGeral).isEmpty();
    }

    public boolean toastVisivel() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(toastBody));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getTextoDoToast() {
        WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(toastBody));
        return toast.getText();
    }
}
