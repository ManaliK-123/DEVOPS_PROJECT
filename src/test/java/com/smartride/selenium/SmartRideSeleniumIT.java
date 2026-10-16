package com.smartride.selenium;

import com.smartride.service.DataSeederService;
import com.smartride.service.RideService;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SmartRideSeleniumIT {

    @LocalServerPort
    private int port;

    @Autowired
    private RideService rideService;

    @Autowired
    private DataSeederService dataSeederService;

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;
    private static final String SCREENSHOTS_DIR = "target/selenium-screenshots";

    @BeforeAll
    static void setupClass() {
        new File(SCREENSHOTS_DIR).mkdirs();
        try {
            WebDriverManager.chromedriver().setup();
        } catch (Exception e) {
            System.err.println("WebDriverManager auto-setup notice: " + e.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        rideService.resetToSeedData();
        dataSeederService.seedInitialData();

        baseUrl = "http://localhost:" + port;

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        try {
            driver = new ChromeDriver(options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        } catch (Exception e) {
            System.err.println("Chrome driver initialization skipped (No Chrome in environment): " + e.getMessage());
            Assumptions.assumeTrue(false, "ChromeDriver not available in host environment");
        }
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        if (driver != null) {
            try {
                captureScreenshot(testInfo.getDisplayName().replaceAll("[^a-zA-Z0-9.-]", "_"));
            } catch (Exception ignored) {}
            driver.quit();
        }
    }

    private void captureScreenshot(String testName) {
        try {
            if (driver instanceof TakesScreenshot takesScreenshot) {
                File src = takesScreenshot.getScreenshotAs(OutputType.FILE);
                Path destDir = Paths.get(SCREENSHOTS_DIR);
                Files.createDirectories(destDir);
                Path dest = destDir.resolve(testName + ".png");
                Files.copy(src.toPath(), dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Screenshot captured: " + dest.toAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
        }
    }

    @Test
    @Order(1)
    @DisplayName("Journey 1: Landing Page Verification and Brand Header")
    void testLandingPageTitleAndBrand() {
        driver.get(baseUrl);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("brandLogo")));

        String pageTitle = driver.getTitle();
        assertThat(pageTitle).contains("SmartRide");

        WebElement healthBadge = driver.findElement(By.id("healthStatusText"));
        assertThat(healthBadge.getText()).contains("SYSTEM ACTIVE");
    }

    @Test
    @Order(2)
    @DisplayName("Journey 2: Passenger Ride Booking and Immediate Listing")
    void testPassengerRideBooking() {
        driver.get(baseUrl);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("rideBookingForm")));

        driver.findElement(By.id("passengerName")).sendKeys("Rachel Zane");
        driver.findElement(By.id("passengerPhone")).sendKeys("+1-555-7766");
        driver.findElement(By.id("sourceLocation")).sendKeys("Pearson Hardman Tower");
        driver.findElement(By.id("destinationLocation")).sendKeys("Federal District Court");

        Select vehicleSelect = new Select(driver.findElement(By.id("vehicleType")));
        vehicleSelect.selectByValue("SEDAN");

        Select seatsSelect = new Select(driver.findElement(By.id("requestedSeats")));
        seatsSelect.selectByValue("2");

        WebElement submitBtn = driver.findElement(By.id("btnSubmitRide"));
        submitBtn.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("toastContainer")));
        WebElement feed = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("passengerRidesList")));

        wait.until(d -> feed.getText().contains("Pearson Hardman Tower"));
        assertThat(feed.getText()).contains("Pearson Hardman Tower");
    }

    @Test
    @Order(3)
    @DisplayName("Journey 3: Driver Portal Switching and Ride Acceptance Workflow")
    void testDriverAcceptanceWorkflow() {
        driver.get(baseUrl);

        // Switch to Driver Tab
        WebElement driverTab = wait.until(ExpectedConditions.elementToBeClickable(By.id("tabDriver")));
        driverTab.click();

        WebElement openFeed = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("driverAvailableList")));
        wait.until(d -> openFeed.findElements(By.tagName("button")).size() > 0);

        List<WebElement> acceptButtons = openFeed.findElements(By.xpath(".//button[contains(text(),'Accept Ride')]"));
        assertThat(acceptButtons).isNotEmpty();

        acceptButtons.get(0).click();

        // Verify Active dispatch section contains updated trip
        WebElement activeFeed = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("driverActiveList")));
        wait.until(d -> activeFeed.getText().contains("ACCEPTED") || activeFeed.getText().contains("Ongoing"));
        assertThat(activeFeed.getText()).isNotEmpty();
    }

    @Test
    @Order(4)
    @DisplayName("Journey 4: Admin Analytics Dashboard and Search Filter")
    void testAdminDashboardMetricsAndSearch() {
        driver.get(baseUrl);

        WebElement adminTab = wait.until(ExpectedConditions.elementToBeClickable(By.id("tabAdmin")));
        adminTab.click();

        WebElement totalCard = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("metricTotalRides")));
        wait.until(d -> !totalCard.getText().equals("0"));
        assertThat(Integer.parseInt(totalCard.getText())).isGreaterThanOrEqualTo(5);

        // Test Filter
        WebElement searchInput = driver.findElement(By.id("adminSearchInput"));
        searchInput.sendKeys("Airport");

        WebElement tableBody = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("adminRidesTableBody")));
        wait.until(d -> tableBody.getText().contains("Airport"));
        assertThat(tableBody.getText()).contains("Airport");
    }
}
