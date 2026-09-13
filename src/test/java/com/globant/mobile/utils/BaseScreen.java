package com.globant.mobile.utils;

import com.globant.mobile.config.ConfigReader;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebElement;
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

    private static final int TAP_PRESS_DURATION_MILLIS = 120;
    private static final int TAP_MOVE_DURATION_MILLIS = 40;
    private static final int TAP_MICRO_MOVE_PIXELS = 2;
    private static final long SCROLL_SETTLE_MILLIS = 1000;
    private static final double SWIPE_NEAR_EDGE_RATIO = 0.2;
    private static final double SWIPE_FAR_EDGE_RATIO = 0.8;
    private static final int SWIPE_DURATION_MILLIS = 1000;
    private static final int FLING_SPEED = 2000;

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
     * Variante de scrollToDescriptionLocator para elementos que solo se pueden
     * identificar por su texto.
     */
    protected By scrollToTextLocator(String text) {
        return androidUiAutomator(String.format(
                "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().text(\"%s\"))",
                text
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

    protected void waitForScrollToSettle() {
        try {
            Thread.sleep(SCROLL_SETTLE_MILLIS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Verifica si un elemento esta presente en el arbol, sin esperar. Util en
     * bucles donde la ausencia es el caso esperado en la mayoria de las
     * iteraciones y aplicar el timeout completo cada vez seria muy costoso.
     */
    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    /**
     * Espera a que un elemento deje de estar presente/visible. A diferencia
     * de negar isDisplayed(), esta condicion resuelve apenas el elemento
     * desaparece en vez de agotar el timeout completo.
     */
    protected boolean waitUntilNotDisplayed(By locator) {
        try {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Ejecuta un swipe dentro de los bounds reales del elemento indicado,
     * calculados en runtime (la posicion de los componentes cambia segun el
     * scroll de la pantalla, por lo que coordenadas fijas no son confiables).
     * <p>
     * Usa W3C Actions en vez de "mobile: swipeGesture" por consistencia con
     * tap(): es el mecanismo que resulto confiable con los componentes de
     * React Native de esta app.
     */
    protected void swipeOnElement(By locator, Direction direction) {
        Rectangle rect = waitForVisibility(locator).getRect();

        int startX;
        int startY;
        int endX;
        int endY;

        switch (direction) {
            case LEFT -> {
                startY = endY = rect.getY() + rect.getHeight() / 2;
                startX = rect.getX() + (int) (rect.getWidth() * SWIPE_FAR_EDGE_RATIO);
                endX = rect.getX() + (int) (rect.getWidth() * SWIPE_NEAR_EDGE_RATIO);
            }
            case RIGHT -> {
                startY = endY = rect.getY() + rect.getHeight() / 2;
                startX = rect.getX() + (int) (rect.getWidth() * SWIPE_NEAR_EDGE_RATIO);
                endX = rect.getX() + (int) (rect.getWidth() * SWIPE_FAR_EDGE_RATIO);
            }
            case UP -> {
                startX = endX = rect.getX() + rect.getWidth() / 2;
                startY = rect.getY() + (int) (rect.getHeight() * SWIPE_FAR_EDGE_RATIO);
                endY = rect.getY() + (int) (rect.getHeight() * SWIPE_NEAR_EDGE_RATIO);
            }
            case DOWN -> {
                startX = endX = rect.getX() + rect.getWidth() / 2;
                startY = rect.getY() + (int) (rect.getHeight() * SWIPE_NEAR_EDGE_RATIO);
                endY = rect.getY() + (int) (rect.getHeight() * SWIPE_FAR_EDGE_RATIO);
            }
            default -> throw new IllegalArgumentException("Unsupported swipe direction: " + direction);
        }

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipeSequence = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(
                        Duration.ofMillis(SWIPE_DURATION_MILLIS),
                        PointerInput.Origin.viewport(),
                        endX, endY))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(swipeSequence));
        waitForScrollToSettle();
    }

    /**
     * Ejecuta un fling sobre el elemento indicado.
     * <p>
     * A diferencia de swipeOnElement (un arrastre de A a B), el fling imprime
     * velocidad al gesto, que es lo que un carrusel con snap necesita para
     * completar la transicion a la pagina siguiente. Un arrastre por
     * coordenadas deja el resultado librado a donde termine el dedo virtual:
     * segun el caso la app completa el cambio de card o lo revierte, lo que
     * vuelve al test inestable entre corridas.
     */
    protected void flingOnElement(By locator, Direction direction) {
        WebElement element = waitForVisibility(locator);

        Map<String, Object> params = new HashMap<>();
        params.put("elementId", ((RemoteWebElement) element).getId());
        params.put("direction", direction.name().toLowerCase());
        params.put("speed", FLING_SPEED);

        driver.executeScript("mobile: flingGesture", params);
        waitForScrollToSettle();
    }

    /** Devuelve los bounds reales del elemento, para calcular gestos en runtime. */
    protected Rectangle getRect(By locator) {
        return waitForVisibility(locator).getRect();
    }

    /**
     * Arrastra verticalmente entre dos coordenadas sobre la misma columna.
     * Permite acotar el gesto a una franja libre de componentes que capturen
     * el toque (ej. un carrusel, que solo responde en horizontal y no propaga
     * el desplazamiento vertical a su contenedor).
     */
    protected void swipeVerticallyBetween(int x, int startY, int endY) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipeSequence = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, startY))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(
                        Duration.ofMillis(SWIPE_DURATION_MILLIS),
                        PointerInput.Origin.viewport(),
                        x, endY))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(swipeSequence));
        waitForScrollToSettle();
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}