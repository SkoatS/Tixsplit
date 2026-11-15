package commons;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        PageFactory.initElements(driver, this);
    }

    public void waitForElementToBeVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public void waitForElementToBeVisible(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public void waitForElementClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public void waitForElementClickable(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public void waitForUrlContains(String fraction) {
        wait.until(ExpectedConditions.urlContains(fraction));
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void clickWithRetry(By locator) {
        new WebDriverWait(driver, Duration.ofSeconds(12))
                .ignoring(StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class)
                .until(d -> {
                    WebElement element = d.findElement(locator);
                    ((JavascriptExecutor) d).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);
                    if (!element.isDisplayed() || !element.isEnabled()) {
                        return false;
                    }
                    element.click();
                    return true;
                });
    }

    public boolean isElementDisplayed(WebElement element) {
        try {
            waitForElementToBeVisible(element);
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isElementDisplayed(By locator) {
        try {
            waitForElementToBeVisible(locator);
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}

