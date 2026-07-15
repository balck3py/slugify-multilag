package io.github.balck3py.slugify.multilang;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/** A Java port of the C# Slugify.MultiLang slug-generation pipeline. */
public final class SlugifyHelper {
    private static final Map<Character, String> CHAR_MAP = new HashMap<Character, String>();
    private static final Map<String, Map<Character, String>> LOCALES = new HashMap<String, Map<Character, String>>();
    private static final int UNICODE = Pattern.UNICODE_CHARACTER_CLASS;
    private static final Pattern DEFAULT_REMOVE = Pattern.compile("[^\\p{L}\\p{Mn}\\p{Nd}\\p{Pc}\\s$*_+~.()'\"!\\-:@]+", UNICODE);
    private static final Pattern STRICT = Pattern.compile("[^\\p{L}\\p{N}\\s]", UNICODE);
    private static final Pattern SPACES = Pattern.compile("\\s+", UNICODE);

    static {
        MappingData.populateCharMap(CHAR_MAP);
        MappingData.populateLocales(LOCALES);
    }

    private SlugifyHelper() { }

    public static String slugify(String input) {
        return slugify(input, new SlugifySlugOptions());
    }

    public static String slugify(String input, String replacement) {
        SlugifySlugOptions options = new SlugifySlugOptions();
        options.setReplacement(replacement);
        return slugify(input, options);
    }

    public static String slugify(String input, SlugifySlugOptions options) {
        if (input == null) {
            throw new IllegalArgumentException("slugify: string argument expected");
        }
        if (options == null) {
            options = new SlugifySlugOptions();
        }

        Map<Character, String> locale = options.getLocale() == null ? null : LOCALES.get(options.getLocale());
        String replacement = options.getReplacement();
        Pattern remove = options.getRemove() == null ? DEFAULT_REMOVE : options.getRemove();
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFC);
        StringBuilder translated = new StringBuilder(normalized.length());

        for (int index = 0; index < normalized.length(); index++) {
            String character;
            char current = normalized.charAt(index);
            if (Character.isHighSurrogate(current) && index + 1 < normalized.length()
                    && Character.isLowSurrogate(normalized.charAt(index + 1))) {
                character = normalized.substring(index, index + 2);
                index++;
            } else {
                character = String.valueOf(current);
            }

            String value = null;
            if (character.length() == 1) {
                Character key = Character.valueOf(character.charAt(0));
                if (locale != null) {
                    value = locale.get(key);
                }
                if (value == null) {
                    value = CHAR_MAP.get(key);
                }
            }
            if (value == null) {
                value = character;
            }
            if (value.equals(replacement)) {
                value = " ";
            }
            translated.append(remove.matcher(value).replaceAll(""));
        }

        String slug = translated.toString();
        if (options.isStrict()) {
            slug = STRICT.matcher(slug).replaceAll("");
        }
        if (options.isTrim()) {
            slug = trimUnicodeWhitespace(slug);
        }
        slug = SPACES.matcher(slug).replaceAll(replacement);
        return options.isLower() ? slug.toLowerCase(Locale.ROOT) : slug;
    }

    public static void extend(Map<Character, String> customMap) {
        for (Map.Entry<Character, String> entry : customMap.entrySet()) {
            CHAR_MAP.put(entry.getKey(), entry.getValue());
        }
    }

    private static String trimUnicodeWhitespace(String value) {
        int start = 0;
        int end = value.length();
        while (start < end) {
            int codePoint = value.codePointAt(start);
            if (!Character.isWhitespace(codePoint) && !Character.isSpaceChar(codePoint)) break;
            start += Character.charCount(codePoint);
        }
        while (end > start) {
            int codePoint = value.codePointBefore(end);
            if (!Character.isWhitespace(codePoint) && !Character.isSpaceChar(codePoint)) break;
            end -= Character.charCount(codePoint);
        }
        return value.substring(start, end);
    }
}
