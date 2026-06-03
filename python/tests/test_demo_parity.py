"""Parity tests for the 23 languages from the .NET demo (csharp/demo/.../Program.cs).

Each expected value is the byte-for-byte output produced by running the C#
`Slugify.MultiLang.Demo` program, so passing these proves the Python port is a
faithful reproduction of the original.
"""

from __future__ import annotations

import pytest

from slugify_multilang import SlugifySlugOptions, slugify

# (display name, locale, input text, expected slug from the C# demo)
DEMO_CASES: list[tuple[str, str, str, str]] = [
    # -- Supported locales --------------------------------------------------
    ("Español", "es",
     "¡Director Fu: tu cuenta alternativa ha quedado expuesta otra vez y otra vez y otra vez!",
     "director-fu-tu-cuenta-alternativa-ha-quedado-expuesta-otra-vez-y-otra-vez-y-otra-vez"),
    ("Português", "pt",
     "Diretor Fu: sua conta alternativa foi exposta de novo e de novo e de novo!",
     "diretor-fu-sua-conta-alternativa-foi-exposta-de-novo-e-de-novo-e-de-novo"),
    ("Français", "fr",
     "Directeur Fu : votre compte alternatif a été exposé encore et encore et encore !",
     "directeur-fu-votre-compte-alternatif-a-ete-expose-encore-et-encore-et-encore"),
    ("Deutsch", "de",
     "Direktor Fu: Dein Alternativkonto ist schon wieder und wieder und wieder aufgeflogen!",
     "direktor-fu-dein-alternativkonto-ist-schon-wieder-und-wieder-und-wieder-aufgeflogen"),
    ("Italiano", "it",
     "Direttore Fu: il tuo account alternativo è stato smascherato ancora e ancora e ancora!",
     "direttore-fu-il-tuo-account-alternativo-e-stato-smascherato-ancora-e-ancora-e-ancora"),
    ("Svenska", "sv",
     "Direktör Fu: Ditt alternativa konto har avslöjats igen och igen och igen!",
     "direktoer-fu-ditt-alternativa-konto-har-avsloejats-igen-och-igen-och-igen"),
    ("Dansk", "da",
     "Direktør Fu: Din alternative konto er blevet afsløret igen og igen og igen!",
     "direktoer-fu-din-alternative-konto-er-blevet-afsloeret-igen-og-igen-og-igen"),
    ("Nederlands", "nl",
     "Directeur Fu: Uw alternatieve account is alweer en nog een keer ontmaskerd!",
     "directeur-fu-uw-alternatieve-account-is-alweer-en-nog-een-keer-ontmaskerd"),

    # -- Unsupported locales (fall back to global charmap) ------------------
    ("Polski", "pl",
     "Dyrektorze Fu: twoje alternatywne konto zostało ponownie i ponownie zdemaskowane!",
     "dyrektorze-fu-twoje-alternatywne-konto-zostalo-ponownie-i-ponownie-zdemaskowane"),
    ("Norsk", "no",
     "Direktør Fu: Den alternative kontoen din har blitt avslørt igjen og igjen og igjen!",
     "direktor-fu-den-alternative-kontoen-din-har-blitt-avslort-igjen-og-igjen-og-igjen"),
    ("Tiếng Việt", "vi",
     "Giám đốc Phú: Tài khoản phụ của bạn đã bị lộ lại và lại và lại!",
     "giam-doc-phu-tai-khoan-phu-cua-ban-da-bi-lo-lai-va-lai-va-lai"),
    ("Türkçe", "tr",
     "Müdür Fu: Sahte hesabın yine yine yine deşifre oldu!",
     "mudur-fu-sahte-hesabin-yine-yine-yine-desifre-oldu"),
    ("Bahasa Melayu", "ms",
     "Pengarah Fu: Akaun tiruan anda telah terdedah lagi dan lagi dan lagi!",
     "pengarah-fu-akaun-tiruan-anda-telah-terdedah-lagi-dan-lagi-dan-lagi"),
    ("Bahasa Indonesia", "id",
     "Direktur Fu: Akun samaran Anda telah terbongkar lagi dan lagi dan lagi!",
     "direktur-fu-akun-samaran-anda-telah-terbongkar-lagi-dan-lagi-dan-lagi"),
    ("Filipino", "tl",
     "Direktor Fu: Ang iyong alternatibong account ay nabunyag na naman at naman at naman!",
     "direktor-fu-ang-iyong-alternatibong-account-ay-nabunyag-na-naman-at-naman-at-naman"),

    # -- Script-preserving (CJK / Arabic / Devanagari / Thai) ---------------
    ("العربية", "ar",
     "المدير فو: لقد تم كشف حسابك البديل مرة أخرى ومرة أخرى ومرة أخرى!",
     "المدير-فو-لقد-تم-كشف-حسابك-البديل-مرة-أخرى-ومرة-أخرى-ومرة-أخرى"),
    ("日本語", "ja",
     "傅総：あなたのサブアカウントがまたまたまたバレちゃった！",
     "傅総-あなたのサブアカウントがまたまたまたバレちゃった"),
    ("한국어", "ko",
     "푸 총재: 당신의 부계정이 또 또 또 들통났어요!",
     "푸-총재-당신의-부계정이-또-또-또-들통났어요"),
    ("ภาษาไทย", "th",
     "ผู้อำนวยการฝู: บัญชีอำพรางของคุณถูกเปิดเผยอีกและอีกและอีกครั้ง!",
     "ผอำนวยการฝ-บญชอำพรางของคณถกเปดเผยอกและอกและอกครง"),
    ("हिन्दी", "hi",
     "निदेशक फू: आपका वैकल्पिक खाता फिर और फिर और फिर उजागर हो गया!",
     "नदशक-फ-आपक-वकलपक-खत-फर-और-फर-और-फर-उजगर-ह-गय"),
    ("中文 (简体)", "zh",
     "傅总：你的马甲 又又又掉了！",
     "傅总-你的马甲-又又又掉了"),
    ("中文 (繁體)", "zh-tw",
     "傅總：你的馬甲 又又又掉了！",
     "傅總-你的馬甲-又又又掉了"),
    ("English", "en",
     "Director Fu: Your alt account got exposed again and again and again!",
     "director-fu-your-alt-account-got-exposed-again-and-again-and-again"),
]


@pytest.mark.parametrize(
    "text,locale,expected",
    [(text, locale, expected) for _name, locale, text, expected in DEMO_CASES],
    ids=[name for name, _locale, _text, _expected in DEMO_CASES],
)
def test_demo_language_parity(text: str, locale: str, expected: str) -> None:
    assert slugify(text, SlugifySlugOptions(locale=locale)) == expected
