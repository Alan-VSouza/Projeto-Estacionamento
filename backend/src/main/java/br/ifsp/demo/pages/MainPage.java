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
}
