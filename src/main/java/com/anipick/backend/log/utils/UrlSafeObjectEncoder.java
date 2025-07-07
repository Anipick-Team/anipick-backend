package com.anipick.backend.log.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@UtilityClass
public class UrlSafeObjectEncoder {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public String encodeURL(final Object object) {
        if (object == null) {
            throw new IllegalArgumentException("object is null");
        }
        try {
            final String json = objectMapper.writeValueAsString(object);
            final byte[] utf8Bytes = json.getBytes(StandardCharsets.UTF_8);
            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(utf8Bytes);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
