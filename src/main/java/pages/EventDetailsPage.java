package pages;

import commons.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class EventDetailsPage extends BasePage {

    @FindBy(id = "btn-event-rsvp")
    private WebElement rsvpButton;

    @FindBy(id = "btn-event-rsvp-cancel")
    private WebElement rsvpCancelButton;

    @FindBy(id = "btn-event-rsvp-rejoin")
    private WebElement rsvpRejoinButton;

    @FindBy(id = "btn-event-create-payment")
    private WebElement createPaymentButton;

    @FindBy(id = "event-rsvp-error")
    private WebElement rsvpError;

    private final By rsvpButtonBy = By.id("btn-event-rsvp");
    private final By rsvpCancelButtonBy = By.id("btn-event-rsvp-cancel");
    private final By rsvpRejoinButtonBy = By.id("btn-event-rsvp-rejoin");
    private final By createPaymentButtonBy = By.id("btn-event-create-payment");
    private final By attendingNoticeBy = By.xpath("//*[text()= 'You are attending.']");
    private final By validatedNoticeBy = By.xpath("//*[contains(text(), 'tervalidasi') or contains(text(), 'PAID') or contains(text(), 'Sudah bayar')]");

    public EventDetailsPage(WebDriver driver) {
        super(driver);
    }

    public void navigateTo(String baseUrl, String eventId) {
        driver.get(baseUrl + "/events/" + eventId);
    }

    public void clickRsvp() {
        waitForElementToBeVisible(rsvpButtonBy);
        clickWithRetry(rsvpButtonBy);
    }

    public void clickCancelAttendance() {
        waitForElementToBeVisible(rsvpCancelButtonBy);
        clickWithRetry(rsvpCancelButtonBy);
    }

    public void clickRejoin() {
        waitForElementToBeVisible(rsvpRejoinButtonBy);
        clickWithRetry(rsvpRejoinButtonBy);
    }

    public void clickCreatePayment() {
        waitForElementToBeVisible(createPaymentButtonBy);
        clickWithRetry(createPaymentButtonBy);
    }

    public boolean isAttending() {
        try {
            waitForElementToBeVisible(rsvpCancelButtonBy);
            return isElementDisplayed(rsvpCancelButtonBy) && isElementDisplayed(attendingNoticeBy);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEventFull() {
        try {
            waitForElementToBeVisible(rsvpButtonBy);
            WebElement btn = driver.findElement(rsvpButtonBy);
            String btnText = btn.getText();
            boolean isDisabled = !btn.isEnabled() || "true".equals(btn.getAttribute("disabled"));
            return isDisabled || btnText.equalsIgnoreCase("Event Full");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRejoinVisible() {
        return isElementDisplayed(rsvpRejoinButtonBy);
    }

    public boolean isValidatedPaymentDisplayed() {
        return isElementDisplayed(validatedNoticeBy);
    }

    public void refreshPage()
    {
        driver.navigate().refresh();
    }
}

