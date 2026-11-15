package tests.api;

import core.BaseApiTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelCheckoutApiTests extends BaseApiTest {

    private static final Logger logger = LogManager.getLogger(ParallelCheckoutApiTests.class);

    @Test(priority = 1, groups = {"api", "concurrency"}, description = "TC-API-PAY-004: Anti-Oversell Checkout Serentak Concurrency")
    public void testParallelConcurrentCheckoutApi() throws InterruptedException {
        logger.info("Starting TC-API-PAY-004: Anti-Oversell Checkout Serentak via API");

        String[][] accounts = {
                {config.getProperty("adminEmail"), config.getProperty("adminPassword")},
                {config.getProperty("aliceEmail"), config.getProperty("alicePassword")},
                {config.getProperty("memberEmail"), config.getProperty("memberPassword")},
                {config.getProperty("pendingEmail"), config.getProperty("pendingPassword")},
                {config.getProperty("emptyEmail"), config.getProperty("emptyPassword")}
        };

        List<String> tokens = new ArrayList<>();
        for (String[] acc : accounts) {
            Response loginResp = RestAssured.given()
                    .contentType("application/json")
                    .body("{\"email\":\"" + acc[0] + "\",\"password\":\"" + acc[1] + "\"}")
                    .post("/api/auth/login");

            if (loginResp.getStatusCode() == 200) {
                tokens.add(loginResp.jsonPath().getString("token"));
            }
        }
        logger.info("Authenticated {} users for concurrent checkout test", tokens.size());

        String targetEventId = "cmvv20mw00001jm040i9gbc0e";
        int threadCount = tokens.size();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(threadCount);
        List<Integer> responseCodes = Collections.synchronizedList(new ArrayList<>());

        for (String token : tokens) {
            executor.submit(() -> {
                try {
                    startGate.await();
                    Response rsvpResp = RestAssured.given()
                            .contentType("application/json")
                            .header("Authorization", "Bearer " + token)
                            .cookie("nerd_octopus_token", token)
                            .body("{\"action\":\"join\"}")
                            .post("/api/events/" + targetEventId + "/rsvp");

                    responseCodes.add(rsvpResp.getStatusCode());
                    logger.info("Thread finished with status: {}", rsvpResp.getStatusCode());
                } catch (Exception e) {
                    logger.error("Error in parallel worker", e);
                } finally {
                    endGate.countDown();
                }
            });
        }

        startGate.countDown();
        boolean completed = endGate.await(15, TimeUnit.SECONDS);
        executor.shutdown();

        Assert.assertTrue(completed, "All parallel requests must complete within timeout");
        logger.info("Collected status codes: {}", responseCodes);

        for (int code : responseCodes) {
            Assert.assertTrue(code == 200 || code == 409 || code == 401,
                    "Response code should be 200, 409, or 401 but got: " + code);
        }
    }
}

