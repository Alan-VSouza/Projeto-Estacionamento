package br.ifsp.demo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;

public class ReportsPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public ReportsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(2));
    }

    public void abrirPagina() {
        driver.get("http://localhost:3000/reports");
    }

    public void clicarTabReceitaDiaria() {
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Receita Diária')]")));
        tab.click();
    }

    public void clicarTabRelatorioMensal() {
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Relatório Mensal')]")));
        tab.click();
    }

    public boolean estaMostrandoReceitaDiaria() {
        return Objects.requireNonNull(driver.getPageSource()).contains("Relatório Diário de Receita");
    }

    public boolean estaMostrandoRelatorioMensal() {
        return Objects.requireNonNull(driver.getPageSource()).contains("Relatório Mensal");
    }

    public WebElement botaoExportarPDF() {
        return driver.findElement(By.xpath("//button[contains(text(),'Exportar PDF')]"));
    }
    public WebElement botaoExportarCSV() {
        return driver.findElement(By.xpath("//button[contains(text(),'Exportar CSV')]"));
    }

    public void clicarCardReceita() {
        WebElement revenueCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'flip-card')]//h3[contains(text(),'Receita Total')]/ancestor::div[contains(@class,'flip-card')]")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", revenueCard);
    }

    public WebElement cardReceitaFlipInner() {
        WebElement revenueCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'flip-card')]//h3[contains(text(),'Receita Total')]/ancestor::div[contains(@class,'flip-card')]")
        ));
        return revenueCard.findElement(By.cssSelector(".flip-card-inner"));
    }

    public boolean cardReceitaEstaFlipped() {
        return cardReceitaFlipInner().getAttribute("class").contains("flipped");
    }

    public void clicarCardVehicles() {
        WebElement vehiclesCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'flip-card')]//h3[contains(text(),'Veículos Atendidos')]/ancestor::div[contains(@class,'flip-card')]")
        ));
        WebElement flipInner = vehiclesCard.findElement(By.cssSelector(".flip-card-inner"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", flipInner);
    }

    public boolean cardVehiclesEstaFlipped() {
        WebElement vehiclesCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'flip-card')]//h3[contains(text(),'Veículos Atendidos')]/ancestor::div[contains(@class,'flip-card')]")
        ));
        WebElement flipInner = vehiclesCard.findElement(By.cssSelector(".flip-card-inner"));
        return flipInner.getAttribute("class").contains("flipped");
    }

    public void clicarCardAvgTime() {
        WebElement avgTimeCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'flip-card')]//h3[contains(text(),'Tempo Médio')]/ancestor::div[contains(@class,'flip-card')]")
        ));
        WebElement flipInner = avgTimeCard.findElement(By.cssSelector(".flip-card-inner"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", flipInner);
    }

    public boolean cardAvgTimeEstaFlipped() {
        WebElement avgTimeCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'flip-card')]//h3[contains(text(),'Tempo Médio')]/ancestor::div[contains(@class,'flip-card')]")
        ));
        WebElement flipInner = avgTimeCard.findElement(By.cssSelector(".flip-card-inner"));
        return flipInner.getAttribute("class").contains("flipped");
    }

    public void clicarCardOccupancy() {
        WebElement occupancyCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'flip-card')]//h3[contains(text(),'Taxa de Ocupação')]/ancestor::div[contains(@class,'flip-card')]")
        ));
        WebElement flipInner = occupancyCard.findElement(By.cssSelector(".flip-card-inner"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", flipInner);
    }

    public boolean cardOccupancyEstaFlipped() {
        WebElement occupancyCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'flip-card')]//h3[contains(text(),'Taxa de Ocupação')]/ancestor::div[contains(@class,'flip-card')]")
        ));
        WebElement flipInner = occupancyCard.findElement(By.cssSelector(".flip-card-inner"));
        return flipInner.getAttribute("class").contains("flipped");
    }

    // Clicar no card Receita Total
    public void clicarCardMensalReceita() {
        WebElement revenueCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'flip-card')]//h3[contains(text(),'Receita Total')]/ancestor::div[contains(@class,'flip-card')]")
        ));
        WebElement flipInner = revenueCard.findElement(By.cssSelector(".flip-card-inner"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", flipInner);
    }

    public boolean cardMensalReceitaEstaFlipped() {
        WebElement revenueCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'flip-card')]//h3[contains(text(),'Receita Total')]/ancestor::div[contains(@class,'flip-card')]")
        ));
        WebElement flipInner = revenueCard.findElement(By.cssSelector(".flip-card-inner"));
        return flipInner.getAttribute("class").contains("flipped");
    }

    // Veículos Atendidos
    public void clicarCardMensalVeiculos() {
        WebElement vehiclesCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'flip-card')]//h3[contains(text(),'Veículos Atendidos')]/ancestor::div[contains(@class,'flip-card')]")
        ));
        WebElement flipInner = vehiclesCard.findElement(By.cssSelector(".flip-card-inner"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", flipInner);
    }

    public boolean cardMensalVeiculosEstaFlipped() {
        WebElement vehiclesCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'flip-card')]//h3[contains(text(),'Veículos Atendidos')]/ancestor::div[contains(@class,'flip-card')]")
        ));
        WebElement flipInner = vehiclesCard.findElement(By.cssSelector(".flip-card-inner"));
        return flipInner.getAttribute("class").contains("flipped");
    }

    // Tempo Médio
    public void clicarCardMensalAvgTime() {
        WebElement avgTimeCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'flip-card')]//h3[contains(text(),'Tempo Médio')]/ancestor::div[contains(@class,'flip-card')]")
        ));
        WebElement flipInner = avgTimeCard.findElement(By.cssSelector(".flip-card-inner"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", flipInner);
    }

    public boolean cardMensalAvgTimeEstaFlipped() {
        WebElement avgTimeCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'flip-card')]//h3[contains(text(),'Tempo Médio')]/ancestor::div[contains(@class,'flip-card')]")
        ));
        WebElement flipInner = avgTimeCard.findElement(By.cssSelector(".flip-card-inner"));
        return flipInner.getAttribute("class").contains("flipped");
    }

    // Melhor Dia
    public void clicarCardMensalMelhorDia() {
        WebElement bestDayCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'flip-card')]//h3[contains(text(),'Melhor Dia')]/ancestor::div[contains(@class,'flip-card')]")
        ));
        WebElement flipInner = bestDayCard.findElement(By.cssSelector(".flip-card-inner"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", flipInner);
    }

    public boolean cardMensalMelhorDiaEstaFlipped() {
        WebElement bestDayCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'flip-card')]//h3[contains(text(),'Melhor Dia')]/ancestor::div[contains(@class,'flip-card')]")
        ));
        WebElement flipInner = bestDayCard.findElement(By.cssSelector(".flip-card-inner"));
        return flipInner.getAttribute("class").contains("flipped");
    }


}
