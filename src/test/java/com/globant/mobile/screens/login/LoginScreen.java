package com.globant.mobile.screens.login;


import com.globant.mobile.utils.BaseScreen;
import io.appium.java_client.android.AndroidDriver;

/**
 * Representa la pantalla Login, que en realidad contiene DOS formularios
 * (Login y Sign Up) dentro del mismo contenedor "Login-screen" -- solo uno
 * esta visible a la vez, segun que tab este activo.
 * <p>
 * input-email e input-password se reutilizan identicos en ambos formularios
 * (mismo content-desc); input-repeat-password solo existe en Sign Up.
 * <p>
 * Las constantes SIGN_UP_SUCCESS_* / LOGIN_SUCCESS_* documentan el resultado
 * esperado en el AlertDialog nativo (leido via NativeAlertComponent) tras
 * cada flujo exitoso.
 * <p>
 * Los botones de submit (LOGIN y SIGN UP) requieren dos cosas:
 * 1) scrollToElement con el content-desc del contenedor: tras escribir en los
 *    inputs, el ScrollView autoscrollea y empuja el boton fuera del viewport
 *    (verificado: sin el scroll el tap no alcanza al boton).
 * 2) tap sobre el TextView interno (localizado por texto), NO sobre el
 *    ViewGroup contenedor que lleva el content-desc.
 * <p>
 * El scroll trae un efecto secundario propio: el responder system de React
 * Native retiene el toque que llega inmediatamente despues de un gesto de
 * scroll (inercia/fling) y lo cancela antes de que alcance al boton. Por eso
 * scrollToElement espera a que el scroll se asiente. Diagnostico: un click
 * humano real sobre el boton SIN scroll previo si dispara el onPress,
 * mientras que el tap automatizado inmediatamente posterior al scroll no.
 */
public class LoginScreen extends BaseScreen {

    public static final String SIGN_UP_SUCCESS_TITLE = "Signed Up!";
    public static final String SIGN_UP_SUCCESS_MESSAGE = "You successfully signed up!";
    public static final String LOGIN_SUCCESS_TITLE = "Success";
    public static final String LOGIN_SUCCESS_MESSAGE = "You are logged in!";

    private static final String SCREEN_MARKER = "Login-screen";
    private static final String TAB_LOGIN = "button-login-container";
    private static final String TAB_SIGN_UP = "button-sign-up-container";
    private static final String INPUT_EMAIL = "input-email";
    private static final String INPUT_PASSWORD = "input-password";
    private static final String INPUT_REPEAT_PASSWORD = "input-repeat-password";
    private static final String BUTTON_LOGIN = "button-LOGIN";
    private static final String BUTTON_SIGN_UP = "button-SIGN UP";
    private static final String BUTTON_LOGIN_TEXT = "LOGIN";
    private static final String BUTTON_SIGN_UP_TEXT = "SIGN UP";

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

    public void tapLoginTab() {
        tap(descriptionLocator(TAB_LOGIN));
    }

    public void tapSignUpTab() {
        tap(descriptionLocator(TAB_SIGN_UP));
    }

    public void fillLoginForm(String email, String password) {
        type(descriptionLocator(INPUT_EMAIL), email);
        type(descriptionLocator(INPUT_PASSWORD), password);
    }

    public void fillSignUpForm(String email, String password) {
        type(descriptionLocator(INPUT_EMAIL), email);
        type(descriptionLocator(INPUT_PASSWORD), password);
        type(descriptionLocator(INPUT_REPEAT_PASSWORD), password);
    }

    public void tapLoginButton() {
        scrollToElement(BUTTON_LOGIN);
        tap(textLocator(BUTTON_LOGIN_TEXT));
    }

    public void tapSignUpButton() {
        scrollToElement(BUTTON_SIGN_UP);
        tap(textLocator(BUTTON_SIGN_UP_TEXT));
    }
}
