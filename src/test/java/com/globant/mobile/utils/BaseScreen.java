package com.globant.mobile.utils;

import com.globant.mobile.config.ConfigReader;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Base para todos los Screens (Page Objects de mobile).
 * <p>
 * El driver se recibe por constructor (composicion), no se hereda de BaseTest.
 * Centraliza esperas explicitas, interacciones basicas y gestos, para que los
 * Screens concretos solo declaren locators y flujos de pantalla.
 */
public abstract class BaseScreen {

    private static final double GESTURE_AREA_MARGIN_RATIO = 0.1;
    private static final double GESTURE_AREA_SIZE_RATIO = 0.8;
    private static final double GESTURE_PERCENT = 0.75;

    protected final AndroidDriver driver;
    protected final WebDriverWait wait;

    protected BaseScreen(AndroidDriver driver) {
        this.driver = driver;
        ConfigReader config = new ConfigReader();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(config.getExplicitWaitSeconds()));
    }

    protected WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void tap(By locator) {
        waitForClickable(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement element = waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected boolean isDisplayed(By locator) {
        try {
            return waitForVisibility(locator).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Ejecuta un swipe sobre un area central de la pantalla (evita bordes,
     * donde algunos gestos del sistema pueden interferir).
     */
    protected void swipe(Direction direction) {
        Dimension size = driver.manage().window().getSize();

        Map<String, Object> params = new HashMap<>();
        params.put("left", (int) (size.width * GESTURE_AREA_MARGIN_RATIO));
        params.put("top", (int) (size.height * GESTURE_AREA_MARGIN_RATIO));
        params.put("width", (int) (size.width * GESTURE_AREA_SIZE_RATIO));
        params.put("height", (int) (size.height * GESTURE_AREA_SIZE_RATIO));
        params.put("direction", direction.name().toLowerCase());
        params.put("percent", GESTURE_PERCENT);

        driver.executeScript("mobile: swipeGesture", params);
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}