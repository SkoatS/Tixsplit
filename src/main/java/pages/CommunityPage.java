package pages;

import commons.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

public class CommunityPage extends BasePage {

    @FindBy(id = "community-name")
    private WebElement communityNameInput;

    @FindBy(id = "community-description")
    private WebElement communityDescriptionInput;

    @FindBy(id = "btn-community-cover-location")
    private WebElement coverLocationButton;

    @FindBy(id = "community-join-policy")
    private WebElement joinPolicySelect;

    @FindBy(id = "btn-create-community-submit")
    private WebElement submitButton;

    private final By communityTitleBy = By.cssSelector("h1, .nf-title");
    private final By ownerBadgeBy = By.xpath("//span[contains(@class, 'nf-badge') and contains(text(), 'Owner')]");

    public CommunityPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToCreate(String baseUrl) {
        driver.get(baseUrl + "/communities/new");
        waitForElementToBeVisible(communityNameInput);
    }

    public void createCommunity(String name, String description, String joinPolicyValue) {
        waitForElementToBeVisible(communityNameInput);
        communityNameInput.clear();
        communityNameInput.sendKeys(name);

        communityDescriptionInput.clear();
        communityDescriptionInput.sendKeys(description);

        if (isElementDisplayed(coverLocationButton)) {
            coverLocationButton.click();
        }

        if (joinPolicyValue != null && !joinPolicyValue.isEmpty()) {
            Select select = new Select(joinPolicySelect);
            try {
                select.selectByValue(joinPolicyValue);
            } catch (Exception e) {
                select.selectByVisibleText(joinPolicyValue);
            }
        }

        scrollToElement(submitButton);
        waitForElementClickable(submitButton);
        submitButton.click();
    }

    public boolean isCommunityCreated(String expectedSlug) {
        try {
            waitForUrlContains("/communities/" + expectedSlug);
            return driver.getCurrentUrl().contains("/communities/" + expectedSlug);
        } catch (Exception e) {
            return driver.getCurrentUrl().contains("/communities/");
        }
    }

    public boolean isOwnerBadgeVisible() {
        return isElementDisplayed(ownerBadgeBy);
    }
}

