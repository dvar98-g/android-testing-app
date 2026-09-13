package com.globant.mobile.screens.common;

import com.globant.mobile.utils.BaseScreen;
import io.appium.java_client.android.AndroidDriver;

/**
 * Representa un AlertDialog nativo de Android (usado por Login y Sign Up
 * para mostrar el resultado de la operacion, pero no exclusivo de ellos --
 * cualquier flujo futuro que dispare este mismo tipo de alert puede
 * reutilizarlo).
 * <p>
 * No conoce de antemano que titulo/mensaje esperar -- solo expone lecturas.
 * Las comparaciones contra el texto esperado viven en la clase de test.
 */
public class NativeAlertComponent extends BaseScreen {

    private static final String TITLE_RESOURCE_ID = "com.wdiodemoapp:id/alert_title";
    private static final String MESSAGE_RESOURCE_ID = "android:id/message";
    private static final String OK_BUTTON_RESOURCE_ID = "android:id/button1";

    public NativeAlertComponent(AndroidDriver driver) {
        super(driver);
    }

    public String getTitle() {
        return waitForVisibility(resourceIdLocator(TITLE_RESOURCE_ID)).getText();
    }

    public String getMessage() {
        return waitForVisibility(resourceIdLocator(MESSAGE_RESOURCE_ID)).getText();
    }

    public void tapOk() {
        tap(resourceIdLocator(OK_BUTTON_RESOURCE_ID));
    }
}
