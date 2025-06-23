package br.ifsp.demo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class VehicleEntryFormPage {
    private WebDriver driver;

    public VehicleEntryFormPage(WebDriver driver) {
        this.driver = driver;
    }

    public void preencherPlaca(String placa) {
        WebElement campoPlaca = driver.findElement(By.id("placa"));
        campoPlaca.clear();
        campoPlaca.sendKeys(placa);

        System.out.println("Valor preenchido no campo Placa: " + campoPlaca.getAttribute("value"));
    }

    public void preencherTipo(String tipo) {
        WebElement campoTipo = driver.findElement(By.id("tipoVeiculo"));
        Select select = new Select(campoTipo);
        select.selectByValue(tipo); // Ex: "CARRO" ou "MOTO"

        System.out.println("Valor preenchido no campo Tipo: " + campoTipo.getAttribute("value"));
    }

    public void preencherModelo(String modelo) {
        WebElement campoModelo = driver.findElement(By.id("modelo"));
        campoModelo.clear();
        campoModelo.sendKeys(modelo);

        System.out.println("Valor preenchido no campo Modelo: " + campoModelo.getAttribute("value"));

    }

    public void preencherCor(String cor) {
        WebElement campoCor = driver.findElement(By.id("cor"));
        campoCor.clear();
        campoCor.sendKeys(cor);

        System.out.println("Valor preenchido no campo Cor: " + campoCor.getAttribute("value"));
    }

    public void submeterFormulario() {
        WebElement botao = driver.findElement(By.cssSelector("button[type='submit']"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", botao);

    }

    public boolean existeErroComTexto(String texto) {
        try {
            WebElement erro = driver.findElement(By.xpath("//span[contains(text(), '" + texto + "')]"));
            return erro.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
