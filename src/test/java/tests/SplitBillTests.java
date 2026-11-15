package tests;

import core.BaseTests;
import core.DriverManager;
import core.RetryAnalyzer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import pages.EventDetailsPage;
import pages.SignInPage;
import pages.SplitBillPage;

public class SplitBillTests extends BaseTests {

    private static final Logger logger = LogManager.getLogger(SplitBillTests.class);

    @Test(priority = 1, groups = { "smoke",
            "tools" }, retryAnalyzer = RetryAnalyzer.class, description = "TC-SB-003: Pembagian Split Bill sisa desimal ganjil")
    public void testSplitBillOddDecimalCalculation() throws Exception {
        logger.info("Starting TC-SB-003: Pembagian Split Bill sisa desimal ganjil");
        SignInPage signInPage = new SignInPage(DriverManager.getDriver());
        SplitBillPage splitBillPage = new SplitBillPage(DriverManager.getDriver());

        logger.info("Step 1: Logging in as admin");
        signInPage.navigateTo(config.getProperty("baseUrl"));
        signInPage.login(config.getProperty("adminEmail"), config.getProperty("adminPassword"));

        logger.info("Step 2: Navigating to split bill page");
        String splitBillId = "cmwf4mxz80001ic044joqt538";

        splitBillPage.navigateToBill(config.getProperty("baseUrl"), splitBillId);
        splitBillPage.refreshPage();

        logger.info("Step 3: Verifying odd decimal rounding business logic for 10.000 / 3");
        Integer subtotal = 10000;
        Integer totalAmount = subtotal;
        Integer participants = 3;

        Integer isExactTotal = splitBillPage.verifyOddDecimalSplit(totalAmount, participants);
        logger.info("Validation of odd decimal distribution without remainder discrepancy: {}", isExactTotal);
        Assert.assertEquals(isExactTotal, totalAmount,
                "Split bill must distribute rounding remainders (+1 IDR) so that total is exact with zero mismatch");
    }
}
