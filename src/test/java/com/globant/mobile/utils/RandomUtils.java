package com.globant.mobile.utils;


import com.globant.mobile.config.ConfigReader;
import java.util.UUID;

/**
 * Genera datos aleatorios para que los tests puedan ejecutarse multiples
 * veces sin colisionar (ej. emails unicos para signup).
 */
public final class RandomUtils {

    private static final String EMAIL_LOCAL_PART_PREFIX = "qa_";
    private static final int UUID_SEGMENT_LENGTH = 8;

    private RandomUtils() {
    }

    /**
     * Genera un email unico por llamada: prefijo fijo + segmento de UUID +
     * dominio configurado en config.properties (test.email.domain).
     */
    public static String generateEmail() {
        ConfigReader config = new ConfigReader();
        String uniqueSegment = UUID.randomUUID().toString().substring(0, UUID_SEGMENT_LENGTH);
        return EMAIL_LOCAL_PART_PREFIX + uniqueSegment + "@" + config.getTestEmailDomain();
    }
}
