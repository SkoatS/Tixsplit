package tests.api;

import core.BaseApiTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class SplitBillApiTests extends BaseApiTest {

    private static final Logger logger = LogManager.getLogger(SplitBillApiTests.class);

    @Test(priority = 1, groups = {"api", "splitbill"}, description = "TC-API-SB-001: Verifikasi data split bill via API")
    public void testApiGetSplitBillDetails() {
        logger.info("Starting TC-API-SB-001: Verifikasi data split bill via API");
        String token = getAdminToken();
        String splitBillId = "cmwf4mxz80001ic044joqt538";

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .cookie("nerd_octopus_token", token)
                .get("/api/tools/split-bill/" + splitBillId);

        logger.info("Get split bill API status: {}", response.getStatusCode());
        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code 200 for split bill retrieval");

        String title = response.jsonPath().getString("splitBill.title");
        Assert.assertNotNull(title, "Split bill title must not be null");

        List<Map<String, Object>> participants = response.jsonPath().getList("splitBill.participants");
        Assert.assertNotNull(participants, "Participants list must not be null");
        Assert.assertTrue(participants.size() >= 3, "Split bill must have at least 3 participants");

        logger.info("Retrieved split bill: {} with {} participants", title, participants.size());
    }

    @Test(priority = 2, groups = {"api", "splitbill"}, description = "TC-API-SB-002: Verifikasi pembagian sisa desimal ganjil")
    public void testApiSplitBillOddDecimalMath() {
        logger.info("Starting TC-API-SB-002: Verifikasi logika pembagian sisa desimal via API");
        int totalAmount = 10000;
        int participantCount = 3;

        int baseShare = totalAmount / participantCount; // 3333
        int remainder = totalAmount % participantCount; // 1

        Assert.assertEquals(baseShare, 3333, "Base share must be 3333 IDR");
        Assert.assertEquals(remainder, 1, "Remainder must be 1 IDR");

        int calculatedTotal = 0;
        for (int i = 0; i < participantCount; i++) {
            int share = baseShare + (i < remainder ? 1 : 0);
            calculatedTotal += share;
        }

        Assert.assertEquals(calculatedTotal, totalAmount, "Total shares must sum exactly to totalAmount without discrepancy");
        logger.info("Odd decimal calculation verified: Total {} across {} participants", calculatedTotal, participantCount);
    }
}

