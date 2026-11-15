package tests.api;

import core.BaseApiTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AuthApiTests extends BaseApiTest {

    private static final Logger logger = LogManager.getLogger(AuthApiTests.class);

    @Test(priority = 1, groups = {"api", "auth"}, description = "TC-API-AUTH-001: Login dengan kredensial valid")
    public void testApiLoginValidCredentials() {
        logger.info("Starting TC-API-AUTH-001: Login dengan kredensial valid via API");
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"email\":\"" + config.getProperty("adminEmail") + "\",\"password\":\"" + config.getProperty("adminPassword") + "\"}")
                .post("/api/auth/login");

        logger.info("Login API status code: {}", response.getStatusCode());
        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code 200 for valid login");

        String token = response.jsonPath().getString("token");
        Assert.assertNotNull(token, "Response must contain auth token");
        Assert.assertFalse(token.trim().isEmpty(), "Token must not be empty");
    }

    @Test(priority = 2, groups = {"api", "auth"}, description = "TC-API-AUTH-002: Login dengan kredensial salah")
    public void testApiLoginInvalidCredentials() {
        logger.info("Starting TC-API-AUTH-002: Login dengan kredensial invalid via API");
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"email\":\"" + config.getProperty("adminEmail") + "\",\"password\":\"wrongpassword123\"}")
                .post("/api/auth/login");

        logger.info("Invalid login API status code: {}", response.getStatusCode());
        Assert.assertEquals(response.getStatusCode(), 401, "Expected status code 401 for invalid credentials");
    }

    @Test(priority = 3, groups = {"api", "auth"}, description = "TC-API-AUTH-003: Registrasi dengan email duplikat")
    public void testApiRegisterDuplicateEmail() {
        logger.info("Starting TC-API-AUTH-003: Registrasi dengan email duplikat via API");
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"name\":\"Admin Test\",\"email\":\"" + config.getProperty("adminEmail") + "\",\"password\":\"Password123!\"}")
                .post("/api/auth/register");

        logger.info("Duplicate register API status code: {}", response.getStatusCode());
        Assert.assertEquals(response.getStatusCode(), 409, "Expected status code 409 for duplicate email");
    }
}

