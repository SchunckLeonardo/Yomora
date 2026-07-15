package com.yomora.catalog.domain;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class BookLanguage {
    public static final String PORTUGUESE = "por";

    private static final Map<String, String> ISO3_TO_ISO2 = Arrays.stream(Locale.getISOLanguages())
            .collect(Collectors.toUnmodifiableMap(
                    BookLanguage::iso3,
                    Function.identity(),
                    (first, ignored) -> first
            ));

    private BookLanguage() {
    }

    public static String normalize(String language) {
        if (language == null || language.isBlank()) {
            return language;
        }
        String normalized = language.trim().toLowerCase(Locale.ROOT);
        if (normalized.length() != 2) {
            return normalized;
        }
        return iso3(normalized);
    }

    public static String toIso6391(String language) {
        String normalized = normalize(language);
        if (normalized == null || normalized.isBlank() || normalized.length() == 2) {
            return normalized;
        }
        return ISO3_TO_ISO2.getOrDefault(normalized, normalized);
    }

    private static String iso3(String iso2) {
        try {
            return Locale.forLanguageTag(iso2).getISO3Language().toLowerCase(Locale.ROOT);
        } catch (MissingResourceException exception) {
            return iso2;
        }
    }
}
