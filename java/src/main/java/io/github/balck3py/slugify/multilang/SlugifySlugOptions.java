package io.github.balck3py.slugify.multilang;

import java.util.regex.Pattern;

/** Options equivalent to the C# SlugifySlugOptions class. */
public class SlugifySlugOptions {
    private String replacement = "-";
    private Pattern remove;
    private boolean lower = true;
    private boolean strict = true;
    private boolean trim = true;
    private String locale;

    public String getReplacement() { return replacement; }
    public void setReplacement(String replacement) { this.replacement = replacement; }
    public Pattern getRemove() { return remove; }
    public void setRemove(Pattern remove) { this.remove = remove; }
    public boolean isLower() { return lower; }
    public void setLower(boolean lower) { this.lower = lower; }
    public boolean isStrict() { return strict; }
    public void setStrict(boolean strict) { this.strict = strict; }
    public boolean isTrim() { return trim; }
    public void setTrim(boolean trim) { this.trim = trim; }
    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }
}
