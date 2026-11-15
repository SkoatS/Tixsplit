package tests;

import core.BaseTests;
import core.DriverManager;
import core.RetryAnalyzer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CommunityPage;
import pages.SignInPage;

public class CommunityTests extends BaseTests {

    private static final Logger logger = LogManager.getLogger(CommunityTests.class);

    @Test(priority = 1, groups = {"smoke", "community"}, retryAnalyzer = RetryAnalyzer.class,
            description = "TC-COM-001: Membuat komunitas baru")
    public void testCreateCommunity() {
        //done checked
        logger.info("Starting TC-COM-001: Membuat komunitas baru");
        SignInPage signInPage = new SignInPage(DriverManager.getDriver());
        CommunityPage communityPage = new CommunityPage(DriverManager.getDriver());

        logger.info("Step 1: Logging in as admin");
        signInPage.navigateTo(config.getProperty("baseUrl"));
        signInPage.login(config.getProperty("adminEmail"), config.getProperty("adminPassword"));
        Assert.assertTrue(signInPage.isLoggedIn(), "Admin should be logged in before creating community");

        logger.info("Step 2: Navigating to create community page");
        communityPage.navigateToCreate(config.getProperty("baseUrl"));

        String uniqueSuffix = String.valueOf(System.currentTimeMillis() % 100000);
        String communityName = "Arsenal " + uniqueSuffix;
        String description = "untuk fans arsenal";
        String joinPolicy = "NEED_CONFIRMATION";

        logger.info("Step 3: Creating community with name: {}", communityName);
        communityPage.createCommunity(communityName, description, joinPolicy);

        String expectedSlug = "arsenal-" + uniqueSuffix;
        logger.info("Step 4: Verifying community page redirection for slug: {}", expectedSlug);
        Assert.assertTrue(communityPage.isCommunityCreated(expectedSlug),
                "User should be redirected to the newly created community page");
        Assert.assertTrue(communityPage.isOwnerBadgeVisible(),
                "Creator should have the OWNER badge visible on the community page");
    }
}

