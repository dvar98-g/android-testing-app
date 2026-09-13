package com.globant.mobile.screens.common;



/**
 * Agrupa los valores exactos de content-desc (accessibility label) de cada
 * pantalla, tal como los expone la app. Se usan tanto para interactuar con
 * la bottom tab bar (BottomTabBarComponent) como con el panel lateral de Menu.
 * <p>
 * Nota: el content-desc del tab de Web es "Webview", no "Web" -- justamente
 * el tipo de detalle que esta clase evita tener que recordar/escribir mas
 * de una vez.
 */
public final class ScreenNames {

    public static final String HOME = "Home";
    public static final String WEB = "Webview";
    public static final String LOGIN = "Login";
    public static final String FORMS = "Forms";
    public static final String SWIPE = "Swipe";
    public static final String DRAG = "Drag";
    public static final String MENU = "Menu";
    public static final String PERMISSIONS = "Permissions";
    public static final String DATA_MANAGEMENT = "Data Management";

    private ScreenNames() {
    }
}