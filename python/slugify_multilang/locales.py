"""Port of C# ``SlugifyHelper.BuildLocales()``.

Per-language override maps, transcribed verbatim from the C# source.
A locale entry takes precedence over the global char map for a given character.
"""

from __future__ import annotations


def build_locales() -> dict[str, dict[str, str]]:
    return {
        "bg": {
            "Й": "Y", "Ц": "Ts", "Щ": "Sht", "Ъ": "A", "Ь": "Y",
            "й": "y", "ц": "ts", "щ": "sht", "ъ": "a", "ь": "y",
        },
        "de": {
            "Ä": "AE", "ä": "ae", "Ö": "OE", "ö": "oe",
            "Ü": "UE", "ü": "ue", "ß": "ss",
            "%": "prozent", "&": "und", "|": "oder",
            "∑": "summe", "∞": "unendlich", "♥": "liebe",
        },
        "es": {
            "%": "por ciento", "&": "y", "<": "menor que", ">": "mayor que", "|": "o",
            "¢": "centavos", "£": "libras", "¤": "moneda",
            "₣": "francos", "∑": "suma", "∞": "infinito", "♥": "amor",
        },
        "fr": {
            "%": "pourcent", "&": "et", "<": "plus petit", ">": "plus grand", "|": "ou",
            "¢": "centime", "£": "livre", "¤": "devise",
            "₣": "franc", "∑": "somme", "∞": "infini", "♥": "amour",
        },
        "pt": {
            "%": "porcento", "&": "e", "<": "menor", ">": "maior", "|": "ou",
            "¢": "centavo", "∑": "soma", "£": "libra",
            "∞": "infinito", "♥": "amor",
        },
        "uk": {
            "И": "Y", "и": "y", "Й": "Y", "й": "y",
            "Ц": "Ts", "ц": "ts", "Х": "Kh", "х": "kh",
            "Щ": "Shch", "щ": "shch", "Г": "H", "г": "h",
        },
        "vi": {
            "Đ": "D", "đ": "d",
        },
        "da": {
            "Ø": "OE", "ø": "oe", "Å": "AA", "å": "aa",
            "%": "procent", "&": "og", "|": "eller", "$": "dollar",
            "<": "mindre end", ">": "større end",
        },
        "nb": {
            "&": "og", "Å": "AA", "Æ": "AE", "Ø": "OE",
            "å": "aa", "æ": "ae", "ø": "oe",
        },
        "it": {
            "&": "e",
        },
        "nl": {
            "&": "en",
        },
        "sv": {
            "&": "och", "Å": "AA", "Ä": "AE", "Ö": "OE",
            "å": "aa", "ä": "ae", "ö": "oe",
        },
    }
