package core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class DriverManager {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static void initDriver(String browser) {
        if (browser == null || browser.trim().isEmpty()) {
            browser = "chrome";
        }

        WebDriver webDriver;
        boolean isCI = "true".equalsIgnoreCase(System.getenv("GITHUB_ACTIONS"));
        boolean isHeadless = isCI || "true".equalsIgnoreCase(System.getProperty("headless"));

        switch (browser.toLowerCase()) {
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (isHeadless) {
                    firefoxOptions.addArguments("-headless");
                }
                webDriver = new FirefoxDriver(firefoxOptions);
            }
            case "edge" -> {
                try {
                    WebDriverManager.edgedriver().setup();
                } catch (Exception e) {
                    // Fall back to Selenium 4 built-in Selenium Manager
                }
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--disable-blink-features=AutomationControlled");
                if (isHeadless) {
                    edgeOptions.addArguments("--headless=new");
                    edgeOptions.addArguments("--window-size=1920,1080");
                    edgeOptions.addArguments("--disable-gpu");
                }
                webDriver = new EdgeDriver(edgeOptions);
            }
            default -> { // chrome
                if (!isCI) {
                    WebDriverManager.chromedriver().setup();
                } else {
                    String chromeDriverPath = System.getenv("CHROMEDRIVER_PATH");
                    if (chromeDriverPath != null && !chromeDriverPath.isEmpty()) {
                        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
                    }
                }

                ChromeOptions options = new ChromeOptions();
                options.addArguments("--disable-blink-features=AutomationControlled");
                options.addArguments("--disable-extensions");
                options.addArguments("--disable-plugins");
                options.setExperimentalOption("useAutomationExtension", false);
                options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

                if (isCI) {
                    String chromeBin = System.getenv("CHROME_BIN");
                    if (chromeBin != null && !chromeBin.isEmpty()) {
                        options.setBinary(chromeBin);
                    }
                    options.addArguments("--headless=new");
                    options.addArguments("--disable-gpu");
                    options.addArguments("--window-size=1920,1080");
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                    options.addArguments("--remote-allow-origins=*");
                } else if (isHeadless) {
                    options.addArguments("--headless=new");
                    options.addArguments("--window-size=1920,1080");
                    options.addArguments("--disable-gpu");
                    options.addArguments("--no-sandbox");
                    options.addArguments("--remote-allow-origins=*");
                } else {
                    options.addArguments("--remote-allow-origins=*");
                }

                webDriver = new ChromeDriver(options);
            }
        }

        driver.set(webDriver);
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            try {
                driver.get().quit();
            } finally {
                driver.remove();
            }
        }
    }

}

