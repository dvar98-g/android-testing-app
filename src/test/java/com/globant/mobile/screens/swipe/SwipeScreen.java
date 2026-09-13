package com.globant.mobile.screens.swipe;


import com.globant.mobile.utils.BaseScreen;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.Rectangle;

import java.util.List;

/**
 * Representa la pantalla Swipe: un carrusel horizontal de cards mas un texto
 * oculto que solo aparece al swipear verticalmente.
 * <p>
 * El carrusel esta virtualizado: mantiene solo dos items renderizados en el
 * arbol a la vez (el actual y el siguiente). Al avanzar, el card anterior
 * desaparece por completo del arbol -- no queda fuera del viewport. Por eso
 * "el card anterior quedo oculto" se verifica con ausencia del elemento y no
 * comparando posiciones.
 * <p>
 * Los gestos se calculan sobre los bounds reales de cada elemento en runtime:
 * la posicion vertical del carrusel varia segun el scroll de la pantalla, asi
 * que coordenadas fijas no serian confiables.
 */
public class SwipeScreen extends BaseScreen {

    public static final String CARD_FULLY_OPEN_SOURCE = "FULLY OPEN SOURCE";
    public static final String CARD_GREAT_COMMUNITY = "GREAT COMMUNITY";
    public static final String CARD_JS_FOUNDATION = "JS.FOUNDATION";
    public static final String CARD_SUPPORT_VIDEOS = "SUPPORT VIDEOS";
    public static final String CARD_EXTENDABLE = "EXTENDABLE";
    public static final String CARD_COMPATIBLE = "COMPATIBLE";
    public static final String HIDDEN_TEXT = "You found me!!!";

    /** Cards del carrusel en el orden en que aparecen al avanzar. */
    public static final List<String> CARDS_IN_ORDER = List.of(
            CARD_FULLY_OPEN_SOURCE,
            CARD_GREAT_COMMUNITY,
            CARD_JS_FOUNDATION,
            CARD_SUPPORT_VIDEOS,
            CARD_EXTENDABLE,
            CARD_COMPATIBLE
    );

    private static final String SCREEN_MARKER = "Swipe-screen";
    private static final String CAROUSEL_RESOURCE_ID = "Carousel";
    private static final String CAROUSEL_ITEM_RESOURCE_ID_PREFIX = "__CAROUSEL_ITEM_";
    private static final String CAROUSEL_ITEM_RESOURCE_ID_SUFFIX = "__";
    private static final int MAX_VERTICAL_SWIPES = 10;
    private static final int GESTURE_MARGIN_PIXELS = 25;

    public SwipeScreen(AndroidDriver driver) {
        super(driver);
    }

    public boolean isScreenDisplayed() {
        return isDisplayed(descriptionLocator(SCREEN_MARKER));
    }

    public boolean isCarouselDisplayed() {
        return isDisplayed(resourceIdLocator(CAROUSEL_RESOURCE_ID));
    }

    public boolean isCardDisplayed(String cardText) {
        return isDisplayed(textLocator(cardText));
    }

    public boolean isCardHidden(String cardText) {
        return waitUntilNotDisplayed(textLocator(cardText));
    }

    /**
     * Avanza un card en el carrusel. Usa fling (gesto con velocidad) en vez de
     * un arrastre por coordenadas: el carrusel tiene snap y solo completa la
     * transicion si el gesto supera cierta velocidad, por lo que un arrastre
     * resulta inestable entre corridas.
     */
    public void swipeToNextCard() {
        flingOnElement(resourceIdLocator(CAROUSEL_RESOURCE_ID), Direction.RIGHT);
    }

    /** Avanza hasta el ultimo card del carrusel. */
    public void swipeToLastCard() {
        for (int index = 1; index < CARDS_IN_ORDER.size(); index++) {
            swipeToNextCard();
        }
    }

    /**
     * Cuenta cuantos items tiene el carrusel en el arbol en este momento.
     * Al estar virtualizado, en el ultimo card deberia quedar uno solo.
     */
    public int getRenderedCarouselItemCount() {
        int count = 0;
        for (int index = 0; index < CARDS_IN_ORDER.size(); index++) {
            String resourceId = CAROUSEL_ITEM_RESOURCE_ID_PREFIX + index + CAROUSEL_ITEM_RESOURCE_ID_SUFFIX;
            if (!driver.findElements(resourceIdLocator(resourceId)).isEmpty()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Desplaza la pantalla verticalmente hasta encontrar el texto oculto, con
     * un limite de intentos para no quedar en un bucle infinito.
     * <p>
     * El gesto no puede arrancar sobre el carrusel: este solo responde en
     * horizontal y no propaga el desplazamiento vertical a su contenedor. Como
     * el carrusel se mueve a medida que la pagina scrollea (y la franja libre
     * por encima se agota), en cada intento se elige la franja libre mas
     * amplia -- arriba o abajo del carrusel -- calculada desde los bounds
     * reales de ambos elementos.
     */
    public void swipeVerticallyUntilHiddenTextIsFound() {
        for (int attempt = 0; attempt < MAX_VERTICAL_SWIPES; attempt++) {
            if (isPresent(textLocator(HIDDEN_TEXT))) {
                return;
            }
            swipeVerticallyAvoidingCarousel();
        }
    }

    private void swipeVerticallyAvoidingCarousel() {
        Rectangle screen = getRect(descriptionLocator(SCREEN_MARKER));
        Rectangle carousel = getRect(resourceIdLocator(CAROUSEL_RESOURCE_ID));

        int x = screen.getX() + screen.getWidth() / 2;
        int screenBottom = screen.getY() + screen.getHeight();
        int carouselBottom = carousel.getY() + carousel.getHeight();
        int carouselTop = carousel.getY();

        int spaceAbove = carouselTop - screen.getY();
        int spaceBelow = screenBottom - carouselBottom;

        int startY;
        int endY;
        if (spaceBelow >= spaceAbove) {
            // Franja libre debajo del carrusel: se arrastra desde abajo hacia
            // el borde inferior del carrusel.
            startY = screenBottom - GESTURE_MARGIN_PIXELS;
            endY = carouselBottom + GESTURE_MARGIN_PIXELS;
        } else {
            // Franja libre encima del carrusel.
            startY = carouselTop - GESTURE_MARGIN_PIXELS;
            endY = screen.getY() + GESTURE_MARGIN_PIXELS;
        }

        swipeVerticallyBetween(x, startY, endY);
    }

    public String getHiddenText() {
        return waitForVisibility(textLocator(HIDDEN_TEXT)).getText();
    }
}