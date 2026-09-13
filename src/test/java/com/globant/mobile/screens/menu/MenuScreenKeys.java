package com.globant.mobile.screens.menu;

/**
 * Keys usadas para construir dinamicamente los accessibility ids del panel
 * de Menu: "side-menu-item-<key>" y "side-menu-star-<key>". Un solo key por
 * pantalla evita mantener dos listas de constantes que puedan desalinearse.
 * <p>
 * HOME no tiene estrella asociada (siempre esta presente en la tab bar y no
 * puede desactivarse), por lo que no debe usarse con los metodos de estrella
 * de MenuScreen.
 */
public final class MenuScreenKeys {

    public static final String HOME = "home";
    public static final String WEBVIEW = "webview";
    public static final String LOGIN = "login";
    public static final String FORMS = "forms";
    public static final String SWIPE = "swipe";
    public static final String DRAG = "drag";
    public static final String PERMISSIONS = "permissions";
    public static final String DATA_MANAGEMENT = "data-management";

    private MenuScreenKeys() {
    }
}
