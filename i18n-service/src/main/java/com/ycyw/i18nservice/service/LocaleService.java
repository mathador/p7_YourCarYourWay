package com.ycyw.i18nservice.service;

import com.ycyw.i18nservice.model.LocaleInfo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class LocaleService {

    private final Map<String, LocaleInfo> supportedLocales;

    public LocaleService() {
        supportedLocales = buildLocales();
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

    private Map<String, LocaleInfo> buildLocales() {
        Map<String, LocaleInfo> map = new LinkedHashMap<>();

        map.put("en-US", new LocaleInfo(
                "en-US",
                "en",
                "US",
                "MM/dd/yyyy",
                "hh:mm a",
                Map.of(
                        "greeting", "Hello",
                        "farewell", "Goodbye",
                        "dateLabel", "Date",
                        "timeLabel", "Time"
                )
        ));

        map.put("en-CA", new LocaleInfo(
                "en-CA",
                "en",
                "CA",
                "yyyy-MM-dd",
                "HH:mm",
                Map.of(
                        "greeting", "Hello",
                        "farewell", "Goodbye",
                        "dateLabel", "Date",
                        "timeLabel", "Time"
                )
        ));

        map.put("fr-CA", new LocaleInfo(
                "fr-CA",
                "fr",
                "CA",
                "yyyy-MM-dd",
                "HH:mm",
                Map.of(
                        "greeting", "Bonjour",
                        "farewell", "Au revoir",
                        "dateLabel", "Date",
                        "timeLabel", "Heure"
                )
        ));

        map.put("fr-FR", new LocaleInfo(
                "fr-FR",
                "fr",
                "FR",
                "dd/MM/yyyy",
                "HH:mm",
                Map.of(
                        "greeting", "Bonjour",
                        "farewell", "Au revoir",
                        "dateLabel", "Date",
                        "timeLabel", "Heure"
                )
        ));

        map.put("es-ES", new LocaleInfo(
                "es-ES",
                "es",
                "ES",
                "dd/MM/yyyy",
                "HH:mm",
                Map.of(
                        "greeting", "Hola",
                        "farewell", "Adiós",
                        "dateLabel", "Fecha",
                        "timeLabel", "Hora"
                )
        ));

        map.put("de-DE", new LocaleInfo(
                "de-DE",
                "de",
                "DE",
                "dd.MM.yyyy",
                "HH:mm",
                Map.of(
                        "greeting", "Hallo",
                        "farewell", "Auf Wiedersehen",
                        "dateLabel", "Datum",
                        "timeLabel", "Uhrzeit"
                )
        ));

        map.put("it-IT", new LocaleInfo(
                "it-IT",
                "it",
                "IT",
                "dd/MM/yyyy",
                "HH:mm",
                Map.of(
                        "greeting", "Ciao",
                        "farewell", "Arrivederci",
                        "dateLabel", "Data",
                        "timeLabel", "Ora"
                )
        ));

        return map;
    }
}
