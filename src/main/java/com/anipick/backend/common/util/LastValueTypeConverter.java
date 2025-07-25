package com.anipick.backend.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LastValueTypeConverter {
    public static Long toLong(String str) {
        if (isBlank(str)) {
            return null;
        }
        return Long.valueOf(str);
    }

    public static Double toDouble(String str) {
        if (isBlank(str)) {
            return null;
        }
        return Double.valueOf(str);
    }

    private static Boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        if (str.trim().isEmpty()) {
            return true;
        }
        return false;
    }
}
