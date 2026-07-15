# Slugify.MultiLang

> A multi-language, dependency-free slug generator for .NET, Python, JavaScript/TypeScript, and Java — with extended locale support and sensible handling of CJK, Arabic, and other non-Latin scripts.

[English](./README.md) | [简体中文](./README.zh-CN.md)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)
[![.NET Standard 2.0](https://img.shields.io/badge/.NET%20Standard-2.0-512BD4.svg)](https://learn.microsoft.com/dotnet/standard/net-standard)

## Implementations

| Language | Location | Package |
|---|---|---|
| **C# / .NET** | [`csharp/`](./csharp) | [NuGet](https://www.nuget.org/packages/Slugify.MultiLang) — `Slugify.MultiLang` |
| **Python (3.10+)** | [`python/`](./python) — [README](./python/README.md) | [PyPI](https://pypi.org/project/slugify-multilang/) — `slugify-multilang` |
| **JavaScript / TypeScript** | [`js/`](./js) — [README](./js/README.md) | [npm](https://www.npmjs.com/package/slugify-multilang) — `slugify-multilang` |
| **Java (8+)** | [`java/`](./java) — [README](./java/README.md) | Maven coordinates — `io.github.balck3py:slugify-multilang` |

All implementations share the same charmap, locale overrides, defaults, and slug-generation behavior.

## What it does

Turn any string into a clean, URL-safe slug:

```
"Director Fu: Your alt account got exposed again!"  →  "director-fu-your-alt-account-got-exposed-again"
"Café au lait & cròissant"                          →  "cafe-au-lait-and-croissant"
"傅总：你的马甲 又又又掉了！"                          →  "傅总-你的马甲-又又又掉了"
```

It transliterates accented Latin, Greek, Cyrillic, Armenian, Georgian, Vietnamese and more into ASCII, maps common symbols and currencies to words, applies locale-specific rules, and preserves scripts that have no meaningful romanization (CJK, Arabic, Devanagari, Thai, …) so slugs stay readable in any language.

## Features

- **No dependencies** — a single static helper, targets **.NET Standard 2.0** (works on .NET Framework 4.6.1+, .NET Core, .NET 5–8+, Mono, Xamarin, Unity).
- **Huge built-in charmap** — Latin (incl. Extended), Greek, Cyrillic (incl. Kazakh), Armenian, Georgian, Vietnamese, currencies, punctuation, and math/misc symbols.
- **Locale-aware** — per-language overrides for `bg`, `de`, `es`, `fr`, `pt`, `uk`, `vi`, `da`, `nb`, `it`, `nl`, `sv` (e.g. German `ä → ae`, `& → und`).
- **Script-preserving** — CJK / Arabic / Devanagari / Thai letters are kept as-is rather than dropped.
- **Configurable** — replacement char, lowercasing, strict mode, trimming, and a custom remove-regex.
- **Extensible** — register your own character mappings at runtime via `Extend`.

## How It Works

Every string goes through a five-step pipeline:

1. **NFC normalize** — Unicode Normalization Form C is applied so that pre-composed and decomposed forms are treated identically.
2. **Per-character translation** — each character (including surrogate pairs for supplementary-plane code points) is looked up in order:
   1. *Locale override map* — if `Locale` is set and that locale has an entry for the character.
   2. *Global charmap* — Latin Extended, Greek, Cyrillic, Armenian, Georgian, Vietnamese, currencies, math symbols, CJK punctuation, …
   3. *Passthrough* — characters with no entry are kept as-is.
3. **Remove pass** — the built-in (or custom `Remove`) regex strips characters not useful in a URL.
4. **Strict pass** (`Strict = true` by default) — the Unicode-aware pattern `[^\p{L}\p{N}\s]` removes anything that is not a letter, digit, or whitespace. This is how non-Latin scripts are **preserved**: Arabic, CJK, Hangul, Kana, Thai consonants, and Devanagari consonants all satisfy `\p{L}` and survive untouched.
5. **Finalize** — whitespace runs are collapsed into the replacement char (default `-`); optional trim and lower-case are applied.

### Script-handling strategies

| Strategy | Scripts / locales | Detail |
|---|---|---|
| **Full charmap transliteration** | Latin Extended, Greek, Cyrillic (incl. Kazakh), Armenian, Georgian | Non-ASCII letter → ASCII (e.g. `é→e`, `Ж→Zh`, `Ñ→N`) |
| **Locale-specific overrides** | `de`, `es`, `fr`, `pt`, `it`, `nl`, `sv`, `da`, `nb`, `bg`, `uk`, `vi` | Language-correct mapping before global charmap (e.g. `de`: `ü→ue`, `&→und`; `sv`: `ö→oe`, `&→och`; `da`: `ø→oe`, `å→aa`) |
| **Charmap fallback** | `pl`, `tr`, `ms`, `id`, `tl`, and any unregistered locale | Global charmap only; diacritics not in the map pass through then get stripped by strict mode |
| **Script-preserving passthrough** | Arabic, Chinese, Japanese, Korean, Thai, Devanagari | No charmap entry — letters survive via `\p{L}`; CJK punctuation (`，`, `。`, `！`, `？`, `：`, `「」`, `【】`, `—`, `·` …) is mapped to spaces and becomes the separator |

### Thai and Devanagari (Hindi) — combining marks stripped

Thai base consonants and Devanagari base consonants are Unicode letters (`\p{L}`) and survive intact.
Their combining vowel signs and tone marks are Unicode *combining marks* (`\p{M}`) — e.g. Thai `้` `ู` `็` or Devanagari `ि` `ा` `ी`.
With `Strict = true` (the default) these marks are removed, leaving consonant skeletons in the slug.
Set `Strict = false` if phonetically complete Thai / Devanagari slugs are needed.

### Arabic — letters preserved, diacritics dropped

Arabic letters satisfy `\p{L}` and are preserved. Harakat (short-vowel diacritics such as `َ` `ِ` `ُ`) are combining marks (`\p{M}`) and are stripped by strict mode, which is fine for slug purposes since Arabic URLs routinely omit them.

## Installation

This repository ships the source directly. Add a project reference to `csharp/src/Slugify.MultiLang/Slugify.MultiLang.csproj`, or drop `SlugifyHelper.cs` and `SlugifySlugOptions.cs` into your project.

```xml
<ItemGroup>
  <ProjectReference Include="path/to/Slugify.MultiLang/Slugify.MultiLang.csproj" />
</ItemGroup>
```

### JavaScript / TypeScript

```bash
npm install slugify-multilang
```

### Java

```xml
<dependency>
  <groupId>io.github.balck3py</groupId>
  <artifactId>slugify-multilang</artifactId>
  <version>1.0.0</version>
</dependency>
```

The Maven coordinates are configured for a future publication. Build the
module locally with `cd java && mvn package` until it is published.

## Usage

```csharp
using Slugify.MultiLang;

// Extension-method style (default options)
string slug = "Hello, World!".Slugify();
// → "hello-world"

// Static call with options
string s = SlugifyHelper.Slugify("Müller & Söhne", new SlugifySlugOptions
{
    Locale = "de"   // German: ü → ue, ö → oe, & → und
});
// → "mueller-und-soehne"

// Custom replacement character
"Some Text".Slugify("_");
// → "some_text"
```

### Options

All options live on `SlugifySlugOptions`:

| Option        | Type     | Default | Description                                                                 |
| ------------- | -------- | ------- | --------------------------------------------------------------------------- |
| `Replacement` | `string` | `"-"`   | Character that replaces whitespace and separators.                          |
| `Lower`       | `bool`   | `true`  | Lowercase the result (invariant culture).                                   |
| `Strict`      | `bool`   | `true`  | Remove anything that is not a letter, number, or whitespace.                |
| `Trim`        | `bool`   | `true`  | Trim leading/trailing whitespace before joining.                            |
| `Locale`      | `string?`| `null`  | Locale code to apply language-specific overrides.                           |
| `Remove`      | `Regex?` | `null`  | Custom regex of characters to strip (overrides the default remove pattern). |

### Extending the charmap

```csharp
SlugifyHelper.Extend(new Dictionary<char, string>
{
    { '☂', "umbrella" },
    { '♛', "queen" },
});

"☂♛".Slugify(); // → "umbrella-queen"
```

### JavaScript / TypeScript usage

```ts
import { extend, slugify, SlugifyHelper } from "slugify-multilang";

slugify("Hello, World!");
// → "hello-world"

slugify("Müller & Söhne", { Locale: "de" });
// → "mueller-und-soehne"

slugify("Some Text", "_");
// → "some_text"

extend({ "☂": "umbrella" });
SlugifyHelper.Slugify("☂ rain");
// → "umbrella-rain"
```

The JavaScript options use the same PascalCase names as C#: `Replacement`,
`Remove`, `Lower`, `Strict`, `Trim`, and `Locale`. It ships ESM, CommonJS, and
TypeScript declaration files. See the [JavaScript README](./js/README.md) for
the full API and 23-language examples.

### Java usage

```java
import io.github.balck3py.slugify.multilang.SlugifyHelper;
import io.github.balck3py.slugify.multilang.SlugifySlugOptions;

String slug = SlugifyHelper.slugify("Hello, World!");
// → "hello-world"

SlugifySlugOptions options = new SlugifySlugOptions();
options.setLocale("de");
String german = SlugifyHelper.slugify("Müller & Söhne", options);
// → "mueller-und-soehne"

SlugifyHelper.slugify("Some Text", "_");
// → "some_text"
```

The Java API uses `SlugifySlugOptions` getters and setters for the same six
options. See the [Java README](./java/README.md) for Maven and API details.

## Demo

A runnable console demo showing 23 languages lives in [`csharp/demo`](./csharp/demo):

```bash
cd csharp
dotnet run --project demo/Slugify.MultiLang.Demo
```

All examples below use the same source sentence — "傅总：你的马甲 又又又掉了！" ("Director Fu: Your alt account got exposed again and again and again!") — translated into each target language.

### With explicit locale mapping

| Language | Locale | Slug output |
|---|---|---|
| Español | `es` | `director-fu-tu-cuenta-alternativa-ha-quedado-expuesta-otra-vez-y-otra-vez-y-otra-vez` |
| Português | `pt` | `diretor-fu-sua-conta-alternativa-foi-exposta-de-novo-e-de-novo-e-de-novo` |
| Français | `fr` | `directeur-fu-votre-compte-alternatif-a-ete-expose-encore-et-encore-et-encore` |
| Deutsch | `de` | `direktor-fu-dein-alternativkonto-ist-schon-wieder-und-wieder-und-wieder-aufgeflogen` |
| Italiano | `it` | `direttore-fu-il-tuo-account-alternativo-e-stato-smascherato-ancora-e-ancora-e-ancora` |
| Svenska | `sv` | `direktoer-fu-ditt-alternativa-konto-har-avsloejats-igen-och-igen-och-igen` |
| Dansk | `da` | `direktoer-fu-din-alternative-konto-er-blevet-afsloeret-igen-og-igen-og-igen` |
| Nederlands | `nl` | `directeur-fu-uw-alternatieve-account-is-alweer-en-nog-een-keer-ontmaskerd` |
| Tiếng Việt | `vi` | `giam-doc-phu-tai-khoan-phu-cua-ban-da-bi-lo-lai-va-lai-va-lai` |

> **`vi` note:** the locale entry only maps `Đ/đ → D/d`; the extensive Vietnamese diacritics (`ấ`, `ề`, `ộ`, …) are handled by the global charmap.

### Charmap fallback (no dedicated locale entry)

| Language | Locale | Slug output |
|---|---|---|
| Polski | `pl` | `dyrektorze-fu-twoje-alternatywne-konto-zostalo-ponownie-i-ponownie-zdemaskowane` |
| Norsk | `no` | `direktor-fu-den-alternative-kontoen-din-har-blitt-avslort-igjen-og-igjen-og-igjen` |
| Türkçe | `tr` | `mudur-fu-sahte-hesabin-yine-yine-yine-desifre-oldu` |
| Bahasa Melayu | `ms` | `pengarah-fu-akaun-tiruan-anda-telah-terdedah-lagi-dan-lagi-dan-lagi` |
| Bahasa Indonesia | `id` | `direktur-fu-akun-samaran-anda-telah-terbongkar-lagi-dan-lagi-dan-lagi` |
| Filipino | `tl` | `direktor-fu-ang-iyong-alternatibong-account-ay-nabunyag-na-naman-at-naman-at-naman` |
| English | `en` | `director-fu-your-alt-account-got-exposed-again-and-again-and-again` |

> **`no` note:** Norwegian Bokmål (`nb`) **does** have a locale entry. The demo passes `no` which falls back to charmap. Use `Locale = "nb"` for proper Bokmål mapping (`ø→oe`, `å→aa`, `&→og`).

### Script-preserving (Unicode passthrough)

Non-Latin scripts with no charmap entries are preserved via `\p{L}`:

| Language | Locale | Slug output |
|---|---|---|
| العربية | `ar` | `المدير-فو-لقد-تم-كشف-حسابك-البديل-مرة-أخرى-ومرة-أخرى-ومرة-أخرى` |
| 日本語 | `ja` | `傅総-あなたのサブアカウントがまたまたまたバレちゃった` |
| 한국어 | `ko` | `푸-총재-당신의-부계정이-또-또-또-들통났어요` |
| ภาษาไทย | `th` † | `ผอำนวยการฝ-บญชอำพรางของคณถกเปดเผยอกและอกและอกครง` |
| हिन्दी | `hi` † | `नदशक-फ-आपक-वकलपक-खत-फर-और-फर-और-फर-उजगर-ह-गय` |
| 中文 (简体) | `zh` | `傅总-你的马甲-又又又掉了` |
| 中文 (繁體) | `zh-tw` | `傅總-你的馬甲-又又又掉了` |

† Combining vowel marks (`\p{M}`) are stripped by strict mode — see [Thai and Devanagari note](#thai-and-devanagari-hindi--combining-marks-stripped) in the _How It Works_ section.

## Project layout

```
csharp/
├── Slugify.MultiLang.slnx              # solution
├── src/Slugify.MultiLang/             # the library (netstandard2.0)
│   ├── SlugifyHelper.cs               # core logic + charmaps + locale maps
│   └── SlugifySlugOptions.cs          # options
└── demo/Slugify.MultiLang.Demo/       # multi-language console demo (net8.0)
js/
├── src/                               # TypeScript implementation and mapping data
├── test.html                          # browser playground for the local build
└── README.md                          # npm package documentation
java/
├── src/main/java/                     # Java 8 implementation and mapping data
├── pom.xml                            # Maven coordinates and build metadata
└── README.md                          # Maven package documentation
```

## Credits

Charmap and behavior are derived from [simov/slugify](https://github.com/simov/slugify) (MIT).

## License

Released under the [MIT License](./LICENSE) — free for any use, including commercial. Do whatever you like with it.
