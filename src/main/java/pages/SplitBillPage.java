package pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import commons.BasePage;

public class SplitBillPage extends BasePage {

    @FindBy(id = "split-title")
    private WebElement splitTitle;

    @FindBy(id = "btn-split-add-expense")
    private WebElement addExpenseButton;

    @FindBy(id = "btn-split-add-participant")
    private WebElement addParticipantButton;

    @FindBy(id = "equal-split-total")
    private WebElement equalSplitTotalInput;

    @FindBy(id = "btn-split-save")
    private WebElement saveButton;

    private final By equalSplitModalBy = By.id("split-equal-title");
    private final By remainderHintBy = By
            .xpath("//*[contains(text(), '+sisa 1 ke beberapa orang') or contains(text(), 'sisa 1')]");
    private final By splitEqualTotalBy = By.id("equal-split-total");

    public SplitBillPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToNew(String baseUrl) {
        driver.get(baseUrl + "/tools/split-bill?new=1");
        waitForUrlContains("/tools/split-bill/");
    }

    public void navigateToBill(String baseUrl, String billId) {
        driver.get(baseUrl + "/tools/split-bill/" + billId);
    }

    public void openEqualSplitModal() {
        By bagiRataBtn = By.xpath("//button[contains(text(), 'Bagi rata')]");
        if (isElementDisplayed(bagiRataBtn)) {
            clickWithRetry(bagiRataBtn);
            waitForElementToBeVisible(equalSplitModalBy);
        }
    }

    public void enterEqualSplitTotal(int total) {
        waitForElementToBeVisible(splitEqualTotalBy);
        WebElement input = driver.findElement(splitEqualTotalBy);
        input.clear();
        input.sendKeys(String.valueOf(total));
    }

    public boolean isRemainderDistributedProperly() {
        return isElementDisplayed(remainderHintBy);
    }

    /**
     * Pure business logic validator verifying odd decimal split calculation
     * e.g., 11000 IDR divided by 3 people:
     * base = floor(11000 / 3) = 3666
     * remainder = 11000 % 3 = 2
     * Two people pay 3667, one person pays 3666. Total = 3667 + 3667 + 3666 =
     * 11000.
     * 
     * @throws InterruptedException
     */
    public Integer verifyOddDecimalSplit(Integer totalAmount, Integer participantCount) throws InterruptedException {
        Integer total = 0;

        Thread.sleep(1000);

        for (int i = 1; i <= participantCount; i++) {
            By jumlah = By.xpath(
                    "(//*[text()='Pengeluaran bersama']/following-sibling::*//input[@aria-label='Jumlah'])[" + i + "]");
            total += Integer.parseInt(driver.findElement(jumlah).getAttribute("value").replace(".", ""));
        }

        System.out.println("Total: " + total);
        return total;

    }

    public void refreshPage() {
        driver.navigate().refresh();
    }
}
