package pages;

import commons.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class SignInPage extends BasePage {

    @FindBy(id = "sign-in-email")
    private WebElement emailInput;

    @FindBy(id = "sign-in-password")
    private WebElement passwordInput;

    @FindBy(id = "btn-sign-in-submit")
    private WebElement submitButton;

    @FindBy(id = "sign-in-error")
    private WebElement errorMessage;

    @FindBy(id = "btn-nav-avatar")
    private WebElement navAvatar;

    private final By errorMessageBy = By.id("sign-in-error");
    private final By navAvatarBy = By.id("btn-nav-avatar");

    public SignInPage(WebDriver driver) {
        super(driver);
    }

    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/sign-in");
        waitForElementToBeVisible(emailInput);
    }

    public void login(String email, String password) {
        waitForElementToBeVisible(emailInput);
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

    public boolean isLoggedIn() {
        return isElementDisplayed(navAvatarBy);
    }
}

