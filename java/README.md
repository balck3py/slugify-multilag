# slugify-multilang for Java

A dependency-free Java port of the C# `Slugify.MultiLang` library. It preserves
the same 583 character mappings, 12 locale override maps, option defaults, and
Unicode slug-generation pipeline.

## Maven coordinates

```xml
<dependency>
  <groupId>io.github.balck3py</groupId>
  <artifactId>slugify-multilang</artifactId>
  <version>1.0.0</version>
</dependency>
```

The coordinates are configured for a future Maven publication. Until then,
build this module locally with `mvn package` from `java/`.

## Usage

```java
import io.github.balck3py.slugify.multilang.SlugifyHelper;
import io.github.balck3py.slugify.multilang.SlugifySlugOptions;

String slug = SlugifyHelper.slugify("Hello, World!");
// "hello-world"

SlugifySlugOptions options = new SlugifySlugOptions();
options.setLocale("de");
String german = SlugifyHelper.slugify("Müller & Söhne", options);
// "mueller-und-soehne"

String underscored = SlugifyHelper.slugify("Some Text", "_");
// "some_text"
```

## Options

`SlugifySlugOptions` maps directly to the C# options:

| Java property | Default | Meaning |
| --- | --- | --- |
| `replacement` | `"-"` | Replaces whitespace and separators. |
| `remove` | `null` | Custom `Pattern` used to strip characters. |
| `lower` | `true` | Lowercases using `Locale.ROOT`. |
| `strict` | `true` | Keeps Unicode letters, numbers, and whitespace only. |
| `trim` | `true` | Trims Unicode whitespace before joining. |
| `locale` | `null` | Applies a locale-specific override map. |

Locale maps are available for `bg`, `de`, `es`, `fr`, `pt`, `uk`, `vi`, `da`,
`nb`, `it`, `nl`, and `sv`.

## Extend the character map

```java
import java.util.Collections;

SlugifyHelper.extend(Collections.singletonMap('☂', "umbrella"));
SlugifyHelper.slugify("☂ rain");
// "umbrella-rain"
```

## Compatibility notes

The implementation NFC-normalizes input, applies locale mappings before the
global map, filters characters, optionally applies strict Unicode filtering,
then trims, joins whitespace, and lowercases. CJK and Arabic letters are kept;
Thai and Devanagari combining marks are removed when strict mode is enabled.
