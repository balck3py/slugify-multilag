# slugify-multilang

A multi-language, dependency-free slug generator for JavaScript and TypeScript.
This is a faithful frontend port of the repository's C# `Slugify.MultiLang`
library: it shares the same character map, locale overrides, defaults, and
Unicode handling.

It transliterates accented Latin, Greek, Cyrillic, Armenian, Georgian,
Vietnamese, currencies, and common symbols. Scripts without meaningful
romanization—including Chinese, Japanese, Korean, Arabic, Thai, and
Devanagari—are preserved so generated URLs remain readable.

## Features

- No runtime dependencies; works in browser applications and Node.js.
- Unicode NFC normalization for equivalent composed/decomposed input.
- Transliteration for Latin Extended, Greek, Cyrillic (including Kazakh),
  Armenian, Georgian, and Vietnamese.
- Locale-specific overrides for Bulgarian, German, Spanish, French,
  Portuguese, Ukrainian, Vietnamese, Danish, Norwegian Bokmål, Italian,
  Dutch, and Swedish.
- Readable passthrough for CJK, Arabic, Thai, and Devanagari letters.
- Configurable separator, casing, strict filtering, trimming, removal regex,
  and global character-map extensions.

## Install

```bash
npm install slugify-multilang
```

## Usage

```ts
import { extend, slugify, SlugifyHelper } from "slugify-multilang";

slugify("Hello, World!");
// "hello-world"

slugify("Café au lait & cròissant");
// "cafe-au-lait-and-croissant"

slugify("傅总：你的马甲 又又又掉了！");
// "傅总-你的马甲-又又又掉了"

slugify("Müller & Söhne", { Locale: "de" });
// "mueller-und-soehne"

slugify("Some Text", "_");
// "some_text"

extend({ "☂": "umbrella" });
SlugifyHelper.Slugify("☂ rain");
// "umbrella-rain"
```

The package supports both ESM and CommonJS:

```js
const { slugify } = require("slugify-multilang");
```

## Options

Option names intentionally match the C# API.

| Option | Type | Default | Description |
| --- | --- | --- | --- |
| `Replacement` | `string` | `"-"` | Replaces whitespace and separators. |
| `Remove` | `RegExp \| null` | `null` | Custom character-removal expression. |
| `Lower` | `boolean` | `true` | Lowercases the final slug. |
| `Strict` | `boolean` | `true` | Retains Unicode letters, numbers, and whitespace only. |
| `Trim` | `boolean` | `true` | Removes leading and trailing whitespace. |
| `Locale` | `string \| null` | `null` | Applies locale-specific overrides. |

Locale maps are available for `bg`, `de`, `es`, `fr`, `pt`, `uk`, `vi`, `da`,
`nb`, `it`, `nl`, and `sv`.

```ts
slugify("Müller & Söhne", {
  Locale: "de",       // ü → ue, ö → oe, & → und
  Replacement: "-",
  Lower: true,
  Strict: true,
  Trim: true,
});
// "mueller-und-soehne"
```

Unknown locale codes use the global character map without throwing an error.

## Extend the Character Map

`extend` changes the module-wide map for later calls. It accepts either a plain
object or a `Map` whose keys are individual characters.

```ts
import { extend, slugify } from "slugify-multilang";

extend({
  "☂": "umbrella",
  "♛": "queen",
});

slugify("☂ ♛");
// "umbrella-queen"
```

## How It Works

Each input is NFC-normalized, translated character by character (locale map,
then global map, then passthrough), filtered, optionally strict-filtered,
trimmed, converted to the replacement separator, and optionally lowercased.
With default strict mode, combining marks are stripped while Unicode letters
such as Arabic and CJK characters are preserved.

| Input | Output |
| --- | --- |
| `"Café au lait & cròissant"` | `"cafe-au-lait-and-croissant"` |
| `"Директор Фу"` | `"direktor-fu"` |
| `"傅总：你的马甲"` | `"傅总-你的马甲"` |
| `"المدير فو"` | `"المدير-فو"` |

When `Strict` is `false`, characters retained by the removal pattern, such as
underscores, remain in the result. With `Strict: true` (the default), only
Unicode letters, numbers, and whitespace remain before separators are applied.

## 23-Language Examples

The following examples use the same source phrase in each language. Outputs
match the C# and Python ports in this repository.

### Locale-specific overrides

| Language | `Locale` | Input | Output |
| --- | --- | --- | --- |
| Spanish | `es` | `Director Fu: Tu cuenta alternativa ha quedado expuesta otra vez` | `director-fu-tu-cuenta-alternativa-ha-quedado-expuesta-otra-vez` |
| Portuguese | `pt` | `Diretor Fu: Sua conta alternativa foi exposta de novo` | `diretor-fu-sua-conta-alternativa-foi-exposta-de-novo` |
| French | `fr` | `Directeur Fu: Votre compte alternatif a été exposé encore` | `directeur-fu-votre-compte-alternatif-a-ete-expose-encore` |
| German | `de` | `Direktor Fu: Dein Alternativkonto ist aufgeflogen` | `direktor-fu-dein-alternativkonto-ist-aufgeflogen` |
| Italian | `it` | `Direttore Fu: Il tuo account alternativo è stato smascherato` | `direttore-fu-il-tuo-account-alternativo-e-stato-smascherato` |
| Swedish | `sv` | `Direktör Fu: Ditt alternativa konto har avslöjats` | `direktoer-fu-ditt-alternativa-konto-har-avsloejats` |
| Danish | `da` | `Direktør Fu: Din alternative konto er blevet afsløret` | `direktoer-fu-din-alternative-konto-er-blevet-afsloeret` |
| Dutch | `nl` | `Directeur Fu: Uw alternatieve account is ontmaskerd` | `directeur-fu-uw-alternatieve-account-is-ontmaskerd` |
| Vietnamese | `vi` | `Giám đốc Phú: Tài khoản phụ của bạn đã bị lộ` | `giam-doc-phu-tai-khoan-phu-cua-ban-da-bi-lo` |

### Global character-map fallback

These locale labels have no dedicated override map (except `nb` for Norwegian
Bokmål), so the global map provides the transliteration.

| Language | `Locale` | Input | Output |
| --- | --- | --- | --- |
| Polish | `pl` | `Dyrektorze Fu: Twoje alternatywne konto zostało zdemaskowane` | `dyrektorze-fu-twoje-alternatywne-konto-zostalo-zdemaskowane` |
| Norwegian | `no` | `Direktor Fu: Den alternative kontoen din har blitt avslørt` | `direktor-fu-den-alternative-kontoen-din-har-blitt-avslort` |
| Turkish | `tr` | `Müdür Fu: Sahte hesabın deşifre oldu` | `mudur-fu-sahte-hesabin-desifre-oldu` |
| Malay | `ms` | `Pengarah Fu: Akaun tiruan anda telah terdedah` | `pengarah-fu-akaun-tiruan-anda-telah-terdedah` |
| Indonesian | `id` | `Direktur Fu: Akun samaran Anda telah terbongkar` | `direktur-fu-akun-samaran-anda-telah-terbongkar` |
| Filipino | `tl` | `Direktor Fu: Ang iyong account ay nabunyag` | `direktor-fu-ang-iyong-account-ay-nabunyag` |
| English | `en` | `Director Fu: Your alt account got exposed again` | `director-fu-your-alt-account-got-exposed-again` |

### Unicode script preservation

These scripts have no global transliteration map. Their letters remain in the
slug, while mapped punctuation becomes the configured separator.

| Language | `Locale` | Input | Output |
| --- | --- | --- | --- |
| Arabic | `ar` | `المدير فو: تم كشف حسابك البديل` | `المدير-فو-تم-كشف-حسابك-البديل` |
| Japanese | `ja` | `傅総：あなたのサブアカウントがバレちゃった` | `傅総-あなたのサブアカウントがバレちゃった` |
| Korean | `ko` | `푸 총재: 당신의 부계정이 들통났어요` | `푸-총재-당신의-부계정이-들통났어요` |
| Thai | `th` | `ผู้อำนวยการฟู: บัญชีอำพรางของคุณถูกเปิดเผย` | `ผอำนวยการฟ-บญชอำพรางของคณถกเปดเผย` |
| Hindi | `hi` | `निदेशक फू: आपका वैकल्पिक खाता उजागर हो गया` | `नदशक-फ-आपक-वकलपक-खत-उजगर-ह-गय` |
| Simplified Chinese | `zh` | `傅总：你的马甲又掉了` | `傅总-你的马甲又掉了` |
| Traditional Chinese | `zh-tw` | `傅總：你的馬甲又掉了` | `傅總-你的馬甲又掉了` |

Thai and Devanagari combining vowel and tone marks are removed by default
strict mode. Set `Strict: false` if retaining those marks is more important
than strict URL filtering.

## API

```ts
function slugify(input: string, replacement?: string): string;
function slugify(input: string, options?: SlugifySlugOptions): string;
function extend(customMap: Record<string, string> | Map<string, string>): void;
```

For callers that prefer the original static-class naming, `SlugifyHelper`
exposes `SlugifyHelper.Slugify` and `SlugifyHelper.Extend`.

## Compatibility

The package ships ESM, CommonJS, and TypeScript declaration files. It is
intended for Vue, React, Angular, vanilla browser bundlers, and Node.js.
Browser environments must support ES2020 and Unicode property escapes.

## License

[MIT](../LICENSE)
