package com.slugify.multilang;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Faithful 1:1 port of the C# {@code SlugifyHelper}. Converts arbitrary text into
 * a clean, URL-safe slug. Zero third-party dependencies (JDK standard library only).
 *
 * <p>Targets Java 17+ (runs on all mainstream LTS releases: 17, 21, 25).
 */
public final class SlugifyHelper {

    // Shared mutable state, mirroring the C# `static readonly` fields.
    // CHAR_MAP is mutable so extend() can register custom mappings at runtime.
    private static final Map<Character, String> CHAR_MAP = CharMap.build();
    private static final Map<String, Map<Character, String>> LOCALES = Locales.build();

    // C#:  new Regex(@"[^\w\s$*_+~.()'""!\-:@]+")
    // .NET `\w` == [\p{L}\p{Mn}\p{Nd}\p{Pc}], so the class is spelled out explicitly
    // here; UNICODE_CHARACTER_CLASS makes `\s` match Unicode whitespace like .NET.
    private static final Pattern DEFAULT_REMOVE_REGEX = Pattern.compile(
            "[^\\p{L}\\p{Mn}\\p{Nd}\\p{Pc}\\s$*_+~.()'\"!\\-:@]+",
            Pattern.UNICODE_CHARACTER_CLASS);

    // C#:  new Regex(@"[^\p{L}\p{N}\s]")
    private static final Pattern STRICT_REGEX = Pattern.compile(
            "[^\\p{L}\\p{N}\\s]",
            Pattern.UNICODE_CHARACTER_CLASS);

    // C#:  new Regex(@"\s+")
    private static final Pattern SPACES_REGEX = Pattern.compile(
            "\\s+",
            Pattern.UNICODE_CHARACTER_CLASS);

    private SlugifyHelper() {
    }

    /** Convenience overload using all default options. */
    public static String slugify(String input) {
        return slugify(input, new SlugifySlugOptions());
    }

    /**
     * Replacement-only overload, mirroring the C#
     * {@code Slugify(this string input, string replacement = "-")}.
     */
    public static String slugify(String input, String replacement) {
        return slugify(input, new SlugifySlugOptions().replacement(replacement));
    }

    /** Main entry point, mirroring {@code Slugify(string, SlugifySlugOptions?)}. */
    public static String slugify(String input, SlugifySlugOptions options) {
        if (input == null) {
            // C# throws ArgumentException("slugify: string argument expected").
            throw new IllegalArgumentException("slugify: string argument expected");
        }
        if (options == null) {
            options = new SlugifySlugOptions();
        }

        Map<Character, String> locale =
                options.getLocale() != null ? LOCALES.get(options.getLocale()) : null;
        String replacement = options.getReplacement();
        boolean trim = options.isTrim();

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFC);
        Pattern removeRegex = options.getRemove() != null ? options.getRemove() : DEFAULT_REMOVE_REGEX;

        StringBuilder sb = new StringBuilder(normalized.length());

        // C# iterates UTF-16 with manual surrogate-pair handling and only consults
        // the maps for BMP (single-code-unit) characters. We iterate by code point
        // and replicate the `c.Length == 1` guard via Character.charCount.
        int i = 0;
        while (i < normalized.length()) {
            int cp = normalized.codePointAt(i);
            int cc = Character.charCount(cp);
            String c = normalized.substring(i, i + cc);
            i += cc;

            String appendStr = null;

            // single char path (BMP)
            if (cc == 1) {
                char key = c.charAt(0);
                if (locale != null) {
                    appendStr = locale.get(key);
                }
                if (appendStr == null) {
                    appendStr = CHAR_MAP.get(key);
                }
            }

            if (appendStr == null) {
                appendStr = c;
            }

            if (appendStr.equals(replacement)) {
                appendStr = " ";
            }

            appendStr = removeRegex.matcher(appendStr).replaceAll("");
            sb.append(appendStr);
        }

        String slug = sb.toString();

        if (options.isStrict()) {
            slug = STRICT_REGEX.matcher(slug).replaceAll("");
        }

        if (trim) {
            // C# String.Trim() removes Unicode whitespace; String.strip() matches it.
            slug = slug.strip();
        }

        // quoteReplacement so the replacement string is treated literally,
        // matching C# Regex.Replace (no backreference expansion).
        slug = SPACES_REGEX.matcher(slug).replaceAll(Matcher.quoteReplacement(replacement));

        if (options.isLower()) {
            // C# ToLowerInvariant(); Locale.ROOT is the JDK's locale-independent casing.
            slug = slug.toLowerCase(Locale.ROOT);
        }

        return slug;
    }

    /**
     * Register custom character mappings at runtime. Faithful port of C#
     * {@code SlugifyHelper.Extend}: mutates the shared global char map, so the
     * change affects every subsequent {@link #slugify} call.
     */
    public static void extend(Map<Character, String> customMap) {
        CHAR_MAP.putAll(customMap);
    }
}
