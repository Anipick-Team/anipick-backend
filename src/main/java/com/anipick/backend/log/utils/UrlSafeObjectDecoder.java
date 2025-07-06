package com.anipick.backend.log.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@UtilityClass
public class UrlSafeObjectDecoder {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public <T> T decodeURL(final String logData, final Class<T> valueType) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(logData);
            String json = new String(decoded, StandardCharsets.UTF_8);
            return objectMapper.readValue(json, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
