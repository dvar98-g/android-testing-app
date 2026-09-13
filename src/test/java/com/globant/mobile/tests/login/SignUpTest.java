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
 * Escenario: Successful Sign Up.
 * <p>
 * Precondicion: estar en la seccion Login (Home -> tab Login de la bottom
 * tab bar), verificado en @BeforeMethod.
 * <p>
 * El email se genera aleatoriamente (RandomUtils) para que el test pueda
 * ejecutarse multiples veces sin colisionar. La app no tiene backend real
 * (no valida ni persiste cuentas), por lo que no hace falta un paso de
 * limpieza posterior -- se confirmo explicitamente antes de implementar esto.
 */
public class SignUpTest extends BaseTest {

    private final ConfigReader config = new ConfigReader();

    private LoginScreen loginScreen;

    @BeforeMethod
    public void navigateToLoginSection() {
        BottomTabBarComponent tabBar = new BottomTabBarComponent(driver);
        tabBar.tapTab(ScreenNames.LOGIN);

        loginScreen = new LoginScreen(driver);
        assertThat("Login screen displayed as precondition", loginScreen.isScreenDisplayed(), is(true));
    }

    @Test
    public void signUp_withRandomEmail_completesSuccessfully() {
        loginScreen.tapSignUpTab();

        String email = RandomUtils.generateEmail();
        String password = config.getTestPassword();
        loginScreen.fillSignUpForm(email, password);
        loginScreen.tapSignUpButton();

        NativeAlertComponent alert = new NativeAlertComponent(driver);
        assertThat("Sign up alert title", alert.getTitle(), equalTo(LoginScreen.SIGN_UP_SUCCESS_TITLE));
        assertThat("Sign up alert message", alert.getMessage(), equalTo(LoginScreen.SIGN_UP_SUCCESS_MESSAGE));

        alert.tapOk();
    }
}
