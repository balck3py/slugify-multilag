package com.slugify.multilang;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.Normalizer;
import java.util.Map;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/** Behavioural tests for options, extensibility, and edge cases (mirror the C# semantics). */
class OptionsAndApiTest {

    @Nested
    class Basic {
        @Test
        void simpleAscii() {
            assertEquals("hello-world", SlugifyHelper.slugify("Hello World"));
        }

        @Test
        void ampersandToAnd() {
            assertEquals("cafe-au-lait-and-croissant", SlugifyHelper.slugify("Café au lait & cròissant"));
        }

        @Test
        void cjkPreserved() {
            assertEquals("傅总-你的马甲-又又又掉了", SlugifyHelper.slugify("傅总：你的马甲 又又又掉了！"));
        }
    }

    @Nested
    class ReplacementOverload {
        @Test
        void stringOverload() {
            // Mirrors C#: Slugify(this string, string replacement = "-")
            assertEquals("hello_world", SlugifyHelper.slugify("hello world", "_"));
        }

        @Test
        void replacementOption() {
            assertEquals("hello.world", SlugifyHelper.slugify("hello world", new SlugifySlugOptions().replacement(".")));
        }

        @Test
        void defaultReplacementIsDash() {
            assertEquals("a-b-c", SlugifyHelper.slugify("a b c"));
        }
    }

    @Nested
    class Options {
        @Test
        void lowerFalseKeepsCase() {
            assertEquals("Hello-World", SlugifyHelper.slugify("Hello World", new SlugifySlugOptions().lower(false)));
        }

        @Test
        void lowerTrueIsDefault() {
            assertEquals("hello", SlugifyHelper.slugify("HELLO"));
        }

        @Test
        void strictFalseKeepsUnderscore() {
            assertEquals("a_b", SlugifyHelper.slugify("a_b", new SlugifySlugOptions().strict(false)));
        }

        @Test
        void strictTrueStripsUnderscore() {
            // C# strict regex [^\p{L}\p{N}\s] removes underscore.
            assertEquals("ab", SlugifyHelper.slugify("a_b", new SlugifySlugOptions().strict(true)));
        }

        @Test
        void trimDefaultTrimsEdges() {
            assertEquals("hello-world", SlugifyHelper.slugify("  hello world  "));
        }

        @Test
        void customRemoveRegex() {
            SlugifySlugOptions opts = new SlugifySlugOptions().remove(Pattern.compile("[aeiou]"));
            assertEquals("hll-wrld", SlugifyHelper.slugify("hello world", opts));
        }

        @Test
        void localeDe() {
            assertEquals("mueduer", SlugifyHelper.slugify("Müdür", new SlugifySlugOptions().locale("de")));
        }

        @Test
        void unknownLocaleFallsBack() {
            assertEquals("mudur", SlugifyHelper.slugify("Müdür", new SlugifySlugOptions().locale("xx")));
        }
    }

    @Nested
    class SymbolsAndCurrencies {
        @Test
        void dollar() {
            assertEquals("dollar100", SlugifyHelper.slugify("$100"));
        }

        @Test
        void euro() {
            assertEquals("euro50", SlugifyHelper.slugify("€50"));
        }

        @Test
        void percent() {
            assertEquals("50percent", SlugifyHelper.slugify("50%"));
        }
    }

    @Nested
    class UnicodeMechanics {
        @Test
        void nfcNormalization() {
            // Decomposed "café" (e + U+0301) must behave like precomposed.
            String decomposed = Normalizer.normalize("café", Normalizer.Form.NFD);
            assertEquals("cafe", SlugifyHelper.slugify(decomposed));
        }

        @Test
        void astralCodepointStripped() {
            // Emoji (astral, no charmap entry) -> stripped by strict pass.
            assertEquals("hi-there", SlugifyHelper.slugify("hi 😀 there"));
        }

        @Test
        void emptyString() {
            assertEquals("", SlugifyHelper.slugify(""));
        }
    }

    @Nested
    class Extend {
        @Test
        void extendRegistersMapping() {
            SlugifyHelper.extend(Map.of('☂', "umbrella"));
            assertEquals("umbrella-rain", SlugifyHelper.slugify("☂ rain"));
        }

        @Test
        void extendOverridesExisting() {
            String original = SlugifyHelper.slugify("₿");
            try {
                SlugifyHelper.extend(Map.of('₿', "btc"));
                assertEquals("btc", SlugifyHelper.slugify("₿"));
            } finally {
                SlugifyHelper.extend(Map.of('₿', "bitcoin"));
                assertEquals(original, SlugifyHelper.slugify("₿"));
            }
        }
    }

    @Nested
    class Errors {
        @Test
        void nullRaises() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> SlugifyHelper.slugify((String) null));
            assertEquals("slugify: string argument expected", ex.getMessage());
        }
    }
}
