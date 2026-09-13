package com.globant.mobile.tests.login;

import com.globant.mobile.config.ConfigReader;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.globant.mobile.screens.common.BottomTabBarComponent;
import com.globant.mobile.screens.common.NativeAlertComponent;
import com.globant.mobile.screens.common.ScreenNames;
import com.globant.mobile.screens.login.LoginScreen;
import com.globant.mobile.utils.BaseTest;
import com.globant.mobile.utils.RandomUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Escenario: Successful Login.
 * <p>
 * Precondicion: estar en la seccion Login Y tener un usuario previamente
 * creado. Como la app no tiene backend real ni valida credenciales, la
 * precondicion se cumple haciendo un signup real en @BeforeMethod (con las
 * credenciales que despues se usan para el login), en vez de asumir que
 * "cualquier cosa funciona" sin pasar por el flujo real de creacion -- asi
 * el test refleja el escenario tal como esta descripto.
 */
public class LoginTest extends BaseTest {

    private final ConfigReader config = new ConfigReader();

    private LoginScreen loginScreen;
    private String email;
    private String password;

    @BeforeMethod
    public void createUserAndReturnToLoginTab() {
        BottomTabBarComponent tabBar = new BottomTabBarComponent(driver);
        tabBar.tapTab(ScreenNames.LOGIN);

        loginScreen = new LoginScreen(driver);
        assertThat("Login screen displayed as precondition", loginScreen.isScreenDisplayed(), is(true));

        email = RandomUtils.generateEmail();
        password = config.getTestPassword();

        loginScreen.tapSignUpTab();
        loginScreen.fillSignUpForm(email, password);
        loginScreen.tapSignUpButton();

        NativeAlertComponent signUpAlert = new NativeAlertComponent(driver);
        assertThat("User created as precondition", signUpAlert.getTitle(), equalTo(LoginScreen.SIGN_UP_SUCCESS_TITLE));
        signUpAlert.tapOk();

        loginScreen.tapLoginTab();
    }

    @Test
    public void login_withCreatedUser_completesSuccessfully() {
        loginScreen.fillLoginForm(email, password);
        loginScreen.tapLoginButton();

        NativeAlertComponent alert = new NativeAlertComponent(driver);
        assertThat("Login alert title", alert.getTitle(), equalTo(LoginScreen.LOGIN_SUCCESS_TITLE));
        assertThat("Login alert message", alert.getMessage(), equalTo(LoginScreen.LOGIN_SUCCESS_MESSAGE));

        alert.tapOk();
    }
}
