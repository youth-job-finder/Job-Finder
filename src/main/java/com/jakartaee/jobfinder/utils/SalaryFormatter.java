package com.jakartaee.jobfinder.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Normalizes free-text salary values into a consistent Rand-style format.
 * Example: "$120,000 - $180,000" → "R120,000 - R180,000"
 */
public final class SalaryFormatter {

    private static final Pattern CURRENCY_WORDS = Pattern.compile(
            "(?i)\\b(?:usd|eur|gbp|zar|cad|aud|ngn|kes|bwp|inr|jpy|cny|dollars?|euros?|pounds?|rand)\\b\\s*");
    private static final Pattern CURRENCY_SYMBOLS = Pattern.compile("[\\$€£¥₹₦₱₽₨₴₵₡₭₮₫]");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d[\\d,]*(?:\\.\\d+)?");

    private SalaryFormatter() {}

    public static String normalizeToRand(String salaryText) {
        if (salaryText == null) return null;

        String normalized = salaryText.trim();
        if (normalized.isEmpty()) return "Competitive";

        // Replace currency words/symbols with "R"
        normalized = CURRENCY_WORDS.matcher(normalized).replaceAll("R");
        normalized = CURRENCY_SYMBOLS.matcher(normalized).replaceAll("R");

        // Collapse "R 120000" → "R120000"
        normalized = normalized.replaceAll("(?i)R\\s+(?=\\d)", "R");

        // Remove stray "R" inside numbers
        normalized = normalized.replaceAll("(?<=\\d)R(?=\\d)", "");

        // Ensure each number has a single "R" prefix
        Matcher matcher = NUMBER_PATTERN.matcher(normalized);
        StringBuffer rebuilt = new StringBuffer();
        while (matcher.find()) {
            String number = matcher.group();
            // If already prefixed with R, leave it
            int start = matcher.start();
            boolean alreadyPrefixed = start > 0 && normalized.substring(Math.max(0, start - 2), start).matches("R\\s?");
            String replacement = alreadyPrefixed ? number : "R" + number;
            matcher.appendReplacement(rebuilt, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(rebuilt);
        normalized = rebuilt.toString();

        // Clean up ranges and spacing
        normalized = normalized.replaceAll("\\s*-\\s*", " - ");
        normalized = normalized.replaceAll("\\s{2,}", " ").trim();

        // Final safety: collapse any accidental double R
        normalized = normalized.replaceAll("R{2,}", "R");

        return normalized;
    }
}


