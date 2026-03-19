package com.ycyw.i18nservice.persistence;

/**
 * Minimal CSV parser for 2-column lines: key,value
 * Supports quoted values and escaped quotes ("" inside quoted strings).
 */
final class CsvLine {

    private CsvLine() {}

    record Pair(String left, String right) {}

    static Pair parseTwoColumns(String line) {
        int i = 0;
        StringBuilder left = new StringBuilder();
        StringBuilder right = new StringBuilder();

        boolean inQuotes = false;
        boolean inRight = false;

        while (i < line.length()) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // escaped quote
                    (inRight ? right : left).append('"');
                    i += 2;
                    continue;
                }
                inQuotes = !inQuotes;
                i++;
                continue;
            }

            if (!inQuotes && !inRight && c == ',') {
                inRight = true;
                i++;
                continue;
            }

            (inRight ? right : left).append(c);
            i++;
        }

        if (!inRight) {
            // no comma => not a 2 column CSV line
            return null;
        }
        return new Pair(left.toString(), right.toString());
    }
}

