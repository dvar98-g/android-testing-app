package com.globant.mobile.screens.common;

import com.globant.mobile.utils.BaseScreen;
import io.appium.java_client.android.AndroidDriver;

/**
 * Representa la bottom tab bar de la app.
 * <p>
 * La tab bar es dinamica: siempre tiene Home a la izquierda y Menu a la derecha,
 * pero los slots del medio dependen de que pantallas se hayan activado desde el
 * panel lateral de Menu (incluyendo Permissions y Data Management, que no estan
 * ahi por defecto). Por eso los metodos se parametrizan por nombre de pantalla
 * en vez de exponer un metodo fijo por cada tab.
 */
public class BottomTabBarComponent extends BaseScreen {

    public BottomTabBarComponent(AndroidDriver driver) {
        super(driver);
    }

    public void tapTab(String screenName) {
        tap(descriptionLocator(screenName));
    }

    public boolean isTabVisible(String screenName) {
        return isDisplayed(descriptionLocator(screenName));
    }
}
