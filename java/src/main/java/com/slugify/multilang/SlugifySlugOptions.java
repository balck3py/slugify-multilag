package com.slugify.multilang;

import java.util.regex.Pattern;

/**
 * Options controlling slug generation.
 *
 * <p>Faithful 1:1 port of the C# {@code SlugifySlugOptions} class. Every default
 * matches the original exactly. Setters are fluent so options can be built
 * inline, e.g. {@code new SlugifySlugOptions().locale("de").lower(false)}.
 *
 * <table>
 *   <caption>C# &harr; Java field mapping</caption>
 *   <tr><th>C#</th><th>Java</th><th>Default</th></tr>
 *   <tr><td>{@code string Replacement}</td><td>{@code replacement}</td><td>{@code "-"}</td></tr>
 *   <tr><td>{@code Regex? Remove}</td><td>{@code remove}</td><td>{@code null}</td></tr>
 *   <tr><td>{@code bool Lower}</td><td>{@code lower}</td><td>{@code true}</td></tr>
 *   <tr><td>{@code bool Strict}</td><td>{@code strict}</td><td>{@code true}</td></tr>
 *   <tr><td>{@code bool Trim}</td><td>{@code trim}</td><td>{@code true}</td></tr>
 *   <tr><td>{@code string? Locale}</td><td>{@code locale}</td><td>{@code null}</td></tr>
 * </table>
 */
public class SlugifySlugOptions {

    private String replacement = "-";
    private Pattern remove = null;
    private boolean lower = true;
    private boolean strict = true;
    private boolean trim = true;
    private String locale = null;

    public SlugifySlugOptions() {
    }

    public SlugifySlugOptions replacement(String replacement) {
        this.replacement = replacement;
        return this;
    }

    public SlugifySlugOptions remove(Pattern remove) {
        this.remove = remove;
        return this;
    }

    public SlugifySlugOptions lower(boolean lower) {
        this.lower = lower;
        return this;
    }

    public SlugifySlugOptions strict(boolean strict) {
        this.strict = strict;
        return this;
    }

    public SlugifySlugOptions trim(boolean trim) {
        this.trim = trim;
        return this;
    }

    public SlugifySlugOptions locale(String locale) {
        this.locale = locale;
        return this;
    }

    public String getReplacement() {
        return replacement;
    }

    public Pattern getRemove() {
        return remove;
    }

    public boolean isLower() {
        return lower;
    }

    public boolean isStrict() {
        return strict;
    }

    public boolean isTrim() {
        return trim;
    }

    public String getLocale() {
        return locale;
    }
}
