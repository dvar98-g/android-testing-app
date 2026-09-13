package com.globant.mobile.utils;

import com.globant.mobile.config.ConfigReader;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Collections;
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
    private static final int TAP_PRESS_DURATION_MILLIS = 120;
    private static final int TAP_MOVE_DURATION_MILLIS = 40;
    private static final int TAP_MICRO_MOVE_PIXELS = 2;
    private static final long SCROLL_SETTLE_MILLIS = 1000;

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

    /**
     * Toca un elemento usando W3C Actions sobre el centro de sus bounds.
     * <p>
     * El gesto incluye deliberadamente un micro-movimiento y una presion
     * prolongada: el sistema de responders de React Native puede descartar
     * un toque perfectamente estatico e instantaneo (se verifico que un
     * toque humano real en las mismas coordenadas si dispara el onPress,
     * mientras que click(), "mobile: clickGesture" y un tap W3C plano no).
     */
    protected void tap(By locator) {
        WebElement element = waitForClickable(locator);
        Rectangle rect = element.getRect();
        int centerX = rect.getX() + rect.getWidth() / 2;
        int centerY = rect.getY() + rect.getHeight() / 2;

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tapSequence = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX, centerY))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(new Pause(finger, Duration.ofMillis(TAP_PRESS_DURATION_MILLIS)))
                .addAction(finger.createPointerMove(
                        Duration.ofMillis(TAP_MOVE_DURATION_MILLIS),
                        PointerInput.Origin.viewport(),
                        centerX + TAP_MICRO_MOVE_PIXELS,
                        centerY))
                .addAction(new Pause(finger, Duration.ofMillis(TAP_PRESS_DURATION_MILLIS)))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(tapSequence));
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
     * Construye un locator a partir de una expresion UiSelector de UiAutomator2
     * (ej. "new UiSelector().description(\"foo\")"). Centraliza esta construccion
     * para que los Screens concretos no dupliquen el mismo wrapper.
     */
    protected By androidUiAutomator(String uiSelectorExpression) {
        return AppiumBy.androidUIAutomator(uiSelectorExpression);
    }

    /**
     * Shortcut para el patron mas repetido entre Screens: localizar por
     * content-desc exacto via UiSelector().description(...).
     */
    protected By descriptionLocator(String description) {
        return androidUiAutomator(String.format("new UiSelector().description(\"%s\")", description));
    }

    /**
     * Localiza un elemento por content-desc exacto, scrolleando automaticamente
     * dentro del contenedor scrollable mas cercano si no esta inicialmente
     * visible en el viewport (UiScrollable.scrollIntoView). Usar solo cuando
     * se sabe que el elemento puede quedar fuera del area visible sin scroll
     * manual -- para elementos siempre visibles, descriptionLocator alcanza.
     */
    protected By scrollToDescriptionLocator(String description) {
        return androidUiAutomator(String.format(
                "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().description(\"%s\"))",
                description
        ));
    }

    /**
     * Shortcut para localizar por resource-id exacto via UiSelector().resourceId(...).
     * Util para elementos sin content-desc propio (ej. dialogs nativos del SO).
     */
    protected By resourceIdLocator(String resourceId) {
        return androidUiAutomator(String.format("new UiSelector().resourceId(\"%s\")", resourceId));
    }

    /**
     * Localiza por el atributo text exacto. Necesario cuando el elemento que
     * realmente responde al toque es el TextView hijo y no el ViewGroup
     * contenedor que lleva el content-desc.
     */
    protected By textLocator(String text) {
        return androidUiAutomator(String.format("new UiSelector().text(\"%s\")", text));
    }

    /**
     * Dispara el scroll hacia un elemento UNA sola vez (a diferencia de
     * scrollToDescriptionLocator, que al usarse dentro de un wait se
     * reevalua -- y por lo tanto re-scrollea -- en cada poll, pudiendo
     * generar una posicion inestable).
     * <p>
     * Tras el scroll espera a que la inercia (fling) del ScrollView se
     * asiente: el responder system de React Native puede capturar y cancelar
     * un toque que llega inmediatamente despues de un gesto de scroll, de
     * modo que el tap siguiente no alcanza al elemento destino.
     */
    protected void scrollToElement(String description) {
        driver.findElement(scrollToDescriptionLocator(description));
        waitForScrollToSettle();
    }

    private void waitForScrollToSettle() {
        try {
            Thread.sleep(SCROLL_SETTLE_MILLIS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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