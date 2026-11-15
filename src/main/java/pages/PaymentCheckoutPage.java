package pages;

import commons.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class PaymentCheckoutPage extends BasePage {

    @FindBy(id = "event-payment-transfer-amount")
    private WebElement transferAmountText;

    @FindBy(id = "event-payment-unique-hint")
    private WebElement uniqueHintText;

    @FindBy(id = "event-payment-amount-breakdown")
    private WebElement breakdownText;

    @FindBy(id = "event-payment-countdown")
    private WebElement countdownTimer;

    @FindBy(id = "btn-event-mark-paid")
    private WebElement markPaidButton;

    @FindBy(id = "btn-event-cancel-hold")
    private WebElement cancelHoldButton;

    private final By transferAmountBy = By.id("event-payment-transfer-amount");
    private final By markPaidButtonBy = By.id("btn-event-mark-paid");
    private final By expiredNoticeBy = By.xpath("//*[contains(text(), 'Waktu pembayaran habis') or contains(text(), 'expired') or contains(text(), '410')]");

    public PaymentCheckoutPage(WebDriver driver) {
        super(driver);
    }

    public void navigateTo(String checkoutUrl) {
        driver.get(checkoutUrl);
        waitForElementToBeVisible(transferAmountBy);
    }

    public String getTransferAmount() {
        waitForElementToBeVisible(transferAmountBy);
        return transferAmountText.getText();
    }

    public String getUniqueHint() {
        if (isElementDisplayed(uniqueHintText)) {
            return uniqueHintText.getText();
        }
        return "";
    }

    public String getBreakdown() {
        if (isElementDisplayed(breakdownText)) {
            return breakdownText.getText();
        }
        return "";
    }

    public String getCountdown() {
        if (isElementDisplayed(countdownTimer)) {
            return countdownTimer.getText();
        }
        return "";
    }

    public void clickMarkPaid() {
        waitForElementToBeVisible(markPaidButtonBy);
        clickWithRetry(markPaidButtonBy);
    }

    public void clickCancelHold() {
        if (isElementDisplayed(cancelHoldButton)) {
            cancelHoldButton.click();
        }
    }

    public boolean isExpiredNoticeDisplayed() {
        return isElementDisplayed(expiredNoticeBy);
    }
}

