package core;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeSuite;

import java.util.Properties;

public class BaseApiTest {

    protected static Properties config;

    @BeforeSuite(alwaysRun = true)
    public void setUpApiSuite() {
        String env = System.getProperty("env");
        env = (env == null || env.trim().isEmpty()) ? "staging" : env;
        config = ConfigManager.loadProperties(env);
        RestAssured.baseURI = config.getProperty("baseUrl");
    }

    /**
     * Helper to authenticate and return bearer token
     */
    protected String getAuthToken(String email, String password) {
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")
                .post("/api/auth/login");

        if (response.getStatusCode() == 200) {
            return response.jsonPath().getString("token");
        }
        throw new IllegalStateException("Failed to authenticate API user: " + response.getBody().asString());
    }

    protected String getAdminToken() {
        return getAuthToken(config.getProperty("adminEmail"), config.getProperty("adminPassword"));
    }

    protected String getMemberToken() {
        return getAuthToken(config.getProperty("memberEmail"), config.getProperty("memberPassword"));
    }
}

