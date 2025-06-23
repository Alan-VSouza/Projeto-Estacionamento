package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RegisterPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open() {
        driver.get("http://localhost:3000/register-admin"); // ajuste se necessário
    }

    public WebElement nameField() {
        return driver.findElement(By.id("name"));
    }

    public WebElement lastnameField() {
        return driver.findElement(By.id("lastname"));
    }

    public WebElement emailField() {
        return driver.findElement(By.id("email"));
    }

    public WebElement passwordField() {
        return driver.findElement(By.id("password"));
    }

    public WebElement confirmPasswordField() {
        return driver.findElement(By.id("confirmPassword"));
    }

    public WebElement registerButton() {
        return driver.findElement(By.xpath("//button[@type='submit']"));
    }

    public boolean registrerButtonEnabled() {
        return registerButton().isEnabled();
    }

    public WebElement successMessage() {
        return driver.findElement(By.xpath("//p[contains(text(),'Administrador registrado com sucesso')]"));
    }

    public WebElement errorMessage() {
        return driver.findElement(By.cssSelector("p[style*='color: red']"));
    }

    public WebElement fieldError(String fieldId) {
        return driver.findElement(By.xpath("//input[@id='" + fieldId + "']/following-sibling::div[@class='error-message']"));
    }
}