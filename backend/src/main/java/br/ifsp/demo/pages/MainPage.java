package br.ifsp.demo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MainPage {
    private WebDriver driver;

    public MainPage(WebDriver driver) {
        this.driver = driver;
    }

    public WebElement clicarPrimeiraVagaDisponivel() {
        WebElement vaga = driver.findElement(By.cssSelector(".parking-spot.vacant"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", vaga);
        return vaga;
    }

    public boolean existeToastComTexto(String texto) {
        try {
            WebElement toast = driver.findElement(By.xpath("//div[contains(text(), '" + texto + "')]"));
            return toast.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clicarNaPrimeiraVagaOcupada() {
        WebElement vaga = driver.findElement(By.cssSelector(".parking-spot.occupied")); // pega a primeira vaga ocupada
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", vaga);
    }

    public void clicarRegistrarSaida() {
        WebElement botao = driver.findElement(By.cssSelector(".vacate-btn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botao);
    }

    public void clicarCancelarEntrada() {
        WebElement botao = driver.findElement(By.cssSelector(".cancel-btn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botao);
    }


    public String obterNumeroDaVagaSelecionada() {
        WebElement titulo = driver.findElement(By.cssSelector(".action-modal-header h3"));
        String texto = titulo.getText(); // Exemplo: "🚗 3"
        return texto.replaceAll("\\D+", ""); // Remove tudo que não é número
    }
}
