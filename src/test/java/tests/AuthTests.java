package tests;

import core.BaseTests;
import core.DriverManager;
import core.RetryAnalyzer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CreateAccountPage;
import pages.SignInPage;

public class AuthTests extends BaseTests {

    private static final Logger logger = LogManager.getLogger(AuthTests.class);

    @Test(priority = 1, groups = {"smoke", "auth"}, retryAnalyzer = RetryAnalyzer.class,
            description = "TC-AUTH-002: Registrasi dengan email duplikat")
    public void testRegisterDuplicateEmail() {
        //Done Checked
        logger.info("Starting TC-AUTH-002: Registrasi dengan email duplikat");
        CreateAccountPage createAccountPage = new CreateAccountPage(DriverManager.getDriver());

        createAccountPage.navigateTo(config.getProperty("baseUrl"));
        logger.info("Submitting registration with already registered email: {}", config.getProperty("adminEmail"));
        createAccountPage.register("admin", config.getProperty("adminEmail"), config.getProperty("adminPassword"));

        Assert.assertTrue(createAccountPage.isErrorMessageDisplayed(),
                "Expected error message element to be displayed");
        String actualError = createAccountPage.getErrorMessage();
        logger.info("Actual error received: {}", actualError);
        Assert.assertTrue(actualError.contains("Email already registered. Sign in instead."),
                "Expected 'Email already registered. Sign in instead.' but got: " + actualError);
    }

    @Test(priority = 2, groups = {"smoke", "auth"}, retryAnalyzer = RetryAnalyzer.class,
            description = "TC-AUTH-003: Login email+password valid")
    public void testLoginValidCredentials() {
        //Done Checked
        logger.info("Starting TC-AUTH-003: Login email+password valid");
        SignInPage signInPage = new SignInPage(DriverManager.getDriver());

        signInPage.navigateTo(config.getProperty("baseUrl"));
        logger.info("Submitting login with valid credentials for: {}", config.getProperty("adminEmail"));
        signInPage.login(config.getProperty("adminEmail"), config.getProperty("adminPassword"));

        Assert.assertTrue(signInPage.isLoggedIn(),
                "User should be successfully logged in and nav avatar should be displayed");
    }

    @Test(priority = 3, groups = {"smoke", "auth"}, retryAnalyzer = RetryAnalyzer.class,
            description = "TC-AUTH-004: Login kredensial salah")
    public void testLoginInvalidCredentials() {
        //Done Checking
        logger.info("Starting TC-AUTH-004: Login kredensial salah");
        SignInPage signInPage = new SignInPage(DriverManager.getDriver());

        signInPage.navigateTo(config.getProperty("baseUrl"));
        logger.info("Submitting login with invalid password for: {}", config.getProperty("adminEmail"));
        signInPage.login(config.getProperty("adminEmail"), config.getProperty("invalidPassword"));

        Assert.assertTrue(signInPage.isErrorMessageDisplayed(),
                "Expected error message element to be displayed");
        String actualError = signInPage.getErrorMessage();
        logger.info("Actual error received: {}", actualError);
        Assert.assertTrue(actualError.toLowerCase().contains("invalid email or password"),
                "Expected 'Invalid email or password' but got: " + actualError);
    }
}

