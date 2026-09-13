package com.globant.mobile.screens.home;

import com.globant.mobile.utils.BaseScreen;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

/**
 * Representa la pantalla Home.
 * <p>
 * Ninguno de los elementos de contenido (titulo, subtitulo, imagen) tiene
 * content-desc ni resource-id propio -- unicamente el contenedor general
 * expone un accessibility id ("Home-screen"). Por eso titulo y subtitulo se
 * localizan por su texto exacto (UiSelector().text(...)), que en esta app es
 * el unico identificador disponible para esos elementos.
 * <p>
 * Nota: como el locator ya matchea por texto exacto, isTitleDisplayed()/
 * getTitleText() no son una verificacion 100% independiente del contenido
 * (si el texto cambiara, el locator directamente no encontraria el elemento).
 * Se expone igual getTitleText()/getSubtitleText() para que el test compare
 * explicitamente contra las constantes EXPECTED_* con un assertThat legible,
 * en vez de embeber el string esperado dentro del locator sin dejar rastro
 * en la asercion.
 * <p>
 * Este Screen no realiza aserciones -- solo expone estado. Las validaciones
 * viven en la clase de test.
 */
public class HomeScreen extends BaseScreen {

    public static final String EXPECTED_TITLE = "WEBDRIVER";
    public static final String EXPECTED_SUBTITLE = "Demo app for the appium-boilerplate";

    private static final String SCREEN_MARKER = "Home-screen";

    public HomeScreen(AndroidDriver driver) {
        super(driver);
    }

    public boolean isScreenDisplayed() {
        return isDisplayed(descriptionLocator(SCREEN_MARKER));
    }

    public String getTitleText() {
        return waitForVisibility(titleLocator()).getText();
    }

    public String getSubtitleText() {
        return waitForVisibility(subtitleLocator()).getText();
    }

    private By titleLocator() {
        return androidUiAutomator(String.format("new UiSelector().text(\"%s\")", EXPECTED_TITLE));
    }

    private By subtitleLocator() {
        return androidUiAutomator(String.format("new UiSelector().text(\"%s\")", EXPECTED_SUBTITLE));
    }
}