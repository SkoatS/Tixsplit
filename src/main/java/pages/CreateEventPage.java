package pages;

import commons.BasePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

public class CreateEventPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(CreateEventPage.class);

    @FindBy(id = "event-name")
    private WebElement eventNameInput;

    @FindBy(id = "event-category")
    private WebElement eventCategoryInput;

    @FindBy(id = "event-description")
    private WebElement eventDescriptionInput;

    @FindBy(id = "event-starts-at")
    private WebElement eventStartsAtInput;

    @FindBy(id = "event-location")
    private WebElement eventLocationInput;

    @FindBy(id = "event-limit")
    private WebElement eventLimitInput;

    @FindBy(id = "event-visibility")
    private WebElement eventVisibilitySelect;

    @FindBy(id = "event-pricing-type")
    private WebElement eventPricingTypeSelect;

    @FindBy(id = "event-admin-bearer")
    private WebElement eventAdminBearerSelect;

    @FindBy(id = "event-price-amount")
    private WebElement eventPriceAmountInput;

    @FindBy(id = "event-payment-notes")
    private WebElement eventPaymentNotesInput;

    @FindBy(id = "btn-add-payment-method")
    private WebElement addPaymentMethodButton;

    @FindBy(id = "create-event-error")
    private WebElement createEventError;

    @FindBy(id = "btn-create-event-submit")
    private WebElement submitButton;

    private final By eventStartsAtBy = By.id("event-starts-at");
    private final By createEventErrorBy = By.id("create-event-error");
    private final By payNoteBy = By.cssSelector("input[id^='event-pay-note-']");
    private final By payAccountBy = By.cssSelector("input[id^='event-pay-account-']");
    private final By payQrisBy = By.cssSelector("input[id^='event-pay-qris-']");

    public CreateEventPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Sets a datetime-local input value robustly for React-based apps.
     *
     * Strategy (tried in order until value is accepted):
     *  1. React nativeInputValueSetter — sets value via React's internal setter and
     *     dispatches 'input' + 'change' events, which React's synthetic event system picks up.
     *  2. Keys.chord keyboard simulation — mimics real user interaction for browsers
     *     that render datetime-local as a segmented picker (Month/Day/Year HH:MM).
     *  3. JS setAttribute fallback — raw attribute set + manual 'change' dispatch.
     *
     * @param element  the WebElement with type="datetime-local"
     * @param dateTime value in format "yyyy-MM-ddTHH:mm" (e.g. "2026-11-30T19:00")
     */
    private void setDateTimePicker(WebElement element, String dateTime) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // --- Strategy 1: React nativeInputValueSetter ---
        try {
            js.executeScript(
                "var nativeInputValueSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
                "nativeInputValueSetter.call(arguments[0], arguments[1]);" +
                "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                element, dateTime
            );
            String val = element.getAttribute("value");
            if (val != null && !val.isEmpty()) {
                logger.info("Date set via React nativeInputValueSetter: {}", val);
                return;
            }
        } catch (Exception e) {
            logger.warn("React nativeInputValueSetter failed, trying keyboard simulation: {}", e.getMessage());
        }

        // --- Strategy 2: Keyboard simulation (MM/dd/yyyy hh:mm AM/PM for Chromium native picker) ---
        try {
            element.click();
            element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            element.sendKeys(Keys.DELETE);

            // Format: "MM" then TAB to day "dd" then TAB to year "yyyy" then TAB to hour "HH" then TAB to minutes "mm"
            // dateTime format expected: "yyyy-MM-ddTHH:mm" e.g. "2026-11-30T19:00"
            String[] parts = dateTime.split("T");
            String[] dateParts = parts[0].split("-"); // [yyyy, MM, dd]
            String[] timeParts = parts[1].split(":");  // [HH, mm]

            String yyyy = dateParts[0];
            String MM   = dateParts[1];
            String dd   = dateParts[2];
            String HH   = timeParts[0];
            String mm   = timeParts[1];

            // Chromium datetime-local segment order: MM / dd / yyyy  hh : mm AM/PM
            element.sendKeys(MM);        // month
            element.sendKeys(dd);        // day
            element.sendKeys(yyyy);      // year
            element.sendKeys(HH);        // hour (24h → browser auto-adjusts AM/PM)
            element.sendKeys(mm);        // minute

            String val = element.getAttribute("value");
            if (val != null && !val.isEmpty()) {
                logger.info("Date set via keyboard simulation: {}", val);
                return;
            }
        } catch (Exception e) {
            logger.warn("Keyboard simulation failed, trying JS setAttribute fallback: {}", e.getMessage());
        }

        // --- Strategy 3: JS setAttribute fallback ---
        js.executeScript(
            "arguments[0].setAttribute('value', arguments[1]);" +
            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
            element, dateTime
        );
        logger.info("Date set via JS setAttribute fallback: {}", dateTime);
    }

    public void navigateTo(String baseUrl, String communitySlug) {
        driver.get(baseUrl + "/communities/" + communitySlug + "/events/new");
        waitForElementToBeVisible(eventNameInput);
    }

    public void fillBasicInfo(String name, String category, String description, String startsAt, String location, String limit) {
        waitForElementToBeVisible(eventNameInput);
        eventNameInput.clear();
        eventNameInput.sendKeys(name);

        eventCategoryInput.clear();
        eventCategoryInput.sendKeys(category);

        eventDescriptionInput.clear();
        eventDescriptionInput.sendKeys(description);

        if (startsAt != null && !startsAt.isEmpty()) {
            waitForElementToBeVisible(eventStartsAtInput);
            scrollToElement(eventStartsAtInput);
            setDateTimePicker(eventStartsAtInput, startsAt);
        }

        eventLocationInput.clear();
        eventLocationInput.sendKeys(location);

        if (limit != null && !limit.isEmpty()) {
            eventLimitInput.clear();
            eventLimitInput.sendKeys(limit);
        }
    }

    public void setPricingFixed(String adminBearer, int amount, String paymentNotes, String payNote, String payAccount, String payQris) {
        Select pricingSelect = new Select(eventPricingTypeSelect);
        pricingSelect.selectByValue("FIXED");

        waitForElementToBeVisible(eventAdminBearerSelect);
        if (adminBearer != null && !adminBearer.isEmpty()) {
            Select bearerSelect = new Select(eventAdminBearerSelect);
            try {
                bearerSelect.selectByValue(adminBearer);
            } catch (Exception e) {
                bearerSelect.selectByVisibleText(adminBearer);
            }
        }

        waitForElementToBeVisible(eventPriceAmountInput);
        eventPriceAmountInput.clear();
        eventPriceAmountInput.sendKeys(String.valueOf(amount));

        if (paymentNotes != null && !paymentNotes.isEmpty()) {
            eventPaymentNotesInput.clear();
            eventPaymentNotesInput.sendKeys(paymentNotes);
        }

        // Fill payment method
        waitForElementToBeVisible(payNoteBy);
        WebElement noteEl = driver.findElement(payNoteBy);
        noteEl.clear();
        noteEl.sendKeys(payNote);

        WebElement accountEl = driver.findElement(payAccountBy);
        accountEl.clear();
        accountEl.sendKeys(payAccount);

        if (payQris != null && !payQris.isEmpty()) {
            WebElement qrisEl = driver.findElement(payQrisBy);
            qrisEl.clear();
            qrisEl.sendKeys(payQris);
        }
    }

    public void submitForm() {
        scrollToElement(submitButton);
        waitForElementClickable(submitButton);
        submitButton.click();
    }
    //false positive. DELETE THIS METHOD
    public boolean isRedirectedToEventDetails() {
        try {
            waitForUrlContains("/events/");
            return driver.getCurrentUrl().matches(".*/events/[a-zA-Z0-9]+$");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDateValidationTriggered() {
        // Native HTML5 validation check on event-starts-at input
        try {
            WebElement dateEl = driver.findElement(eventStartsAtBy);
            String validationMessage = dateEl.getAttribute("validationMessage");
            boolean isInvalid = (Boolean) ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("return !arguments[0].checkValidity();", dateEl);
            return isInvalid || (validationMessage != null && !validationMessage.isEmpty());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isErrorDisplayed() {
        return isElementDisplayed(createEventErrorBy);
    }

    public boolean containsTitleText(String title) {
        By text = By.xpath("//*[contains(text(), '"+ title +"')]");
        return  driver.findElement(text).isDisplayed();
    }
}

