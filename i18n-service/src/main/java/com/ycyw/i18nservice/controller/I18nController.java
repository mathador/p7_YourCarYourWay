package com.ycyw.i18nservice.controller;

import com.ycyw.i18nservice.model.LocaleInfo;
import com.ycyw.i18nservice.service.LocaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/i18n")
@Tag(name = "I18n Service", description = "Endpoints pour récupérer des traductions et des formats de date/heure")
public class I18nController {

    private final LocaleService localeService;

    public I18nController(LocaleService localeService) {
        this.localeService = localeService;
    }

    @GetMapping("/locales")
    @Operation(summary = "Liste des locales supportées")
    public List<String> getSupportedLocales() {
        return localeService.getAllLocales().keySet().stream().toList();
    }

    @GetMapping("/locale")
    @Operation(summary = "Récupère les informations (formats + traductions) pour une locale donnée")
    public ResponseEntity<LocaleInfo> getLocaleInfo(
            @RequestParam(name = "locale", required = false) String locale
    ) {
        return localeService.getLocaleInfo(locale)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/translate")
    @Operation(summary = "Récupère la traduction d'une clé pour une locale donnée")
    public ResponseEntity<Map<String, String>> translate(
            @RequestParam(name = "locale", required = false) String locale,
            @RequestParam(name = "key") String key
    ) {
        var info = localeService.getLocaleInfo(locale).orElse(null);
        if (info == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        var value = info.translations().get(key);
        if (value == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(Map.of(key, value));
    }

    @GetMapping("/format")
    @Operation(summary = "Formatte une date/heure selon la locale demandée")
    public ResponseEntity<Map<String, String>> format(
            @RequestParam(name = "locale", required = false) String locale,
            @RequestParam(name = "datetime", required = false) String datetime
    ) {
        var infoOpt = localeService.getLocaleInfo(locale);
        if (infoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        var info = infoOpt.get();
        LocalDateTime dt;
        if (datetime == null || datetime.isBlank()) {
            dt = LocalDateTime.now();
        } else {
            try {
                dt = LocalDateTime.parse(datetime, DateTimeFormatter.ISO_DATE_TIME);
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "datetime must be ISO-8601 compliant, e.g. 2026-03-16T15:34:00"));
            }
        }

        String formattedDate = localeService.formatDate(info.locale(), dt);
        String formattedTime = localeService.formatTime(info.locale(), dt);

        return ResponseEntity.ok(Map.of(
                "locale", info.locale(),
                "date", formattedDate,
                "time", formattedTime
        ));
    }
}
