# Slugify.MultiLang

> A multi-language, dependency-free slug generator for .NET — a faithful C# port of the popular [`slugify`](https://github.com/simov/slugify) JavaScript library, with extended locale support and sensible handling of CJK, Arabic, and other non-Latin scripts.

[English](./README.md) | [简体中文](./README.zh-CN.md)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)
[![.NET Standard 2.0](https://img.shields.io/badge/.NET%20Standard-2.0-512BD4.svg)](https://learn.microsoft.com/dotnet/standard/net-standard)

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

## Installation

This repository ships the source directly. Add a project reference to `csharp/src/Slugify.MultiLang/Slugify.MultiLang.csproj`, or drop `SlugifyHelper.cs` and `SlugifySlugOptions.cs` into your project.

```xml
<ItemGroup>
  <ProjectReference Include="path/to/Slugify.MultiLang/Slugify.MultiLang.csproj" />
</ItemGroup>
```

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

## Demo

A runnable console demo showing 20+ languages lives in [`csharp/demo`](./csharp/demo):

```bash
cd csharp
dotnet run --project demo/Slugify.MultiLang.Demo
```

## Project layout

```
csharp/
├── Slugify.MultiLang.slnx              # solution
├── src/Slugify.MultiLang/             # the library (netstandard2.0)
│   ├── SlugifyHelper.cs               # core logic + charmaps + locale maps
│   └── SlugifySlugOptions.cs          # options
└── demo/Slugify.MultiLang.Demo/       # multi-language console demo (net8.0)
```

## Credits

Charmap and behavior are derived from [simov/slugify](https://github.com/simov/slugify) (MIT).

## License

Released under the [MIT License](./LICENSE) — free for any use, including commercial. Do whatever you like with it.
