package com.globant.mobile.screens.login;


import com.globant.mobile.utils.BaseScreen;
import io.appium.java_client.android.AndroidDriver;

/**
 * Representa la pantalla Login.
 * <p>
 * Alcance acotado a lo necesario para el escenario de navegacion: confirmar
 * que la pantalla cargo y que sus elementos principales (inputs y boton de
 * login) estan visibles. No cubre el flujo funcional completo de login/signup
 * (eso corresponde a un test dedicado del modulo Login).
 */
public class LoginScreen extends BaseScreen {

    private static final String SCREEN_MARKER = "Login-screen";
    private static final String INPUT_EMAIL = "input-email";
    private static final String INPUT_PASSWORD = "input-password";
    private static final String BUTTON_LOGIN = "button-LOGIN";

    public LoginScreen(AndroidDriver driver) {
        super(driver);
    }

    public boolean isScreenDisplayed() {
        return isDisplayed(descriptionLocator(SCREEN_MARKER));
    }

    public boolean isEmailInputDisplayed() {
        return isDisplayed(descriptionLocator(INPUT_EMAIL));
    }

    public boolean isPasswordInputDisplayed() {
        return isDisplayed(descriptionLocator(INPUT_PASSWORD));
    }

    public boolean isLoginButtonDisplayed() {
        return isDisplayed(descriptionLocator(BUTTON_LOGIN));
    }
}