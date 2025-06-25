package br.ifsp.demo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage {
    private final WebDriver driver;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        driver.get("http://localhost:3000/login");
    }

    // Elementos
    private WebElement emailField() {
        return driver.findElement(By.id("email"));
    }

    private WebElement passwordField() {
        return driver.findElement(By.id("password"));
    }

    private WebElement loginButton() {
        return driver.findElement(By.cssSelector("button[type='submit']"));
    }

    private WebElement errorMessage() {
        return driver.findElement(By.cssSelector(".error-message"));
    }

    private WebElement registerButton() {
        return driver.findElement(By.xpath("//button[contains(text(), 'registrar')]"));
    }

    // Ações
    public void preencherEmail(String email) {
        emailField().clear();
        emailField().sendKeys(email);
    }

    public void preencherSenha(String senha) {
        passwordField().clear();
        passwordField().sendKeys(senha);
    }

    public void clicarLogin() {
        loginButton().click();
    }

    public boolean botaoLoginHabilitado() {
        return loginButton().isEnabled();
    }

    public void clicarRegistrarFuncionario() {
        registerButton().click();
    }

    public boolean erroPresente() {
        return driver.findElements(By.cssSelector(".error-message")).size() > 0;
    }

    public String obterMensagemErro() {
        return errorMessage().getText();
    }
}

