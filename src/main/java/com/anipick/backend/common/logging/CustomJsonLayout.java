package com.anipick.backend.common.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.contrib.jackson.JacksonJsonFormatter;
import ch.qos.logback.contrib.json.classic.JsonLayout;

import java.util.Map;

public class CustomJsonLayout extends JsonLayout {

    public CustomJsonLayout() {
        super();
        super.setJsonFormatter(new JacksonJsonFormatter());
    }

    @Override
    protected Map toJsonMap(final ILoggingEvent event) {
        @SuppressWarnings("unchecked")
        final Map<String, Object> map = super.toJsonMap(event);

        map.remove("context");

        final String rootCause = rootCauseSimpleName(event.getThrowableProxy());
        if (rootCause != null) {
            map.put("Exception", rootCause);
        }

        map.put("stackTrace", map.getOrDefault("exception", ""));
        map.remove("exception");

        removeLineBreaks(map);
        return map;
    }

    private static String rootCauseSimpleName(final IThrowableProxy throwableProxy) {
        if (throwableProxy == null) {
            return null;
        }
        IThrowableProxy root = throwableProxy;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        final String className = root.getClassName();
        return className.substring(className.lastIndexOf('.') + 1);
    }

    private static void removeLineBreaks(final Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof String value) {
                entry.setValue(value.replaceAll("[\\r\\n]+", " ").replace("\t", ""));
            }
        }
    }
}
