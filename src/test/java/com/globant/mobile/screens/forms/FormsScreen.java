package com.globant.mobile.screens.forms;

import com.globant.mobile.utils.BaseScreen;
import io.appium.java_client.android.AndroidDriver;

/**
 * Representa la pantalla Forms.
 * <p>
 * Alcance acotado a lo necesario para el escenario de navegacion: confirmar
 * que la pantalla cargo y que sus controles principales (input, switch,
 * dropdown, botones) estan visibles. No cubre el flujo funcional completo de
 * cada control (eso corresponde a un test dedicado del modulo Forms).
 */
public class FormsScreen extends BaseScreen {

    private static final String SCREEN_MARKER = "Forms-screen";
    private static final String TEXT_INPUT = "text-input";
    private static final String SWITCH = "switch";
    private static final String DROPDOWN = "Dropdown";
    private static final String BUTTON_ACTIVE = "button-Active";
    private static final String BUTTON_INACTIVE = "button-Inactive";

    public FormsScreen(AndroidDriver driver) {
        super(driver);
    }

    public boolean isScreenDisplayed() {
        return isDisplayed(descriptionLocator(SCREEN_MARKER));
    }

    public boolean isTextInputDisplayed() {
        return isDisplayed(descriptionLocator(TEXT_INPUT));
    }

    public boolean isSwitchDisplayed() {
        return isDisplayed(descriptionLocator(SWITCH));
    }

    public boolean isDropdownDisplayed() {
        return isDisplayed(descriptionLocator(DROPDOWN));
    }

    public boolean isActiveButtonDisplayed() {
        return isDisplayed(descriptionLocator(BUTTON_ACTIVE));
    }

    public boolean isInactiveButtonDisplayed() {
        return isDisplayed(descriptionLocator(BUTTON_INACTIVE));
    }
}