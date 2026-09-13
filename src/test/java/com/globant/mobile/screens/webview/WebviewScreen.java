package com.globant.mobile.screens.webview;

import com.globant.mobile.utils.BaseScreen;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;


/**
 * Representa la pantalla Webview.
 * <p>
 * Al tocar este tab, la app carga el sitio real de WebdriverIO dentro de un
 * WebView nativo, expuesto en el arbol de accesibilidad (no requiere
 * switchContext, se puede interactuar con UiAutomator2 normalmente).
 * <p>
 * Alcance deliberadamente acotado: el contenido de la pagina (enlaces a
 * GitHub/YouTube, logos de sponsors, copy de marketing) pertenece a un sitio
 * externo fuera del control de esta app y puede cambiar en cualquier momento.
 * Asertar sobre ese contenido haria flaky al test por motivos ajenos a lo que
 * realmente se quiere probar (que la navegacion de la app funciona). Por eso
 * este Screen solo verifica:
 * - Que el WebView esta presente.
 * - Que el elemento de branding "WebdriverIO" (parte fija del layout/navbar
 *   del sitio) esta visible, como senal liviana de que cargo el sitio correcto.
 */
public class WebviewScreen extends BaseScreen {

    private static final String BRAND_MARK_DESCRIPTION = "WebdriverIO";

    public WebviewScreen(AndroidDriver driver) {
        super(driver);
    }

    public boolean isWebViewDisplayed() {
        return isDisplayed(webViewLocator());
    }

    public boolean isBrandMarkDisplayed() {
        return isDisplayed(descriptionLocator(BRAND_MARK_DESCRIPTION));
    }

    private By webViewLocator() {
        return androidUiAutomator("new UiSelector().className(\"android.webkit.WebView\")");
    }
}
