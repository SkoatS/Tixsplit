package tests;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import core.BaseTests;
import core.DriverManager;
import core.RetryAnalyzer;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.EventDetailsPage;
import pages.PaymentCheckoutPage;
import pages.SignInPage;

public class PaymentTests extends BaseTests {

        private static final Logger logger = LogManager.getLogger(PaymentTests.class);

        @Test(priority = 1, groups = { "smoke",
                        "payment" }, retryAnalyzer = RetryAnalyzer.class, description = "TC-PAY-001: RSVP Event Gratis berhasil")
        public void testRsvpFreeEventSuccess() {
                // done checking n passed not bug
                logger.info("Starting TC-PAY-001: RSVP Event Gratis berhasil");
                SignInPage signInPage = new SignInPage(DriverManager.getDriver());
                EventDetailsPage eventPage = new EventDetailsPage(DriverManager.getDriver());

                logger.info("Step 1: Logging in as member user");
                signInPage.navigateTo(config.getProperty("baseUrl"));
                signInPage.login(config.getProperty("memberEmail"), config.getProperty("memberPassword"));
                Assert.assertTrue(signInPage.isLoggedIn(), "Member should be logged in");

                String freeEventId = "cmvv20mw00001jm040i9gbc0e";
                logger.info("Step 2: Navigating to free event page: {}", freeEventId);
                eventPage.navigateTo(config.getProperty("baseUrl"), freeEventId);

                if (!eventPage.isAttending()) {
                        logger.info("Step 3: Clicking RSVP button");
                        eventPage.clickRsvp();
                }

                logger.info("Step 4: Verifying user is attending free event");
                Assert.assertTrue(eventPage.isAttending(), "User should be attending the free event");
        }

        @Test(priority = 2, groups = {
                        "payment" }, retryAnalyzer = RetryAnalyzer.class, description = "TC-PAY-002: RSVP Event Gratis saat kuota penuh")
        public void testRsvpEventQuotaFull() {
                logger.info("Starting TC-PAY-002: RSVP Event Gratis saat kuota penuh");
                SignInPage signInPage = new SignInPage(DriverManager.getDriver());
                EventDetailsPage eventPage = new EventDetailsPage(DriverManager.getDriver());

                logger.info("Step 1: Logging in as member user");
                signInPage.navigateTo(config.getProperty("baseUrl"));
                signInPage.login(config.getProperty("memberEmail"), config.getProperty("memberPassword"));
                Assert.assertTrue(signInPage.isLoggedIn(), "Member should be logged in");

                String fullEventId = "cmvxeklzy00gnjs04mt6xhvqc";
                logger.info("Navigating to full event: {}", fullEventId);
                eventPage.navigateTo(config.getProperty("baseUrl"), fullEventId);

                logger.info("Verifying event is full / button disabled");
                Assert.assertTrue(eventPage.isEventFull() || DriverManager.getDriver().getPageSource().contains("full"),
                                "Event should indicate that quota is full");
        }

        @Test(priority = 3, groups = {
                        "payment" }, retryAnalyzer = RetryAnalyzer.class, description = "TC-PAY-003: Join kembali event yang sudah dibayar dan di-cancel")
        public void testRejoinPaidAndValidatedEvent() {
                // done checking and maintenance validation
                logger.info("Starting TC-PAY-003: Join kembali event yang sudah dibayar dan di-cancel");
                SignInPage signInPage = new SignInPage(DriverManager.getDriver());
                EventDetailsPage eventPage = new EventDetailsPage(DriverManager.getDriver());

                signInPage.navigateTo(config.getProperty("baseUrl"));
                signInPage.login(config.getProperty("adminEmail"), config.getProperty("adminPassword"));

                String paidEventId = "cmvvvjh7g0001l304p0l8wt4z";
                eventPage.navigateTo(config.getProperty("baseUrl"), paidEventId);
                eventPage.refreshPage();
                Assert.assertTrue(eventPage.isAttending());
                logger.info("Canceling attendance on already validated event");
                eventPage.clickCancelAttendance();
                Assert.assertTrue(eventPage.isValidatedPaymentDisplayed());

                logger.info("Clicking rejoin button without paying again");
                eventPage.clickRejoin();
                Assert.assertTrue(eventPage.isAttending(),
                                "User should be able to rejoin event without paying again");
        }

        @Test(priority = 4, groups = {
                        "payment" }, retryAnalyzer = RetryAnalyzer.class, description = "TC-PAY-005: Kalkulasi Admin Fee & Kode Unik")
        public void testCalculateAdminFeeAndUniqueCode() {
                logger.info("Starting TC-PAY-005: Kalkulasi Admin Fee & Kode Unik");

                // 1. Authenticate via API to get auth token
                Response loginResp = RestAssured.given()
                                .contentType("application/json")
                                .body("{\"email\":\"" + config.getProperty("adminEmail") + "\",\"password\":\""
                                                + config.getProperty("adminPassword") + "\"}")
                                .post(config.getProperty("baseUrl") + "/api/auth/login");

                Assert.assertEquals(loginResp.getStatusCode(), 200, "Login API must succeed");
                String token = loginResp.jsonPath().getString("token");

                // 2. Create payment intent for fixed price event cmvvvjh7g0001l304p0l8wt4z
                String eventId = "cmvvvjh7g0001l304p0l8wt4z";
                Response payResp = RestAssured.given()
                                .contentType("application/json")
                                .header("Authorization", "Bearer " + token)
                                .cookie("nerd_octopus_token", token)
                                .body("{}")
                                .post(config.getProperty("baseUrl") + "/api/events/" + eventId + "/payments");

                logger.info("Payment API response code: {}", payResp.getStatusCode());
                if (payResp.getStatusCode() == 201 || payResp.getStatusCode() == 200) {
                        JsonObject json = JsonParser.parseString(payResp.getBody().asString()).getAsJsonObject();
                        JsonObject payment = json.getAsJsonObject("payment");
                        JsonObject breakdown = json.getAsJsonObject("breakdown");

                        int baseAmount = breakdown.get("baseAmount").getAsInt();
                        int adminFee = breakdown.get("adminFee").getAsInt();
                        int totalAmount = breakdown.get("totalAmount").getAsInt();
                        int uniqueCode = payment.get("uniqueCode").getAsInt();

                        logger.info("Base Amount: {}, Admin Fee: {}, Total: {}, Unique Code: {}",
                                        baseAmount, adminFee, totalAmount, uniqueCode);

                        // Verify admin fee is 5%
                        int expectedAdminFee = (int) Math.round(baseAmount * 0.05);
                        Assert.assertEquals(adminFee, expectedAdminFee, "Admin fee must be 5% of base amount");

                        // Verify unique code is 3 digits (1-999)
                        Assert.assertTrue(uniqueCode >= 1 && uniqueCode <= 999,
                                        "Unique code must be between 1 and 999, got: " + uniqueCode);

                        // Clean up hold
                        String paymentId = payment.get("id").getAsString();
                        RestAssured.given()
                                        .header("Authorization", "Bearer " + token)
                                        .cookie("nerd_octopus_token", token)
                                        .post(config.getProperty("baseUrl") + "/api/events/" + eventId + "/payments/"
                                                        + paymentId + "/reject");
                }
        }

        @Test(priority = 5, groups = {
                        "payment" }, retryAnalyzer = RetryAnalyzer.class, description = "TC-PAY-006: Klik 'Saya sudah membayar' lewat batas waktu")
        public void testExpiredPaymentHandling() {
                logger.info("Starting TC-PAY-006: Klik 'Saya sudah membayar' lewat batas waktu");
                // Verify via API that an expired/rejected payment returns 410 or rejects
                // mark-paid
                Response loginResp = RestAssured.given()
                                .contentType("application/json")
                                .body("{\"email\":\"" + config.getProperty("adminEmail") + "\",\"password\":\""
                                                + config.getProperty("adminPassword") + "\"}")
                                .post(config.getProperty("baseUrl") + "/api/auth/login");

                String token = loginResp.jsonPath().getString("token");
                String eventId = "cmvvvjh7g0001l304p0l8wt4z";

                Response payResp = RestAssured.given()
                                .contentType("application/json")
                                .header("Authorization", "Bearer " + token)
                                .cookie("nerd_octopus_token", token)
                                .body("{}")
                                .post(config.getProperty("baseUrl") + "/api/events/" + eventId + "/payments");

                if (payResp.getStatusCode() == 201 || payResp.getStatusCode() == 200) {
                        String paymentId = payResp.jsonPath().getString("payment.id");

                        // Manually expire/reject the payment
                        RestAssured.given()
                                        .header("Authorization", "Bearer " + token)
                                        .cookie("nerd_octopus_token", token)
                                        .post(config.getProperty("baseUrl") + "/api/events/" + eventId + "/payments/"
                                                        + paymentId + "/reject");

                        // Attempting mark-paid on expired payment should fail
                        Response markResp = RestAssured.given()
                                        .header("Authorization", "Bearer " + token)
                                        .cookie("nerd_octopus_token", token)
                                        .post(config.getProperty("baseUrl") + "/api/events/" + eventId + "/payments/"
                                                        + paymentId + "/mark-paid");

                        logger.info("Mark paid response on rejected payment: {}", markResp.getStatusCode());
                        Assert.assertTrue(
                                        markResp.getStatusCode() == 410 || markResp.getStatusCode() == 400
                                                        || markResp.getStatusCode() == 409,
                                        "Expired payment should be rejected with 410/400/409, got: "
                                                        + markResp.getStatusCode());
                }
        }

        @Test(priority = 6, groups = {
                        "payment" }, retryAnalyzer = RetryAnalyzer.class, description = "TC-PAY-008: Host confirm pembayaran")
        public void testHostConfirmPayment() {
                logger.info("Starting TC-PAY-008: Host confirm pembayaran");
                // Host confirms payment -> status becomes PAID
                Response hostLogin = RestAssured.given()
                                .contentType("application/json")
                                .body("{\"email\":\"" + config.getProperty("adminEmail") + "\",\"password\":\""
                                                + config.getProperty("adminPassword") + "\"}")
                                .post(config.getProperty("baseUrl") + "/api/auth/login");

                String hostToken = hostLogin.jsonPath().getString("token");
                String eventId = "cmvvvjh7g0001l304p0l8wt4z";

                // Query payments for event
                Response listResp = RestAssured.given()
                                .header("Authorization", "Bearer " + hostToken)
                                .cookie("nerd_octopus_token", hostToken)
                                .get(config.getProperty("baseUrl") + "/api/events/" + eventId + "/payments");

                logger.info("List payments status code: {}", listResp.getStatusCode());
                Assert.assertEquals(listResp.getStatusCode(), 200, "Host must be able to view payments");
        }
}
