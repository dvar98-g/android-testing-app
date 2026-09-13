package com.globant.mobile.screens.drag;


import com.globant.mobile.utils.BaseScreen;
import io.appium.java_client.android.AndroidDriver;

/**
 * Representa la pantalla Drag.
 * <p>
 * Alcance acotado a lo necesario para el escenario de navegacion: confirmar
 * que la pantalla cargo y que el boton "renew" esta visible. Las 9 drop-zones
 * y 9 drag-items del puzzle quedan deliberadamente fuera de este alcance --
 * corresponden a un futuro test funcional dedicado del modulo Drag.
 */
public class DragScreen extends BaseScreen {

    private static final String SCREEN_MARKER = "Drag-drop-screen";
    private static final String BUTTON_RENEW = "renew";

    public DragScreen(AndroidDriver driver) {
        super(driver);
    }

    public boolean isScreenDisplayed() {
        return isDisplayed(descriptionLocator(SCREEN_MARKER));
    }

    public boolean isRenewButtonDisplayed() {
        return isDisplayed(descriptionLocator(BUTTON_RENEW));
    }
}