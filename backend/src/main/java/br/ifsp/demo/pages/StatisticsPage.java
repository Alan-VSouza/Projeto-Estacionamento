package br.ifsp.demo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class StatisticsPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public StatisticsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(2));
    }

    public void abrirPagina() {
        driver.get("http://localhost:3000/statistics");
    }
    public void clicarBotaoAtualizar() {
        WebElement botao = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[contains(.,'Atualizar Estatísticas')]")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botao);
    }


    public boolean botaoAtualizarEstaVisivel() {
        return driver.findElement(By.xpath("//button[contains(text(),'Atualizar Estatísticas')]")).isDisplayed();
    }


    public WebElement botaoAtualizar() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[contains(.,'Atualizar Estatísticas')]")
        ));
    }




}

