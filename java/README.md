# slugify-multilang (Java)

> A faithful, **dependency-free** Java port of the C# [`Slugify.MultiLang`](../csharp) library — a multi-language, URL-safe slug generator with extended locale support and sensible handling of CJK, Arabic, and other non-Latin scripts.

Baseline **Java 17 (LTS)**; runs on all mainstream releases (17, 21, 25). JDK standard library only.

## Install

Maven:

```xml
<dependency>
  <groupId>io.github.balck3py</groupId>
  <artifactId>slugify-multilang</artifactId>
  <version>1.0.2</version>
</dependency>
```

Gradle:

```groovy
implementation 'io.github.balck3py:slugify-multilang:1.0.2'
```

## Usage

```java
import com.slugify.multilang.SlugifyHelper;
import com.slugify.multilang.SlugifySlugOptions;

SlugifyHelper.slugify("Director Fu: Your alt account got exposed again!");
// -> "director-fu-your-alt-account-got-exposed-again"

SlugifyHelper.slugify("Café au lait & cròissant");
// -> "cafe-au-lait-and-croissant"

SlugifyHelper.slugify("傅总：你的马甲 又又又掉了！");
// -> "傅总-你的马甲-又又又掉了"
```

### Replacement-only overload

Mirrors the C# `string Slugify(this string, string replacement = "-")` overload:

```java
SlugifyHelper.slugify("hello world", "_");   // -> "hello_world"
```

### Options

`SlugifySlugOptions` maps 1:1 to the C# `SlugifySlugOptions`, with fluent setters:

| Field         | Type      | Default | Meaning                                                    |
|---------------|-----------|---------|------------------------------------------------------------|
| `replacement` | `String`  | `"-"`   | Word-joining character.                                    |
| `remove`      | `Pattern` | `null`  | Per-character strip regex (default built-in when `null`).  |
| `lower`       | `boolean` | `true`  | Lowercase the result.                                      |
| `strict`      | `boolean` | `true`  | Strip anything that isn't a letter, number, or whitespace. |
| `trim`        | `boolean` | `true`  | Trim surrounding whitespace.                               |
| `locale`      | `String`  | `null`  | Per-language override map.                                 |

```java
SlugifyHelper.slugify("Müdür Fu", new SlugifySlugOptions().locale("de"));    // -> "mueduer-fu"
SlugifyHelper.slugify("Hello World", new SlugifySlugOptions().lower(false)); // -> "Hello-World"
```

Locale overrides exist for: `bg`, `de`, `es`, `fr`, `pt`, `uk`, `vi`, `da`, `nb`, `it`, `nl`, `sv`.

### Extending the character map

Mirrors the C# `Extend` method — registers custom mappings globally at runtime:

```java
import java.util.Map;

SlugifyHelper.extend(Map.of('☂', "umbrella", '♛', "queen"));
SlugifyHelper.slugify("☂♛");   // -> "umbrella-queen"
```

## How it works

Same five-step pipeline as the C# original:

1. **NFC normalize** (`Normalizer.normalize(input, Form.NFC)`).
2. **Per-character translation** — locale override → global charmap → passthrough.
3. **Remove pass** — strip non-URL-friendly characters.
4. **Strict pass** (optional) — keep only Unicode letters, numbers, whitespace.
5. **Collapse + lowercase** — trim, collapse whitespace runs into `replacement`, lowercase.

The default remove regex is built with `Pattern.UNICODE_CHARACTER_CLASS` and the
explicit class `[^\p{L}\p{Mn}\p{Nd}\p{Pc}\s…]`, exactly matching .NET's Unicode
`\w` semantics so non-Latin scripts are preserved.

## Build, test, demo

```bash
mvn test                                              # run the JUnit 5 suite
mvn -q compile exec:java -Dexec.mainClass=com.slugify.multilang.Demo   # 23-language demo
```

No Maven installed? The suite also runs with the JUnit Platform Console launcher:

```bash
javac --release 17 -encoding UTF-8 -d target/classes src/main/java/com/slugify/multilang/*.java
javac --release 17 -encoding UTF-8 -cp "target/classes:junit-console.jar" -d target/test-classes src/test/java/com/slugify/multilang/*.java
java -jar junit-console.jar execute -cp "target/classes:target/test-classes" --scan-classpath
```

## Fidelity

This port is verified against the C# implementation:

- All **583** global charmap entries and **12** locale override maps are
  generated 1:1 from the verified reference maps (byte-for-byte identical).
- The 23-language demo produces output identical to the C# demo.
- JUnit suite: **46 tests** (23-language parity + options / extend / Unicode /
  error-path coverage), all green.

## Publishing to Maven Central

Automated via [`publish.sh`](./publish.sh) (bash) or [`publish.ps1`](./publish.ps1)
(PowerShell), mirroring the NuGet workflow in [`../publish.ps1`](../publish.ps1).
Requires `mvn` and `gpg` (on macOS: `brew install maven gnupg`).

```bash
./publish.sh            # reads version from pom.xml → deploys → bumps patch
```

Credentials come from `~/.m2/settings.xml` (a `<server>` with id `central`,
holding your Central Portal token) and your local GPG signing key — nothing
secret is passed on the command line.

### Version-number rule

Same convention as the NuGet/PyPI packages: **semver `MAJOR.MINOR.PATCH`**, with
the `<version>` in `pom.xml` being the one published *now*. After a successful
deploy the **patch** component is bumped automatically (via `mvn versions:set`).
Bump MINOR/MAJOR by hand for feature/breaking releases. The Java, Python, and C#
packages are versioned independently.

### Git repository URL rule

The POM `<url>` / `<scm>` point at the same repository as the C# `.csproj`
(`RepositoryUrl` / `PackageProjectUrl`):

```
https://github.com/balck3py/slugify-multilag
```
