package pages;

import commons.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CreateAccountPage extends BasePage {

    @FindBy(id = "create-name")
    private WebElement nameInput;

    @FindBy(id = "create-email")
    private WebElement emailInput;

    @FindBy(id = "create-password")
    private WebElement passwordInput;

    @FindBy(id = "btn-create-account-submit")
    private WebElement submitButton;

    @FindBy(id = "create-account-error")
    private WebElement errorMessage;

    private final By errorMessageBy = By.id("create-account-error");

    public CreateAccountPage(WebDriver driver) {
        super(driver);
    }

    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/create-account");
        waitForElementToBeVisible(nameInput);
    }

    public void register(String name, String email, String password) {
        waitForElementToBeVisible(nameInput);
        nameInput.clear();
        nameInput.sendKeys(name);
        emailInput.clear();
        emailInput.sendKeys(email);
        passwordInput.clear();
        passwordInput.sendKeys(password);
        waitForElementClickable(submitButton);
        submitButton.click();
    }

    public String getErrorMessage() {
        waitForElementToBeVisible(errorMessageBy);
        return errorMessage.getText();
    }

    public boolean isErrorMessageDisplayed() {
        return isElementDisplayed(errorMessageBy);
    }
}

