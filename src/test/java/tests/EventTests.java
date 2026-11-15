package tests;

import core.BaseTests;
import core.DriverManager;
import core.RetryAnalyzer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CreateEventPage;
import pages.SignInPage;

public class EventTests extends BaseTests {

        private static final Logger logger = LogManager.getLogger(EventTests.class);

        @Test(priority = 1, groups = { "smoke",
                        "event" }, retryAnalyzer = RetryAnalyzer.class, description = "TC-EVT-001: Membuat event berbayar (FIXED) PUBLIC")
        public void testCreateFixedPublicEvent() {
                // CHECKED - FALSE POSTIVE
                logger.info("Starting TC-EVT-001: Membuat event berbayar (FIXED) PUBLIC");
                SignInPage signInPage = new SignInPage(DriverManager.getDriver());
                CreateEventPage createEventPage = new CreateEventPage(DriverManager.getDriver());

                logger.info("Step 1: Logging in as admin");
                signInPage.navigateTo(config.getProperty("baseUrl"));
                signInPage.login(config.getProperty("adminEmail"), config.getProperty("adminPassword"));
                Assert.assertTrue(signInPage.isLoggedIn(), "Admin should be logged in before creating event");

                logger.info("Step 2: Navigating to create event page");
                createEventPage.navigateTo(config.getProperty("baseUrl"), "arsenal");

                String uniqueSuffix = String.valueOf(System.currentTimeMillis() % 100000);
                String eventName = "London Forever " + uniqueSuffix;
                String category = "Knowledge";
                String description = "Champion 2025/2026";
                String date = "2026-11-30T19:00";
                String location = "London";
                String limit = "14";

                logger.info("Step 3: Filling basic event info");
                createEventPage.fillBasicInfo(eventName, category, description, date, location, limit);

                logger.info("Step 4: Setting fixed pricing and payment method");
                createEventPage.setPricingFixed("PARTICIPANT", 50000, "Konfirmasi Arsenal adalah champion!", "BRI",
                                "01234567", "");

                logger.info("Step 5: Submitting event form");
                createEventPage.submitForm();
                Assert.assertTrue(createEventPage.containsTitleText(eventName),
                                "User should be redirected to the newly created event details page");
        }

        @Test(priority = 2, groups = { "smoke",
                        "event" }, retryAnalyzer = RetryAnalyzer.class, description = "TC-EVT-002: Gagal membuat event jika field wajib kosong")
        public void testCreateEventMissingRequiredDate() {
                logger.info("Starting TC-EVT-002: Gagal membuat event jika field wajib kosong");
                SignInPage signInPage = new SignInPage(DriverManager.getDriver());
                CreateEventPage createEventPage = new CreateEventPage(DriverManager.getDriver());

                logger.info("Step 1: Logging in as admin");
                signInPage.navigateTo(config.getProperty("baseUrl"));
                signInPage.login(config.getProperty("adminEmail"), config.getProperty("adminPassword"));
                Assert.assertTrue(signInPage.isLoggedIn(), "Admin should be logged in");

                logger.info("Step 2: Navigating to create event page");
                createEventPage.navigateTo(config.getProperty("baseUrl"), "arsenal");

                String eventName = "London Forever Empty Date";
                String category = "Knowledge";
                String description = "Champion 2025/2026";
                String emptyDate = "";
                String location = "London";
                String limit = "14";

                logger.info("Step 3: Filling basic event info with empty date");
                createEventPage.fillBasicInfo(eventName, category, description, emptyDate, location, limit);
                createEventPage.setPricingFixed("PARTICIPANT", 50000, "Konfirmasi Arsenal adalah champion!", "BRI",
                                "01234567", "");

                logger.info("Step 4: Clicking submit with missing date field");
                createEventPage.submitForm();

                Assert.assertTrue(createEventPage.isDateValidationTriggered(),
                                "Form validation should prevent submission when required date field is empty");
        }
}
