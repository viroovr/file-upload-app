package com.task.fileuploadapp.service;

import java.util.Locale;
import java.util.regex.Pattern;

public final class ExtensionNormalizer {

    private static final Pattern VALID_PATTERN = Pattern.compile("^[a-z0-9]+$");

    private ExtensionNormalizer() {
    }

    public static String normalize(String raw) {
        if (raw == null) {
            return "";
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        while (trimmed.startsWith(".")) {
            trimmed = trimmed.substring(1);
        }
        trimmed = trimmed.trim();
        return trimmed.toLowerCase(Locale.ROOT);
    }

    public static boolean isValid(String normalized) {
        if (normalized == null || normalized.isEmpty()) {
            return false;
        }
        if (normalized.length() > 20) {
            return false;
        }
        if (containsWhitespace(normalized)) {
            return false;
        }
        return VALID_PATTERN.matcher(normalized).matches();
    }

    private static boolean containsWhitespace(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isWhitespace(value.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
