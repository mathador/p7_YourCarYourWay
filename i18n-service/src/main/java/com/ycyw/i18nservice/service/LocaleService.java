package com.ycyw.i18nservice.service;

import com.ycyw.i18nservice.model.LocaleInfo;
import com.ycyw.i18nservice.persistence.CsvLocaleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class LocaleService {

    private final Map<String, LocaleInfo> supportedLocales;

    public LocaleService(
            CsvLocaleRepository csvLocaleRepository,
            ResourcePatternResolver resourcePatternResolver,
            @Value("${i18n.csv.resource-pattern:classpath*:i18n/*.csv}") String resourcePattern
    ) {
        supportedLocales = csvLocaleRepository.load(resourcePatternResolver, resourcePattern);
    }

    public Map<String, LocaleInfo> getAllLocales() {
        return Collections.unmodifiableMap(supportedLocales);
    }

    public Optional<LocaleInfo> getLocaleInfo(String localeTag) {
        if (localeTag == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(supportedLocales.get(localeTag.trim()));
    }

    public String formatDate(String localeTag, LocalDateTime dateTime) {
        var locale = resolveLocale(localeTag);
        var info = supportedLocales.getOrDefault(localeTag, supportedLocales.get("en-US"));
        var formatter = DateTimeFormatter.ofPattern(info.dateFormat()).withLocale(locale);
        return dateTime.atZone(ZoneId.systemDefault()).format(formatter);
    }

    public String formatTime(String localeTag, LocalDateTime dateTime) {
        var locale = resolveLocale(localeTag);
        var info = supportedLocales.getOrDefault(localeTag, supportedLocales.get("en-US"));
        var formatter = DateTimeFormatter.ofPattern(info.timeFormat()).withLocale(locale);
        return dateTime.atZone(ZoneId.systemDefault()).format(formatter);
    }

    private Locale resolveLocale(String localeTag) {
        if (localeTag == null) {
            return Locale.forLanguageTag("en-US");
        }
        try {
            return Locale.forLanguageTag(localeTag.trim());
        } catch (Exception ignored) {
            return Locale.forLanguageTag("en-US");
        }
    }
}
