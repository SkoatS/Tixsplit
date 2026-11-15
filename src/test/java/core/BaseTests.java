package core;

import java.util.Properties;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

public class BaseTests {

    protected static Properties config;

    @BeforeSuite(alwaysRun = true)
    public void loadConfig() {
        String env = System.getProperty("env");
        env = (env == null || env.trim().isEmpty()) ? "staging" : env;
        config = ConfigManager.loadProperties(env);
    }

    @BeforeMethod(alwaysRun = true)
    @Parameters("browser")
    public void setUp(@Optional("chrome") String browser) {
        DriverManager.initDriver(browser);
        WebDriver driver = DriverManager.getDriver();
        driver.manage().window().maximize();
        driver.get(config.getProperty("baseUrl"));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverManager.quitDriver();
    }

    public WebDriver getDriver() {
        return DriverManager.getDriver();
    }
}
