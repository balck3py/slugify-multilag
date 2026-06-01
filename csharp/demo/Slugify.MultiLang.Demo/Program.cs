using Slugify.MultiLang;

Console.OutputEncoding = System.Text.Encoding.UTF8;

// Source text: "傅总：你的马甲 又又又掉了！"
// Meaning: "Director Fu: Your alt account has been exposed again and again and again!"
// Each entry: (display name, locale code passed to SlugifyHelper, translated text)
var samples = new (string Language, string Locale, string Text)[]
{
    // ── Supported locales (locale map entry exists in SlugifyHelper) ──────────
    ("Español",          "es",    "¡Director Fu: tu cuenta alternativa ha quedado expuesta otra vez y otra vez y otra vez!"),
    ("Português",        "pt",    "Diretor Fu: sua conta alternativa foi exposta de novo e de novo e de novo!"),
    ("Français",         "fr",    "Directeur Fu : votre compte alternatif a été exposé encore et encore et encore !"),
    ("Deutsch",          "de",    "Direktor Fu: Dein Alternativkonto ist schon wieder und wieder und wieder aufgeflogen!"),
    ("Italiano",         "it",    "Direttore Fu: il tuo account alternativo è stato smascherato ancora e ancora e ancora!"),
    ("Svenska",          "sv",    "Direktör Fu: Ditt alternativa konto har avslöjats igen och igen och igen!"),
    ("Dansk",            "da",    "Direktør Fu: Din alternative konto er blevet afsløret igen og igen og igen!"),
    ("Nederlands",       "nl",    "Directeur Fu: Uw alternatieve account is alweer en nog een keer ontmaskerd!"),

    // ── Unsupported locales (fall back to global charmap + strict Unicode keep) ─
    ("Polski",           "pl",    "Dyrektorze Fu: twoje alternatywne konto zostało ponownie i ponownie zdemaskowane!"),
    ("Norsk",            "no",    "Direktør Fu: Den alternative kontoen din har blitt avslørt igjen og igjen og igjen!"),
    ("Tiếng Việt",       "vi",    "Giám đốc Phú: Tài khoản phụ của bạn đã bị lộ lại và lại và lại!"),
    ("Türkçe",           "tr",    "Müdür Fu: Sahte hesabın yine yine yine deşifre oldu!"),
    ("Bahasa Melayu",    "ms",    "Pengarah Fu: Akaun tiruan anda telah terdedah lagi dan lagi dan lagi!"),
    ("Bahasa Indonesia", "id",    "Direktur Fu: Akun samaran Anda telah terbongkar lagi dan lagi dan lagi!"),
    ("Filipino",         "tl",    "Direktor Fu: Ang iyong alternatibong account ay nabunyag na naman at naman at naman!"),

    // ── Script-preserving (CJK / Arabic / Devanagari stay as Unicode letters) ──
    ("العربية",          "ar",    "المدير فو: لقد تم كشف حسابك البديل مرة أخرى ومرة أخرى ومرة أخرى!"),
    ("日本語",           "ja",    "傅総：あなたのサブアカウントがまたまたまたバレちゃった！"),
    ("한국어",           "ko",    "푸 총재: 당신의 부계정이 또 또 또 들통났어요!"),
    ("ภาษาไทย",         "th",    "ผู้อำนวยการฝู: บัญชีอำพรางของคุณถูกเปิดเผยอีกและอีกและอีกครั้ง!"),
    ("हिन्दी",          "hi",    "निदेशक फू: आपका वैकल्पिक खाता फिर और फिर और फिर उजागर हो गया!"),
    ("中文 (简体)",      "zh",    "傅总：你的马甲 又又又掉了！"),
    ("中文 (繁體)",      "zh-tw", "傅總：你的馬甲 又又又掉了！"),
    ("English",          "en",    "Director Fu: Your alt account got exposed again and again and again!"),
};

// Locales that have explicit entries in SlugifyHelper's locale map
var supportedLocales = new HashSet<string> { "bg", "de", "es", "fr", "pt", "uk", "vi", "da", "nb", "it", "nl", "sv" };

Console.WriteLine("╔══════════════════════════════════════════════════════════════════╗");
Console.WriteLine("║          Slugify.MultiLang — Multi-Language Demo                 ║");
Console.WriteLine("║  Source: 傅总：你的马甲 又又又掉了！                              ║");
Console.WriteLine("╚══════════════════════════════════════════════════════════════════╝");
Console.WriteLine();

foreach (var (language, locale, text) in samples)
{
    var localeNote = supportedLocales.Contains(locale) ? $"locale={locale}" : $"locale={locale} (fallback to charmap)";
    var slug = SlugifyHelper.Slugify(text, new SlugifySlugOptions { Locale = locale });

    Console.WriteLine($"┌─ {language} [{localeNote}]");
    Console.WriteLine($"│  IN : {text}");
    Console.WriteLine($"│  OUT: {slug}");
    Console.WriteLine("│");
}

Console.WriteLine("└─ Done.");
