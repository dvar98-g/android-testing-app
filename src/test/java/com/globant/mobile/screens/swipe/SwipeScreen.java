package com.globant.mobile.screens.swipe;


import com.globant.mobile.utils.BaseScreen;
import io.appium.java_client.android.AndroidDriver;

/**
 * Representa la pantalla Swipe.
 * <p>
 * Alcance acotado a lo necesario para el escenario de navegacion: confirmar
 * que la pantalla cargo y que el carousel esta visible. No cubre el flujo
 * funcional completo de swipe horizontal/vertical (eso corresponde a un test
 * dedicado del modulo Swipe).
 * <p>
 * A diferencia del resto de los elementos verificados en otras pantallas, el
 * carousel no tiene content-desc propio -- se identifica por su resource-id
 * ("Carousel"), por eso usa un locator distinto (resourceId en vez de
 * description).
 */
public class SwipeScreen extends BaseScreen {

    private static final String SCREEN_MARKER = "Swipe-screen";
    private static final String CAROUSEL_RESOURCE_ID = "Carousel";

    public SwipeScreen(AndroidDriver driver) {
        super(driver);
    }

    public boolean isScreenDisplayed() {
        return isDisplayed(descriptionLocator(SCREEN_MARKER));
    }

    public boolean isCarouselDisplayed() {
        return isDisplayed(resourceIdLocator(CAROUSEL_RESOURCE_ID));
    }
}
