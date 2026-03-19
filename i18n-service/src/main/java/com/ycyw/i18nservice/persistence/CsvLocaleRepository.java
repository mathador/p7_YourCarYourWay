package com.ycyw.i18nservice.persistence;

import com.ycyw.i18nservice.model.LocaleInfo;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class CsvLocaleRepository {

    /**
     * Default: reads CSV files from classpath under {@code /i18n/*.csv}.
     * Each file name MUST contain the languageTag, e.g. {@code fr-FR.csv}.
     *
     * CSV format (2 columns): {@code key,value}
     * Reserved keys: {@code dateFormat}, {@code timeFormat}
     */
    public Map<String, LocaleInfo> load(ResourcePatternResolver resolver, String resourcePattern) {
        try {
            Resource[] resources = resolver.getResources(resourcePattern);
            Map<String, LocaleInfo> map = new LinkedHashMap<>();
            for (Resource r : resources) {
                if (!r.exists() || !r.isReadable()) continue;
                var fileName = r.getFilename();
                if (fileName == null || !fileName.toLowerCase(Locale.ROOT).endsWith(".csv")) continue;
                var languageTag = fileName.substring(0, fileName.length() - ".csv".length());
                var info = readOne(languageTag, r);
                if (info != null) {
                    map.put(languageTag, info);
                }
            }
            return Collections.unmodifiableMap(map);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load i18n CSV resources from pattern: " + resourcePattern, e);
        }
    }

    private LocaleInfo readOne(String languageTag, Resource resource) {
        String dateFormat = null;
        String timeFormat = null;
        Map<String, String> translations = new LinkedHashMap<>();

        try (var in = resource.getInputStream();
             var reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                var parts = CsvLine.parseTwoColumns(line);
                if (parts == null) continue;
                var key = parts.left().trim();
                var value = parts.right().trim();
                if (key.isEmpty()) continue;

                if ("dateFormat".equalsIgnoreCase(key)) {
                    dateFormat = value;
                } else if ("timeFormat".equalsIgnoreCase(key)) {
                    timeFormat = value;
                } else {
                    translations.put(key, value);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed reading i18n CSV for " + languageTag + " from " + resource, e);
        }

        if (dateFormat == null || dateFormat.isBlank() || timeFormat == null || timeFormat.isBlank()) {
            throw new IllegalStateException("CSV for " + languageTag + " must define dateFormat and timeFormat");
        }

        var locale = Locale.forLanguageTag(languageTag);
        var language = locale.getLanguage();
        var country = locale.getCountry();
        return new LocaleInfo(languageTag, language, country, dateFormat, timeFormat, Map.copyOf(translations));
    }
}

