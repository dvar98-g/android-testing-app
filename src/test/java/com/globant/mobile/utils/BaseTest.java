package com.globant.mobile.utils;

import com.globant.mobile.config.ConfigReader;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Base para todas las clases de test.
 * <p>
 * Se encarga de:
 * - Leer la configuracion via ConfigReader.
 * - Armar las capabilities del driver (UiAutomator2Options).
 * - Iniciar una sesion de Appium nueva antes de cada test (@BeforeMethod).
 * - Cerrar la sesion despues de cada test (@AfterMethod).
 * <p>
 * Cada test method obtiene su propia sesion de Appium, con la app en estado
 * limpio (noReset=false), garantizando aislamiento total entre tests.
 */
public abstract class BaseTest {

    protected AndroidDriver driver;

    private final ConfigReader config = new ConfigReader();

    @BeforeMethod
    public void setUp() throws MalformedURLException {
        UiAutomator2Options options = buildCapabilities();
        URL appiumServerUrl = new URL(config.getAppiumServerUrl());

        driver = new AndroidDriver(appiumServerUrl, options);
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private UiAutomator2Options buildCapabilities() {
        return new UiAutomator2Options()
                .setPlatformName(config.getPlatformName())
                .setPlatformVersion(config.getPlatformVersion())
                .setDeviceName(config.getDeviceName())
                .setUdid(config.getUdid())
                .setAutomationName(config.getAutomationName())
                .setApp(new File(config.getAppPath()).getAbsolutePath())
                .setNoReset(false);
    }
}