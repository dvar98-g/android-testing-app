package com.globant.mobile.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Centraliza la lectura de configuracion del framework.
 * <p>
 * Reglas:
 * - Prioridad de resolucion: variable de entorno > config.properties > valor por defecto.
 * - Si config.properties no existe en el classpath, no se lanza excepcion: se continua
 *   con un Properties vacio, confiando en que las variables de entorno cubran lo necesario.
 * - Los valores obligatorios (sin default posible) lanzan una excepcion clara si ni la
 *   variable de entorno ni el properties los proveen.
 */
public final class ConfigReader {

    private static final String CONFIG_FILE_NAME = "config.properties";

    private static final String APPIUM_SERVER_URL_DEFAULT = "http://127.0.0.1:4723";
    private static final String PLATFORM_NAME_DEFAULT = "Android";
    private static final String AUTOMATION_NAME_DEFAULT = "UiAutomator2";
    private static final int EXPLICIT_WAIT_SECONDS_DEFAULT = 15;
    private static final int ACTION_DELAY_MILLIS_DEFAULT = 0;

    private final Properties properties;

    public ConfigReader() {
        this.properties = loadProperties();
    }

    private Properties loadProperties() {
        Properties loaded = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {
            if (input != null) {
                loaded.load(input);
            }
        } catch (IOException e) {
            // No se interrumpe la ejecucion: config.properties es opcional si las
            // variables de entorno cubren los valores requeridos.
        }
        return loaded;
    }

    public String getAppiumServerUrl() {
        return resolveOptional("APPIUM_SERVER_URL", "appium.server.url", APPIUM_SERVER_URL_DEFAULT);
    }

    public String getAppPath() {
        return resolveRequired("APP_PATH", "app.path");
    }

    public String getPlatformName() {
        return resolveOptional("PLATFORM_NAME", "platform.name", PLATFORM_NAME_DEFAULT);
    }

    public String getPlatformVersion() {
        return resolveRequired("PLATFORM_VERSION", "platform.version");
    }

    public String getDeviceName() {
        return resolveRequired("DEVICE_NAME", "device.name");
    }

    public String getUdid() {
        return resolveRequired("UDID", "udid");
    }

    public String getAutomationName() {
        return resolveOptional("AUTOMATION_NAME", "automation.name", AUTOMATION_NAME_DEFAULT);
    }

    public int getExplicitWaitSeconds() {
        String value = resolveOptional("EXPLICIT_WAIT_SECONDS", "explicit.wait.seconds", null);
        return parseIntOrDefault(value, EXPLICIT_WAIT_SECONDS_DEFAULT);
    }

    public int getActionDelayMillis() {
        String value = resolveOptional("ACTION_DELAY_MILLIS", "action.delay.millis", null);
        return parseIntOrDefault(value, ACTION_DELAY_MILLIS_DEFAULT);
    }

    private String resolveOptional(String envVarName, String propertyKey, String defaultValue) {
        String envValue = System.getenv(envVarName);
        if (isPresent(envValue)) {
            return envValue;
        }

        String propertyValue = properties.getProperty(propertyKey);
        if (isPresent(propertyValue)) {
            return propertyValue;
        }

        return defaultValue;
    }

    private String resolveRequired(String envVarName, String propertyKey) {
        String envValue = System.getenv(envVarName);
        if (isPresent(envValue)) {
            return envValue;
        }

        String propertyValue = properties.getProperty(propertyKey);
        if (isPresent(propertyValue)) {
            return propertyValue;
        }

        throw new IllegalStateException(
                "Missing required config: " + envVarName + " (env var) or " + propertyKey + " (properties)"
        );
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        if (!isPresent(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private boolean isPresent(String value) {
        return value != null && !value.trim().isEmpty();
    }
}