package com.globant.mobile.tests.menu;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.globant.mobile.screens.common.BottomTabBarComponent;
import com.globant.mobile.screens.common.ScreenNames;
import com.globant.mobile.screens.drag.DragScreen;
import com.globant.mobile.screens.forms.FormsScreen;
import com.globant.mobile.screens.home.HomeScreen;
import com.globant.mobile.screens.login.LoginScreen;
import com.globant.mobile.screens.menu.MenuScreen;
import com.globant.mobile.screens.menu.MenuScreenKeys;
import com.globant.mobile.screens.swipe.SwipeScreen;
import com.globant.mobile.screens.webview.WebviewScreen;
import com.globant.mobile.utils.BaseTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Escenario: Navigation on the bottom menu bar.
 * <p>
 * Precondicion: la app arranca en Home (verificado en @BeforeMethod, antes de
 * cada test). Cada test toca un icono distinto de la bottom tab bar y verifica
 * que los elementos principales de la pantalla destino esten visibles.
 * <p>
 * Home no tiene su propio test -- es la precondicion compartida por todos.
 * Un test por seccion, siguiendo la practica de mantener una validacion
 * logica por test (aqui: "la pantalla X cargo correctamente"), expresada en
 * varias aserciones sobre esa misma pantalla.
 */
public class MenuTest extends BaseTest {

    private BottomTabBarComponent tabBar;

    @BeforeMethod
    public void verifyHomeScreenPrecondition() {
        tabBar = new BottomTabBarComponent(driver);
        HomeScreen homeScreen = new HomeScreen(driver);

        assertThat("Home screen displayed as precondition", homeScreen.isScreenDisplayed(), is(true));
    }

    @Test
    public void navigateToWebview_displaysWebviewScreen() {
        tabBar.tapTab(ScreenNames.WEB);
        WebviewScreen webviewScreen = new WebviewScreen(driver);

        assertThat("WebView displayed", webviewScreen.isWebViewDisplayed(), is(true));
        assertThat("WebdriverIO brand mark displayed", webviewScreen.isBrandMarkDisplayed(), is(true));
    }

    @Test
    public void navigateToLogin_displaysLoginScreen() {
        tabBar.tapTab(ScreenNames.LOGIN);
        LoginScreen loginScreen = new LoginScreen(driver);

        assertThat("Login screen displayed", loginScreen.isScreenDisplayed(), is(true));
        assertThat("Email input displayed", loginScreen.isEmailInputDisplayed(), is(true));
        assertThat("Password input displayed", loginScreen.isPasswordInputDisplayed(), is(true));
        assertThat("Login button displayed", loginScreen.isLoginButtonDisplayed(), is(true));
    }

    @Test
    public void navigateToForms_displaysFormsScreen() {
        tabBar.tapTab(ScreenNames.FORMS);
        FormsScreen formsScreen = new FormsScreen(driver);

        assertThat("Forms screen displayed", formsScreen.isScreenDisplayed(), is(true));
        assertThat("Text input displayed", formsScreen.isTextInputDisplayed(), is(true));
        assertThat("Switch displayed", formsScreen.isSwitchDisplayed(), is(true));
        assertThat("Dropdown displayed", formsScreen.isDropdownDisplayed(), is(true));
        assertThat("Active button displayed", formsScreen.isActiveButtonDisplayed(), is(true));
        assertThat("Inactive button displayed", formsScreen.isInactiveButtonDisplayed(), is(true));
    }

    @Test
    public void navigateToSwipe_displaysSwipeScreen() {
        tabBar.tapTab(ScreenNames.SWIPE);
        SwipeScreen swipeScreen = new SwipeScreen(driver);

        assertThat("Swipe screen displayed", swipeScreen.isScreenDisplayed(), is(true));
        assertThat("Carousel displayed", swipeScreen.isCarouselDisplayed(), is(true));
    }

    @Test
    public void navigateToDrag_displaysDragScreen() {
        tabBar.tapTab(ScreenNames.DRAG);
        DragScreen dragScreen = new DragScreen(driver);

        assertThat("Drag screen displayed", dragScreen.isScreenDisplayed(), is(true));
        assertThat("Renew button displayed", dragScreen.isRenewButtonDisplayed(), is(true));
    }

    @Test
    public void navigateToMenu_opensMenuPanel() {
        tabBar.tapTab(ScreenNames.MENU);
        MenuScreen menuScreen = new MenuScreen(driver);

        assertThat("Menu panel displayed", menuScreen.isPanelDisplayed(), is(true));
        assertThat("Home item visible in Menu panel", menuScreen.isItemDisplayed(MenuScreenKeys.HOME), is(true));
        assertThat("Webview item visible in Menu panel", menuScreen.isItemDisplayed(MenuScreenKeys.WEBVIEW), is(true));
    }
}