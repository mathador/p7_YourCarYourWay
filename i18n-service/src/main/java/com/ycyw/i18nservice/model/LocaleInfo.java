package com.ycyw.i18nservice.model;

import java.util.Map;

/**
 * Information about a locale including format patterns and translations.
 */
public record LocaleInfo(
        String locale,
        String language,
        String country,
        String dateFormat,
        String timeFormat,
        Map<String, String> translations
) {
}
