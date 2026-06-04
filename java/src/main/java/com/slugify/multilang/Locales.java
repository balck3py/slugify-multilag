package com.slugify.multilang;

import java.util.HashMap;
import java.util.Map;

/**
 * Per-language override maps. Generated 1:1 from the verified reference maps;
 * every entry is identical to the C# {@code SlugifyHelper.BuildLocales()}.
 */
final class Locales {

    private Locales() {
    }

    static Map<String, Map<Character, String>> build() {
        Map<String, Map<Character, String>> locales = new HashMap<>();
        locales.put("bg", bg());
        locales.put("de", de());
        locales.put("es", es());
        locales.put("fr", fr());
        locales.put("pt", pt());
        locales.put("uk", uk());
        locales.put("vi", vi());
        locales.put("da", da());
        locales.put("nb", nb());
        locales.put("it", it());
        locales.put("nl", nl());
        locales.put("sv", sv());
        return locales;
    }

    private static Map<Character, String> bg() {
        Map<Character, String> m = new HashMap<>(20);
        m.put('\u0419', "Y");
        m.put('\u0426', "Ts");
        m.put('\u0429', "Sht");
        m.put('\u042a', "A");
        m.put('\u042c', "Y");
        m.put('\u0439', "y");
        m.put('\u0446', "ts");
        m.put('\u0449', "sht");
        m.put('\u044a', "a");
        m.put('\u044c', "y");
        return m;
    }

    private static Map<Character, String> de() {
        Map<Character, String> m = new HashMap<>(26);
        m.put('\u00c4', "AE");
        m.put('\u00e4', "ae");
        m.put('\u00d6', "OE");
        m.put('\u00f6', "oe");
        m.put('\u00dc', "UE");
        m.put('\u00fc', "ue");
        m.put('\u00df', "ss");
        m.put('%', "prozent");
        m.put('&', "und");
        m.put('|', "oder");
        m.put('\u2211', "summe");
        m.put('\u221e', "unendlich");
        m.put('\u2665', "liebe");
        return m;
    }

    private static Map<Character, String> es() {
        Map<Character, String> m = new HashMap<>(24);
        m.put('%', "por ciento");
        m.put('&', "y");
        m.put('<', "menor que");
        m.put('>', "mayor que");
        m.put('|', "o");
        m.put('\u00a2', "centavos");
        m.put('\u00a3', "libras");
        m.put('\u00a4', "moneda");
        m.put('\u20a3', "francos");
        m.put('\u2211', "suma");
        m.put('\u221e', "infinito");
        m.put('\u2665', "amor");
        return m;
    }

    private static Map<Character, String> fr() {
        Map<Character, String> m = new HashMap<>(24);
        m.put('%', "pourcent");
        m.put('&', "et");
        m.put('<', "plus petit");
        m.put('>', "plus grand");
        m.put('|', "ou");
        m.put('\u00a2', "centime");
        m.put('\u00a3', "livre");
        m.put('\u00a4', "devise");
        m.put('\u20a3', "franc");
        m.put('\u2211', "somme");
        m.put('\u221e', "infini");
        m.put('\u2665', "amour");
        return m;
    }

    private static Map<Character, String> pt() {
        Map<Character, String> m = new HashMap<>(20);
        m.put('%', "porcento");
        m.put('&', "e");
        m.put('<', "menor");
        m.put('>', "maior");
        m.put('|', "ou");
        m.put('\u00a2', "centavo");
        m.put('\u2211', "soma");
        m.put('\u00a3', "libra");
        m.put('\u221e', "infinito");
        m.put('\u2665', "amor");
        return m;
    }

    private static Map<Character, String> uk() {
        Map<Character, String> m = new HashMap<>(24);
        m.put('\u0418', "Y");
        m.put('\u0438', "y");
        m.put('\u0419', "Y");
        m.put('\u0439', "y");
        m.put('\u0426', "Ts");
        m.put('\u0446', "ts");
        m.put('\u0425', "Kh");
        m.put('\u0445', "kh");
        m.put('\u0429', "Shch");
        m.put('\u0449', "shch");
        m.put('\u0413', "H");
        m.put('\u0433', "h");
        return m;
    }

    private static Map<Character, String> vi() {
        Map<Character, String> m = new HashMap<>(4);
        m.put('\u0110', "D");
        m.put('\u0111', "d");
        return m;
    }

    private static Map<Character, String> da() {
        Map<Character, String> m = new HashMap<>(20);
        m.put('\u00d8', "OE");
        m.put('\u00f8', "oe");
        m.put('\u00c5', "AA");
        m.put('\u00e5', "aa");
        m.put('%', "procent");
        m.put('&', "og");
        m.put('|', "eller");
        m.put('$', "dollar");
        m.put('<', "mindre end");
        m.put('>', "st\u00f8rre end");
        return m;
    }

    private static Map<Character, String> nb() {
        Map<Character, String> m = new HashMap<>(14);
        m.put('&', "og");
        m.put('\u00c5', "AA");
        m.put('\u00c6', "AE");
        m.put('\u00d8', "OE");
        m.put('\u00e5', "aa");
        m.put('\u00e6', "ae");
        m.put('\u00f8', "oe");
        return m;
    }

    private static Map<Character, String> it() {
        Map<Character, String> m = new HashMap<>(2);
        m.put('&', "e");
        return m;
    }

    private static Map<Character, String> nl() {
        Map<Character, String> m = new HashMap<>(2);
        m.put('&', "en");
        return m;
    }

    private static Map<Character, String> sv() {
        Map<Character, String> m = new HashMap<>(14);
        m.put('&', "och");
        m.put('\u00c5', "AA");
        m.put('\u00c4', "AE");
        m.put('\u00d6', "OE");
        m.put('\u00e5', "aa");
        m.put('\u00e4', "ae");
        m.put('\u00f6', "oe");
        return m;
    }

}
