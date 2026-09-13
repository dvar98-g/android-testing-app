package com.globant.mobile.screens.menu;

import com.globant.mobile.utils.BaseScreen;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

/**
 * Representa el panel lateral de Menu: lista de pantallas navegables y sus
 * estrellas de toggle (controlan si la pantalla aparece en la bottom tab bar).
 * <p>
 * Cada fila expone dos elementos con content-desc propio:
 * - "side-menu-item-{key}"  -> tap para navegar a esa pantalla.
 * - "side-menu-star-{key}"  -> ViewGroup clickeable para togglear, pero SIN el
 *   glifo en si mismo. El glifo (que indica estado activa/inactiva) vive en
 *   un TextView hijo sin content-desc propio, por eso la lectura de estado
 *   usa un locator relativo distinto (childSelector) al locator de tap.
 * <p>
 * Home no tiene estrella -- siempre esta presente en la tab bar y no puede
 * desactivarse. No usar MenuScreenKeys.HOME con isStarActive/toggleStar.
 */
public class MenuScreen extends BaseScreen {

    // Codepoint Unicode (Plano 15 / Area de Uso Privado) del glifo de estrella
    // rellena: pantalla activa, visible en la bottom tab bar.
    private static final String STAR_ACTIVE_GLYPH = "\uDB81\uDCCE";
    // Codepoint del glifo de estrella vacia: pantalla inactiva, oculta de la tab bar.
    private static final String STAR_INACTIVE_GLYPH = "\uDB81\uDCD2";

    public MenuScreen(AndroidDriver driver) {
        super(driver);
    }

    public void tapItem(String key) {
        tap(itemLocator(key));
    }

    public void toggleStar(String key) {
        tap(starContainerLocator(key));
    }

    public boolean isStarActive(String key) {
        String glyph = waitForVisibility(starGlyphLocator(key)).getAttribute("text");
        return STAR_ACTIVE_GLYPH.equals(glyph);
    }

    private By itemLocator(String key) {
        return descriptionLocator("side-menu-item-" + key);
    }

    private By starContainerLocator(String key) {
        return descriptionLocator("side-menu-star-" + key);
    }

    private By starGlyphLocator(String key) {
        return androidUiAutomator(String.format(
                "new UiSelector().description(\"side-menu-star-%s\").childSelector(new UiSelector().className(\"android.widget.TextView\"))",
                key
        ));
    }
}