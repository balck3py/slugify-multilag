package com.slugify.multilang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Parity tests for the 23 languages from the .NET demo (csharp/demo/.../Program.cs).
 *
 * <p>Each expected value is the byte-for-byte output produced by the C#
 * {@code Slugify.MultiLang.Demo} program, so passing these proves the Java port
 * is a faithful reproduction of the original.
 */
class DemoParityTest {

    static Stream<Arguments> demoCases() {
        return Stream.of(
                // -- Supported locales --------------------------------------
                Arguments.of("Español", "es",
                        "¡Director Fu: tu cuenta alternativa ha quedado expuesta otra vez y otra vez y otra vez!",
                        "director-fu-tu-cuenta-alternativa-ha-quedado-expuesta-otra-vez-y-otra-vez-y-otra-vez"),
                Arguments.of("Português", "pt",
                        "Diretor Fu: sua conta alternativa foi exposta de novo e de novo e de novo!",
                        "diretor-fu-sua-conta-alternativa-foi-exposta-de-novo-e-de-novo-e-de-novo"),
                Arguments.of("Français", "fr",
                        "Directeur Fu : votre compte alternatif a été exposé encore et encore et encore !",
                        "directeur-fu-votre-compte-alternatif-a-ete-expose-encore-et-encore-et-encore"),
                Arguments.of("Deutsch", "de",
                        "Direktor Fu: Dein Alternativkonto ist schon wieder und wieder und wieder aufgeflogen!",
                        "direktor-fu-dein-alternativkonto-ist-schon-wieder-und-wieder-und-wieder-aufgeflogen"),
                Arguments.of("Italiano", "it",
                        "Direttore Fu: il tuo account alternativo è stato smascherato ancora e ancora e ancora!",
                        "direttore-fu-il-tuo-account-alternativo-e-stato-smascherato-ancora-e-ancora-e-ancora"),
                Arguments.of("Svenska", "sv",
                        "Direktör Fu: Ditt alternativa konto har avslöjats igen och igen och igen!",
                        "direktoer-fu-ditt-alternativa-konto-har-avsloejats-igen-och-igen-och-igen"),
                Arguments.of("Dansk", "da",
                        "Direktør Fu: Din alternative konto er blevet afsløret igen og igen og igen!",
                        "direktoer-fu-din-alternative-konto-er-blevet-afsloeret-igen-og-igen-og-igen"),
                Arguments.of("Nederlands", "nl",
                        "Directeur Fu: Uw alternatieve account is alweer en nog een keer ontmaskerd!",
                        "directeur-fu-uw-alternatieve-account-is-alweer-en-nog-een-keer-ontmaskerd"),

                // -- Unsupported locales (fall back to global charmap) -------
                Arguments.of("Polski", "pl",
                        "Dyrektorze Fu: twoje alternatywne konto zostało ponownie i ponownie zdemaskowane!",
                        "dyrektorze-fu-twoje-alternatywne-konto-zostalo-ponownie-i-ponownie-zdemaskowane"),
                Arguments.of("Norsk", "no",
                        "Direktør Fu: Den alternative kontoen din har blitt avslørt igjen og igjen og igjen!",
                        "direktor-fu-den-alternative-kontoen-din-har-blitt-avslort-igjen-og-igjen-og-igjen"),
                Arguments.of("Tiếng Việt", "vi",
                        "Giám đốc Phú: Tài khoản phụ của bạn đã bị lộ lại và lại và lại!",
                        "giam-doc-phu-tai-khoan-phu-cua-ban-da-bi-lo-lai-va-lai-va-lai"),
                Arguments.of("Türkçe", "tr",
                        "Müdür Fu: Sahte hesabın yine yine yine deşifre oldu!",
                        "mudur-fu-sahte-hesabin-yine-yine-yine-desifre-oldu"),
                Arguments.of("Bahasa Melayu", "ms",
                        "Pengarah Fu: Akaun tiruan anda telah terdedah lagi dan lagi dan lagi!",
                        "pengarah-fu-akaun-tiruan-anda-telah-terdedah-lagi-dan-lagi-dan-lagi"),
                Arguments.of("Bahasa Indonesia", "id",
                        "Direktur Fu: Akun samaran Anda telah terbongkar lagi dan lagi dan lagi!",
                        "direktur-fu-akun-samaran-anda-telah-terbongkar-lagi-dan-lagi-dan-lagi"),
                Arguments.of("Filipino", "tl",
                        "Direktor Fu: Ang iyong alternatibong account ay nabunyag na naman at naman at naman!",
                        "direktor-fu-ang-iyong-alternatibong-account-ay-nabunyag-na-naman-at-naman-at-naman"),

                // -- Script-preserving (CJK / Arabic / Devanagari / Thai) ----
                Arguments.of("العربية", "ar",
                        "المدير فو: لقد تم كشف حسابك البديل مرة أخرى ومرة أخرى ومرة أخرى!",
                        "المدير-فو-لقد-تم-كشف-حسابك-البديل-مرة-أخرى-ومرة-أخرى-ومرة-أخرى"),
                Arguments.of("日本語", "ja",
                        "傅総：あなたのサブアカウントがまたまたまたバレちゃった！",
                        "傅総-あなたのサブアカウントがまたまたまたバレちゃった"),
                Arguments.of("한국어", "ko",
                        "푸 총재: 당신의 부계정이 또 또 또 들통났어요!",
                        "푸-총재-당신의-부계정이-또-또-또-들통났어요"),
                Arguments.of("ภาษาไทย", "th",
                        "ผู้อำนวยการฝู: บัญชีอำพรางของคุณถูกเปิดเผยอีกและอีกและอีกครั้ง!",
                        "ผอำนวยการฝ-บญชอำพรางของคณถกเปดเผยอกและอกและอกครง"),
                Arguments.of("हिन्दी", "hi",
                        "निदेशक फू: आपका वैकल्पिक खाता फिर और फिर और फिर उजागर हो गया!",
                        "नदशक-फ-आपक-वकलपक-खत-फर-और-फर-और-फर-उजगर-ह-गय"),
                Arguments.of("中文 (简体)", "zh",
                        "傅总：你的马甲 又又又掉了！",
                        "傅总-你的马甲-又又又掉了"),
                Arguments.of("中文 (繁體)", "zh-tw",
                        "傅總：你的馬甲 又又又掉了！",
                        "傅總-你的馬甲-又又又掉了"),
                Arguments.of("English", "en",
                        "Director Fu: Your alt account got exposed again and again and again!",
                        "director-fu-your-alt-account-got-exposed-again-and-again-and-again"));
    }

    @ParameterizedTest(name = "{0} [{1}]")
    @MethodSource("demoCases")
    void demoLanguageParity(String name, String locale, String text, String expected) {
        assertEquals(expected, SlugifyHelper.slugify(text, new SlugifySlugOptions().locale(locale)));
    }
}
