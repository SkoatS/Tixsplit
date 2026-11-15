package tests.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import core.BaseApiTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PaymentApiTests extends BaseApiTest {

        private static final Logger logger = LogManager.getLogger(PaymentApiTests.class);

        @Test(priority = 1, groups = { "api",
                        "payment" }, description = "TC-API-PAY-001: Kalkulasi Admin Fee 5% dan 3 Digit Kode Unik")
        public void testApiPaymentFeeCalculation() {
                logger.info("Starting TC-API-PAY-001: Kalkulasi Admin Fee 5% dan 3 Digit Kode Unik via API");
                String token = getAuthToken(config.getProperty("aliceEmail"), config.getProperty("alicePassword"));
                String eventId = "cmvvvjh7g0001l304p0l8wt4z";

                Response payResp = RestAssured.given()
                                .contentType("application/json")
                                .header("Authorization", "Bearer " + token)
                                .cookie("nerd_octopus_token", token)
                                .body("{}")
                                .post("/api/events/" + eventId + "/payments");

                logger.info("Payment API response code: {}", payResp.getStatusCode());
                Assert.assertTrue(
                                payResp.getStatusCode() == 200 || payResp.getStatusCode() == 201
                                                || payResp.getStatusCode() == 409,
                                "Payment intent creation should return 200, 201, or 409");

                JsonObject json = JsonParser.parseString(payResp.getBody().asString()).getAsJsonObject();
                JsonObject payment = json.getAsJsonObject("payment");
                Assert.assertNotNull(payment, "Payment object must be returned in response");

                int baseAmount = payment.get("baseAmount").getAsInt();
                int adminFee = payment.get("adminFee").getAsInt();
                int uniqueCode = payment.get("uniqueCode").getAsInt();

                logger.info("Base: {}, Fee: {}, Code: {}", baseAmount, adminFee, uniqueCode);

                // Verify 5% admin fee formula
                int expectedAdminFee = (int) Math.round(baseAmount * 0.05);
                Assert.assertEquals(adminFee, expectedAdminFee, "Admin fee must be exactly 5% of base amount");

                // Verify unique code is 3 digits (1 - 999)
                Assert.assertTrue(uniqueCode >= 1 && uniqueCode <= 999,
                                "Unique code must be within 1-999 range");

                // Cleanup: reject payment intent if PENDING
                if ("PENDING".equalsIgnoreCase(payment.get("status").getAsString())) {
                        String paymentId = payment.get("id").getAsString();
                        RestAssured.given()
                                        .header("Authorization", "Bearer " + token)
                                        .cookie("nerd_octopus_token", token)
                                        .post("/api/events/" + eventId + "/payments/" + paymentId + "/reject");
                }
        }

        @Test(priority = 2, groups = { "api",
                        "payment" }, description = "TC-API-PAY-002: Penolakan Pembayaran Kadaluarsa / Rejected")
        public void testApiExpiredPaymentHandling() {
                logger.info("Starting TC-API-PAY-002: Penolakan Pembayaran Kadaluarsa / Rejected via API");
                String token = getAdminToken();
                String eventId = "cmvvvjh7g0001l304p0l8wt4z";

                Response payResp = RestAssured.given()
                                .contentType("application/json")
                                .header("Authorization", "Bearer " + token)
                                .cookie("nerd_octopus_token", token)
                                .body("{}")
                                .post("/api/events/" + eventId + "/payments");

                if (payResp.getStatusCode() == 200 || payResp.getStatusCode() == 201) {
                        String paymentId = payResp.jsonPath().getString("payment.id");

                        // Reject payment intent to simulate expired state
                        RestAssured.given()
                                        .header("Authorization", "Bearer " + token)
                                        .cookie("nerd_octopus_token", token)
                                        .post("/api/events/" + eventId + "/payments/" + paymentId + "/reject");

                        // Attempting mark-paid on rejected/expired payment must fail
                        Response markResp = RestAssured.given()
                                        .header("Authorization", "Bearer " + token)
                                        .cookie("nerd_octopus_token", token)
                                        .post("/api/events/" + eventId + "/payments/" + paymentId + "/mark-paid");

                        logger.info("Mark paid response on rejected payment: {}", markResp.getStatusCode());
                        Assert.assertTrue(
                                        markResp.getStatusCode() == 410 || markResp.getStatusCode() == 400
                                                        || markResp.getStatusCode() == 409,
                                        "Expected rejected payment to return 410, 400, or 409 on mark-paid");
                }
        }

        @Test(priority = 3, groups = { "api",
                        "payment" }, description = "TC-API-PAY-003: Host dapat melihat daftar transaksi payment event")
        public void testApiHostListViewPayments() {
                logger.info("Starting TC-API-PAY-003: Host dapat melihat daftar transaksi payment via API");
                String token = getAdminToken();
                String eventId = "cmvvvjh7g0001l304p0l8wt4z";

                Response listResp = RestAssured.given()
                                .header("Authorization", "Bearer " + token)
                                .cookie("nerd_octopus_token", token)
                                .get("/api/events/" + eventId + "/payments");

                logger.info("List payments status code: {}", listResp.getStatusCode());
                Assert.assertEquals(listResp.getStatusCode(), 200, "Host must be authorized to query payments list");
        }
}
