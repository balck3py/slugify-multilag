using System;
using System.Collections.Generic;
using System.Text;
using System.Text.RegularExpressions;

namespace Slugify.MultiLang;

public static class SlugifyHelper
{
    private static readonly Dictionary<char, string> _charMap = BuildCharMap();
    private static readonly Dictionary<string, Dictionary<char, string>> _locales = BuildLocales();

    // Replaces [GeneratedRegex] (C# 11 / .NET 7+) with compiled static fields for netstandard2.0 compat.
    private static readonly Regex _defaultRemoveRegex = new Regex(@"[^\w\s$*_+~.()'""!\-:@]+", RegexOptions.Compiled);
    private static readonly Regex _strictRegex        = new Regex(@"[^\p{L}\p{N}\s]",          RegexOptions.Compiled);
    private static readonly Regex _spacesRegex        = new Regex(@"\s+",                       RegexOptions.Compiled);

    public static string Slugify(this string input, string replacement = "-")
    {
        return Slugify(input, new SlugifySlugOptions
        {
            Replacement = replacement
        });
    }

    public static string Slugify(string input, SlugifySlugOptions? options = null)
    {
        if (input is null)
            throw new ArgumentException("slugify: string argument expected");

        options ??= new SlugifySlugOptions();

        var locale = options.Locale != null && _locales.TryGetValue(options.Locale, out var loc) ? loc : null;
        var replacement = options.Replacement;
        var trim = options.Trim;

        var normalized = input.Normalize(NormalizationForm.FormC);
        var removeRegex = options.Remove ?? _defaultRemoveRegex;

        var sb = new StringBuilder(normalized.Length);

        // Replaces string.EnumerateRunes() (.NET Standard 2.1+) with manual surrogate-pair iteration.
        // Functionally identical: BMP chars are handled via charmap (c.Length == 1),
        // surrogate pairs fall through unchanged — same as the original Rune path.
        for (int i = 0; i < normalized.Length; i++)
        {
            string c;
            if (char.IsHighSurrogate(normalized[i]) && i + 1 < normalized.Length && char.IsLowSurrogate(normalized[i + 1]))
            {
                c = normalized.Substring(i, 2);
                i++;
            }
            else
            {
                c = normalized[i].ToString();
            }

            string? appendStr = null;

            // single char path (BMP)
            if (c.Length == 1)
            {
                var charKey = c[0];
                if (locale != null && locale.TryGetValue(charKey, out var locVal))
                    appendStr = locVal;
                else if (_charMap.TryGetValue(charKey, out var mapVal))
                    appendStr = mapVal;
            }

            appendStr ??= c;

            if (appendStr == replacement)
                appendStr = " ";

            appendStr = removeRegex.Replace(appendStr, "");
            sb.Append(appendStr);
        }

        var slug = sb.ToString();

        if (options.Strict)
            slug = _strictRegex.Replace(slug, "");

        if (trim)
            slug = slug.Trim();

        slug = _spacesRegex.Replace(slug, replacement);

        if (options.Lower)
            slug = slug.ToLowerInvariant();

        return slug;
    }

    public static void Extend(Dictionary<char, string> customMap)
    {
        foreach (var kvp in customMap)
            _charMap[kvp.Key] = kvp.Value;
    }

    private static Dictionary<char, string> BuildCharMap()
    {
        return new Dictionary<char, string>
        {
            // Symbols & special
            {'$', "dollar"}, {'%', "percent"}, {'&', "and"}, {'<', "less"}, {'>', "greater"}, {'|', "or"},
            {'¢', "cent"}, {'£', "pound"}, {'¤', "currency"}, {'¥', "yen"},
            {'©', "(c)"}, {'ª', "a"}, {'®', "(r)"}, {'º', "o"},

            // Latin
            {'À', "A"}, {'Á', "A"}, {'Â', "A"}, {'Ã', "A"}, {'Ä', "A"}, {'Å', "A"},
            {'Æ', "AE"}, {'Ç', "C"}, {'È', "E"}, {'É', "E"}, {'Ê', "E"}, {'Ë', "E"},
            {'Ì', "I"}, {'Í', "I"}, {'Î', "I"}, {'Ï', "I"}, {'Ð', "D"}, {'Ñ', "N"},
            {'Ò', "O"}, {'Ó', "O"}, {'Ô', "O"}, {'Õ', "O"}, {'Ö', "O"}, {'Ø', "O"},
            {'Ù', "U"}, {'Ú', "U"}, {'Û', "U"}, {'Ü', "U"}, {'Ý', "Y"}, {'Þ', "TH"},
            {'ß', "ss"},
            {'à', "a"}, {'á', "a"}, {'â', "a"}, {'ã', "a"}, {'ä', "a"}, {'å', "a"},
            {'æ', "ae"}, {'ç', "c"}, {'è', "e"}, {'é', "e"}, {'ê', "e"}, {'ë', "e"},
            {'ì', "i"}, {'í', "i"}, {'î', "i"}, {'ï', "i"}, {'ð', "d"}, {'ñ', "n"},
            {'ò', "o"}, {'ó', "o"}, {'ô', "o"}, {'õ', "o"}, {'ö', "o"}, {'ø', "o"},
            {'ù', "u"}, {'ú', "u"}, {'û', "u"}, {'ü', "u"}, {'ý', "y"}, {'þ', "th"},
            {'ÿ', "y"},

            // Latin Extended
            {'Ā', "A"}, {'ā', "a"}, {'Ă', "A"}, {'ă', "a"}, {'Ą', "A"}, {'ą', "a"},
            {'Ć', "C"}, {'ć', "c"}, {'Č', "C"}, {'č', "c"},
            {'Ď', "D"}, {'ď', "d"}, {'Đ', "DJ"}, {'đ', "dj"},
            {'Ē', "E"}, {'ē', "e"}, {'Ė', "E"}, {'ė', "e"},
            {'Ę', "e"}, {'ę', "e"}, {'Ě', "E"}, {'ě', "e"},
            {'Ğ', "G"}, {'ğ', "g"}, {'Ģ', "G"}, {'ģ', "g"},
            {'Ĩ', "I"}, {'ĩ', "i"}, {'Ī', "i"}, {'ī', "i"},
            {'Į', "I"}, {'į', "i"}, {'İ', "I"}, {'ı', "i"},
            {'Ķ', "k"}, {'ķ', "k"},
            {'Ļ', "L"}, {'ļ', "l"}, {'Ľ', "L"}, {'ľ', "l"},
            {'Ł', "L"}, {'ł', "l"},
            {'Ń', "N"}, {'ń', "n"}, {'Ņ', "N"}, {'ņ', "n"}, {'Ň', "N"}, {'ň', "n"},
            {'Ō', "O"}, {'ō', "o"}, {'Ő', "O"}, {'ő', "o"},
            {'Œ', "OE"}, {'œ', "oe"},
            {'Ŕ', "R"}, {'ŕ', "r"}, {'Ř', "R"}, {'ř', "r"},
            {'Ś', "S"}, {'ś', "s"}, {'Ş', "S"}, {'ş', "s"}, {'Š', "S"}, {'š', "s"},
            {'Ţ', "T"}, {'ţ', "t"}, {'Ť', "T"}, {'ť', "t"},
            {'Ũ', "U"}, {'ũ', "u"}, {'Ū', "u"}, {'ū', "u"},
            {'Ů', "U"}, {'ů', "u"}, {'Ű', "U"}, {'ű', "u"},
            {'Ų', "U"}, {'ų', "u"},
            {'Ŵ', "W"}, {'ŵ', "w"}, {'Ŷ', "Y"}, {'ŷ', "y"}, {'Ÿ', "Y"},
            {'Ź', "Z"}, {'ź', "z"}, {'Ż', "Z"}, {'ż', "z"}, {'Ž', "Z"}, {'ž', "z"},
            {'Ə', "E"}, {'ƒ', "f"},
            {'Ơ', "O"}, {'ơ', "o"}, {'Ư', "U"}, {'ư', "u"},
            {'ǈ', "LJ"}, {'ǉ', "lj"}, {'ǋ', "NJ"}, {'ǌ', "nj"},
            {'Ș', "S"}, {'ș', "s"}, {'Ț', "T"}, {'ț', "t"},
            {'ə', "e"}, {'˚', "o"},

            // Greek
            {'Ά', "A"}, {'Έ', "E"}, {'Ή', "H"}, {'Ί', "I"},
            {'Ό', "O"}, {'Ύ', "Y"}, {'Ώ', "W"}, {'ΐ', "i"},
            {'Α', "A"}, {'Β', "B"}, {'Γ', "G"}, {'Δ', "D"}, {'Ε', "E"},
            {'Ζ', "Z"}, {'Η', "H"}, {'Θ', "8"}, {'Ι', "I"}, {'Κ', "K"},
            {'Λ', "L"}, {'Μ', "M"}, {'Ν', "N"}, {'Ξ', "3"}, {'Ο', "O"},
            {'Π', "P"}, {'Ρ', "R"}, {'Σ', "S"}, {'Τ', "T"}, {'Υ', "Y"},
            {'Φ', "F"}, {'Χ', "X"}, {'Ψ', "PS"}, {'Ω', "W"},
            {'Ϊ', "I"}, {'Ϋ', "Y"},
            {'ά', "a"}, {'έ', "e"}, {'ή', "h"}, {'ί', "i"}, {'ΰ', "y"},
            {'α', "a"}, {'β', "b"}, {'γ', "g"}, {'δ', "d"}, {'ε', "e"},
            {'ζ', "z"}, {'η', "h"}, {'θ', "8"}, {'ι', "i"}, {'κ', "k"},
            {'λ', "l"}, {'μ', "m"}, {'ν', "n"}, {'ξ', "3"}, {'ο', "o"},
            {'π', "p"}, {'ρ', "r"}, {'ς', "s"}, {'σ', "s"}, {'τ', "t"},
            {'υ', "y"}, {'φ', "f"}, {'χ', "x"}, {'ψ', "ps"}, {'ω', "w"},
            {'ϊ', "i"}, {'ϋ', "y"}, {'ό', "o"}, {'ύ', "y"}, {'ώ', "w"},

            // Cyrillic
            {'Ё', "Yo"}, {'Ђ', "DJ"}, {'Є', "Ye"}, {'І', "I"}, {'Ї', "Yi"},
            {'Ј', "J"}, {'Љ', "LJ"}, {'Њ', "NJ"}, {'Ћ', "C"}, {'Џ', "DZ"},
            {'А', "A"}, {'Б', "B"}, {'В', "V"}, {'Г', "G"}, {'Д', "D"},
            {'Е', "E"}, {'Ж', "Zh"}, {'З', "Z"}, {'И', "I"}, {'Й', "J"},
            {'К', "K"}, {'Л', "L"}, {'М', "M"}, {'Н', "N"}, {'О', "O"},
            {'П', "P"}, {'Р', "R"}, {'С', "S"}, {'Т', "T"}, {'У', "U"},
            {'Ф', "F"}, {'Х', "H"}, {'Ц', "C"}, {'Ч', "Ch"}, {'Ш', "Sh"},
            {'Щ', "Sh"}, {'Ъ', "U"}, {'Ы', "Y"}, {'Ь', ""}, {'Э', "E"},
            {'Ю', "Yu"}, {'Я', "Ya"},
            {'а', "a"}, {'б', "b"}, {'в', "v"}, {'г', "g"}, {'д', "d"},
            {'е', "e"}, {'ж', "zh"}, {'з', "z"}, {'и', "i"}, {'й', "j"},
            {'к', "k"}, {'л', "l"}, {'м', "m"}, {'н', "n"}, {'о', "o"},
            {'п', "p"}, {'р', "r"}, {'с', "s"}, {'т', "t"}, {'у', "u"},
            {'ф', "f"}, {'х', "h"}, {'ц', "c"}, {'ч', "ch"}, {'ш', "sh"},
            {'щ', "sh"}, {'ъ', "u"}, {'ы', "y"}, {'ь', ""}, {'э', "e"},
            {'ю', "yu"}, {'я', "ya"},
            {'ё', "yo"}, {'ђ', "dj"}, {'є', "ye"}, {'і', "i"}, {'ї', "yi"},
            {'ј', "j"}, {'љ', "lj"}, {'њ', "nj"}, {'ћ', "c"}, {'ѝ', "u"}, {'џ', "dz"},
            {'Ґ', "G"}, {'ґ', "g"},

            // Kazakh Cyrillic
            {'Ғ', "GH"}, {'ғ', "gh"}, {'Қ', "KH"}, {'қ', "kh"},
            {'Ң', "NG"}, {'ң', "ng"}, {'Ү', "UE"}, {'ү', "ue"},
            {'Ұ', "U"}, {'ұ', "u"}, {'Һ', "H"}, {'һ', "h"},
            {'Ә', "AE"}, {'ә', "ae"}, {'Ө', "OE"}, {'ө', "oe"},

            // Armenian
            {'Ա', "A"}, {'Բ', "B"}, {'Գ', "G"}, {'Դ', "D"}, {'Ե', "E"},
            {'Զ', "Z"}, {'Է', "E'"}, {'Ը', "Y'"}, {'Թ', "T'"}, {'Ժ', "JH"},
            {'Ի', "I"}, {'Լ', "L"}, {'Խ', "X"}, {'Ծ', "C'"}, {'Կ', "K"},
            {'Հ', "H"}, {'Ձ', "D'"}, {'Ղ', "GH"}, {'Ճ', "TW"}, {'Մ', "M"},
            {'Յ', "Y"}, {'Ն', "N"}, {'Շ', "SH"}, {'Չ', "CH"}, {'Պ', "P"},
            {'Ջ', "J"}, {'Ռ', "R'"}, {'Ս', "S"}, {'Վ', "V"}, {'Տ', "T"},
            {'Ր', "R"}, {'Ց', "C"}, {'Փ', "P'"}, {'Ք', "Q'"}, {'Օ', "O''"},
            {'Ֆ', "F"}, {'և', "EV"},

            // Arabic / Persian: intentionally NOT mapped — preserve original characters.
            // They are kept by Strict regex [^\p{L}\p{N}\s] (Arabic letters are \p{L}, Arabic-Indic digits are \p{N}).
            // Diacritics (ً-ْ) are \p{M} and will be stripped by Strict, which is fine for slug purposes.

            // Thai (baht symbol only)
            {'฿', "baht"},

            // Georgian
            {'ა', "a"}, {'ბ', "b"}, {'გ', "g"}, {'დ', "d"}, {'ე', "e"},
            {'ვ', "v"}, {'ზ', "z"}, {'თ', "t"}, {'ი', "i"}, {'კ', "k"},
            {'ლ', "l"}, {'მ', "m"}, {'ნ', "n"}, {'ო', "o"}, {'პ', "p"},
            {'ჟ', "zh"}, {'რ', "r"}, {'ს', "s"}, {'ტ', "t"}, {'უ', "u"},
            {'ფ', "f"}, {'ქ', "k"}, {'ღ', "gh"}, {'ყ', "q"}, {'შ', "sh"},
            {'ჩ', "ch"}, {'ც', "ts"}, {'ძ', "dz"}, {'წ', "ts"}, {'ჭ', "ch"},
            {'ხ', "kh"}, {'ჯ', "j"}, {'ჰ', "h"},

            // Vietnamese & Latin Extended Additional
            {'Ṣ', "S"}, {'ṣ', "s"},
            {'Ẁ', "W"}, {'ẁ', "w"}, {'Ẃ', "W"}, {'ẃ', "w"}, {'Ẅ', "W"}, {'ẅ', "w"},
            {'ẞ', "SS"},
            {'Ạ', "A"}, {'ạ', "a"}, {'Ả', "A"}, {'ả', "a"},
            {'Ấ', "A"}, {'ấ', "a"}, {'Ầ', "A"}, {'ầ', "a"},
            {'Ẩ', "A"}, {'ẩ', "a"}, {'Ẫ', "A"}, {'ẫ', "a"},
            {'Ậ', "A"}, {'ậ', "a"}, {'Ắ', "A"}, {'ắ', "a"},
            {'Ằ', "A"}, {'ằ', "a"}, {'Ẳ', "A"}, {'ẳ', "a"},
            {'Ẵ', "A"}, {'ẵ', "a"}, {'Ặ', "A"}, {'ặ', "a"},
            {'Ẹ', "E"}, {'ẹ', "e"}, {'Ẻ', "E"}, {'ẻ', "e"},
            {'Ẽ', "E"}, {'ẽ', "e"}, {'Ế', "E"}, {'ế', "e"},
            {'Ề', "E"}, {'ề', "e"}, {'Ể', "E"}, {'ể', "e"},
            {'Ễ', "E"}, {'ễ', "e"}, {'Ệ', "E"}, {'ệ', "e"},
            {'Ỉ', "I"}, {'ỉ', "i"}, {'Ị', "I"}, {'ị', "i"},
            {'Ọ', "O"}, {'ọ', "o"}, {'Ỏ', "O"}, {'ỏ', "o"},
            {'Ố', "O"}, {'ố', "o"}, {'Ồ', "O"}, {'ồ', "o"},
            {'Ổ', "O"}, {'ổ', "o"}, {'Ỗ', "O"}, {'ỗ', "o"},
            {'Ộ', "O"}, {'ộ', "o"}, {'Ớ', "O"}, {'ớ', "o"},
            {'Ờ', "O"}, {'ờ', "o"}, {'Ở', "O"}, {'ở', "o"},
            {'Ỡ', "O"}, {'ỡ', "o"}, {'Ợ', "O"}, {'ợ', "o"},
            {'Ụ', "U"}, {'ụ', "u"}, {'Ủ', "U"}, {'ủ', "u"},
            {'Ứ', "U"}, {'ứ', "u"}, {'Ừ', "U"}, {'ừ', "u"},
            {'Ử', "U"}, {'ử', "u"}, {'Ữ', "U"}, {'ữ', "u"},
            {'Ự', "U"}, {'ự', "u"}, {'Ỳ', "Y"}, {'ỳ', "y"},
            {'Ỵ', "Y"}, {'ỵ', "y"}, {'Ỷ', "Y"}, {'ỷ', "y"},
            {'Ỹ', "Y"}, {'ỹ', "y"},

            // Punctuation & typography
            {'–', "-"}, {'‘', "'"}, {'’', "'"}, {'“', "\""}, {'”', "\""},
            {'„', "\""}, {'†', "+"}, {'•', "*"}, {'…', "..."},

            // Currencies
            {'₠', "ecu"}, {'₢', "cruzeiro"}, {'₣', "french franc"}, {'₤', "lira"},
            {'₥', "mill"}, {'₦', "naira"}, {'₧', "peseta"}, {'₨', "rupee"},
            {'₩', "won"}, {'₪', "new shequel"}, {'₫', "dong"}, {'€', "euro"},
            {'₭', "kip"}, {'₮', "tugrik"}, {'₯', "drachma"}, {'₰', "penny"},
            {'₱', "peso"}, {'₲', "guarani"}, {'₳', "austral"}, {'₴', "hryvnia"},
            {'₵', "cedi"}, {'₸', "kazakhstani tenge"}, {'₹', "indian rupee"},
            {'₺', "turkish lira"}, {'₽', "russian ruble"}, {'₿', "bitcoin"},

            // Misc symbols
            {'℠', "sm"}, {'™', "tm"}, {'∂', "d"}, {'∆', "delta"},
            {'∑', "sum"}, {'∞', "infinity"}, {'♥', "love"},

            // CJK punctuation → space (becomes replacement char)
            {'、', " "},  // 、 enumeration comma
            {'。', " "},  // 。 period
            {'，', " "},  // ， fullwidth comma
            {'！', " "},  // ！ fullwidth exclamation
            {'？', " "},  // ？ fullwidth question mark
            {'：', " "},  // ： fullwidth colon
            {'；', " "},  // ； fullwidth semicolon
            {'「', " "},  // 「 left corner bracket
            {'」', " "},  // 」 right corner bracket
            {'『', " "},  // 『 left double corner bracket
            {'』', " "},  // 』 right double corner bracket
            {'【', " "},  // 【 left black lenticular bracket
            {'】', " "},  // 】 right black lenticular bracket
            {'（', " "},  // （ fullwidth left paren
            {'）', " "},  // ） fullwidth right paren
            {'—', " "},  // — em dash
            {'―', " "},  // ― horizontal bar
            {'·', " "},  // · middle dot

            // Arabic ligatures and Rial symbol intentionally NOT mapped — preserve original.
        };
    }

    private static Dictionary<string, Dictionary<char, string>> BuildLocales()
    {
        return new Dictionary<string, Dictionary<char, string>>
        {
            ["bg"] = new Dictionary<char, string>
            {
                {'Й', "Y"}, {'Ц', "Ts"}, {'Щ', "Sht"}, {'Ъ', "A"}, {'Ь', "Y"},
                {'й', "y"}, {'ц', "ts"}, {'щ', "sht"}, {'ъ', "a"}, {'ь', "y"},
            },
            ["de"] = new Dictionary<char, string>
            {
                {'Ä', "AE"}, {'ä', "ae"}, {'Ö', "OE"}, {'ö', "oe"},
                {'Ü', "UE"}, {'ü', "ue"}, {'ß', "ss"},
                {'%', "prozent"}, {'&', "und"}, {'|', "oder"},
                {'∑', "summe"}, {'∞', "unendlich"}, {'♥', "liebe"},
            },
            ["es"] = new Dictionary<char, string>
            {
                {'%', "por ciento"}, {'&', "y"}, {'<', "menor que"}, {'>', "mayor que"}, {'|', "o"},
                {'¢', "centavos"}, {'£', "libras"}, {'¤', "moneda"},
                {'₣', "francos"}, {'∑', "suma"}, {'∞', "infinito"}, {'♥', "amor"},
            },
            ["fr"] = new Dictionary<char, string>
            {
                {'%', "pourcent"}, {'&', "et"}, {'<', "plus petit"}, {'>', "plus grand"}, {'|', "ou"},
                {'¢', "centime"}, {'£', "livre"}, {'¤', "devise"},
                {'₣', "franc"}, {'∑', "somme"}, {'∞', "infini"}, {'♥', "amour"},
            },
            ["pt"] = new Dictionary<char, string>
            {
                {'%', "porcento"}, {'&', "e"}, {'<', "menor"}, {'>', "maior"}, {'|', "ou"},
                {'¢', "centavo"}, {'∑', "soma"}, {'£', "libra"},
                {'∞', "infinito"}, {'♥', "amor"},
            },
            ["uk"] = new Dictionary<char, string>
            {
                {'И', "Y"}, {'и', "y"}, {'Й', "Y"}, {'й', "y"},
                {'Ц', "Ts"}, {'ц', "ts"}, {'Х', "Kh"}, {'х', "kh"},
                {'Щ', "Shch"}, {'щ', "shch"}, {'Г', "H"}, {'г', "h"},
            },
            ["vi"] = new Dictionary<char, string>
            {
                {'Đ', "D"}, {'đ', "d"},
            },
            ["da"] = new Dictionary<char, string>
            {
                {'Ø', "OE"}, {'ø', "oe"}, {'Å', "AA"}, {'å', "aa"},
                {'%', "procent"}, {'&', "og"}, {'|', "eller"}, {'$', "dollar"},
                {'<', "mindre end"}, {'>', "større end"},
            },
            ["nb"] = new Dictionary<char, string>
            {
                {'&', "og"}, {'Å', "AA"}, {'Æ', "AE"}, {'Ø', "OE"},
                {'å', "aa"}, {'æ', "ae"}, {'ø', "oe"},
            },
            ["it"] = new Dictionary<char, string>
            {
                {'&', "e"},
            },
            ["nl"] = new Dictionary<char, string>
            {
                {'&', "en"},
            },
            ["sv"] = new Dictionary<char, string>
            {
                {'&', "och"}, {'Å', "AA"}, {'Ä', "AE"}, {'Ö', "OE"},
                {'å', "aa"}, {'ä', "ae"}, {'ö', "oe"},
            },
        };
    }
}
