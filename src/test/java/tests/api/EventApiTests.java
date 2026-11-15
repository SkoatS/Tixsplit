package tests.api;

import core.BaseApiTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EventApiTests extends BaseApiTest {

    private static final Logger logger = LogManager.getLogger(EventApiTests.class);

    @Test(priority = 1, groups = {"api", "event"}, description = "TC-API-EVT-001: Verifikasi pengambilan detail event")
    public void testApiGetEventDetails() {
        logger.info("Starting TC-API-EVT-001: Verifikasi detail event via API");
        String eventId = "cmvv20mw00001jm040i9gbc0e";

        Response response = RestAssured.given()
                .get("/api/events/" + eventId);

        logger.info("Get event details response code: {}", response.getStatusCode());
        Assert.assertEquals(response.getStatusCode(), 200, "Event details must return 200");
        Assert.assertNotNull(response.jsonPath().getString("event.name"), "Event name must be present in response");
    }

    @Test(priority = 2, groups = {"api", "event"}, description = "TC-API-EVT-002: RSVP Event Gratis via API")
    public void testApiRsvpFreeEvent() {
        logger.info("Starting TC-API-EVT-002: RSVP Event Gratis via API");
        String token = getMemberToken();
        String freeEventId = "cmvv20mw00001jm040i9gbc0e";

        Response response = RestAssured.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .cookie("nerd_octopus_token", token)
                .body("{\"action\":\"join\"}")
                .post("/api/events/" + freeEventId + "/rsvp");

        logger.info("RSVP Free Event status: {}", response.getStatusCode());
        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 409,
                "RSVP should return 200 (Joined) or 409 (Already joined/Full)");
    }
}

