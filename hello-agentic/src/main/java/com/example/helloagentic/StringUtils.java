package com.example.helloagentic;

import java.util.Arrays;

public class StringUtils {

    public static String slugify(String text, String separator, Integer maxLength) {
        text = text.toLowerCase().trim();
        text = text.replaceAll("[^\\w\\s-]", "");
        text = text.replaceAll("[\\s_]+", separator);
        text = text.replaceAll("^-+|-+$", "");
        if (maxLength != null && text.length() > maxLength) {
            text = text.substring(0, maxLength);
            while (text.endsWith(separator) && !text.isEmpty()) {
                text = text.substring(0, text.length() - separator.length());
            }
        }
        return text;
    }

    public static String slugify(String text) {
        return slugify(text, "-", null);
    }

    public static String truncate(String text, int length, String suffix) {
        if (text == null || text.isEmpty() || length <= 0) {
            return "";
        }
        if (text.length() <= length) {
            return text;
        }
        String truncated = text.substring(0, length - suffix.length());
        int lastSpace = truncated.lastIndexOf(' ');
        if (lastSpace > length / 2) {
            truncated = truncated.substring(0, lastSpace);
        }
        return truncated + suffix;
    }

    public static String truncate(String text, int length) {
        return truncate(text, length, "...");
    }

    public static String maskEmail(String email) {
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address");
        }
        int atIndex = email.lastIndexOf('@');
        String local = email.substring(0, atIndex);
        String domain = email.substring(atIndex + 1);

        String maskedLocal;
        if (local.length() <= 2) {
            maskedLocal = local.charAt(0) + "*";
        } else {
            maskedLocal = local.charAt(0) + "*".repeat(local.length() - 2) + local.charAt(local.length() - 1);
        }

        String[] domainParts = domain.split("\\.");
        String maskedDomain = domainParts[0].charAt(0) + "***" + "." +
                String.join(".", Arrays.copyOfRange(domainParts, 1, domainParts.length));
        return maskedLocal + "@" + maskedDomain;
    }
}
