# slugify-multilang (Python)

> A faithful, **dependency-free** Python port of the C# [`Slugify.MultiLang`](../csharp) library — a multi-language, URL-safe slug generator with extended locale support and sensible handling of CJK, Arabic, and other non-Latin scripts.

Targets **Python 3.10+**. Standard library only (`re` + `unicodedata`).

## Install

```bash
pip install slugify-multilang
```

Or from this folder:

```bash
cd python
pip install .
```

## Usage

```python
from slugify_multilang import slugify, SlugifySlugOptions, extend

slugify("Director Fu: Your alt account got exposed again!")
# -> "director-fu-your-alt-account-got-exposed-again"

slugify("Café au lait & cròissant")
# -> "cafe-au-lait-and-croissant"

slugify("傅总：你的马甲 又又又掉了！")
# -> "傅总-你的马甲-又又又掉了"
```

### Replacement-only overload

Mirrors the C# `string Slugify(this string, string replacement = "-")` overload —
pass a string instead of an options object:

```python
slugify("hello world", "_")          # -> "hello_world"
```

### Options

`SlugifySlugOptions` maps 1:1 to the C# `SlugifySlugOptions` (PascalCase → snake_case):

| Field         | Type                  | Default | Meaning                                                    |
|---------------|-----------------------|---------|------------------------------------------------------------|
| `replacement` | `str`                 | `"-"`   | Word-joining character.                                    |
| `remove`      | `re.Pattern \| None`  | `None`  | Per-character strip regex (default built-in when `None`).  |
| `lower`       | `bool`                | `True`  | Lowercase the result.                                      |
| `strict`      | `bool`                | `True`  | Strip anything that isn't a letter, number, or whitespace. |
| `trim`        | `bool`                | `True`  | Trim surrounding whitespace.                               |
| `locale`      | `str \| None`         | `None`  | Per-language override map.                                 |

```python
from slugify_multilang import slugify, SlugifySlugOptions

slugify("Müdür Fu", SlugifySlugOptions(locale="de"))   # -> "mueduer-fu"
slugify("Hello World", SlugifySlugOptions(lower=False)) # -> "Hello-World"
```

Locale overrides exist for: `bg`, `de`, `es`, `fr`, `pt`, `uk`, `vi`, `da`, `nb`, `it`, `nl`, `sv`.

### Extending the character map

Mirrors the C# `Extend` method — registers custom mappings globally at runtime:

```python
from slugify_multilang import extend, slugify

extend({"☂": "umbrella", "₿": "btc"})
slugify("☂ ₿")   # -> "umbrella-btc"
```

## How it works

Same five-step pipeline as the C# original:

1. **NFC normalize** (`unicodedata.normalize("NFC", ...)`).
2. **Per-character translation** — locale override → global charmap → passthrough.
3. **Remove pass** — strip non-URL-friendly characters.
4. **Strict pass** (optional) — keep only Unicode letters, numbers, whitespace.
5. **Collapse + lowercase** — trim, collapse whitespace runs into `replacement`, lowercase.

## Fidelity

This port is verified against the C# implementation:

- All **583** global charmap entries are byte-for-byte identical.
- All **12** locale override maps are identical.
- The 23-language demo (`python demo.py`) produces output identical to the C# demo.

### One documented nuance

.NET's regex `\w` includes Unicode combining marks (`\p{Mn}`); Python's `re` `\w`
does not. This only affects the intermediate *remove* pass, and only when
`strict=False`. With the default `strict=True`, combining marks are stripped by
the strict pass in both implementations, so output is identical.

## Demo

```bash
python demo.py
```

## Publishing to PyPI

Automated via [`publish.sh`](./publish.sh) (bash) or [`publish.ps1`](./publish.ps1)
(PowerShell), both mirroring the NuGet workflow in [`../publish.ps1`](../publish.ps1).
Requires [`uv`](https://docs.astral.sh/uv/); `publish.ps1` additionally needs `pwsh`
(on macOS: `brew install powershell`).

Credentials come from your `~/.pypirc` (`[pypi]` section) — read automatically by
`twine`, so no token is passed on the command line:

```bash
./publish.sh                 # bash — uploads to PyPI using ~/.pypirc
./publish.sh -r testpypi     # upload to TestPyPI instead (needs a [testpypi] section)

pwsh ./publish.ps1                    # PowerShell equivalent
pwsh ./publish.ps1 -Repository testpypi
```

The script: reads the current version → checks `__init__.py` is in sync → runs the
test suite → `uv build` → `uvx twine check` → `uvx twine upload` to pypi.org →
auto-increments the patch version for next time.

> `twine` also honours the `TWINE_USERNAME` / `TWINE_PASSWORD` env vars if you
> prefer not to use `~/.pypirc`. `uv publish` is *not* used because it does not
> read `~/.pypirc`.

### Version-number rule

Same convention as the NuGet package: **semver `MAJOR.MINOR.PATCH`**, with the
version stored in `pyproject.toml` being the one published *now*. After a
successful publish the **patch** component is bumped automatically (e.g.
`1.0.2 → 1.0.3`). `pyproject.toml` is the single source of truth and the script
keeps `slugify_multilang/__init__.py`'s `__version__` in lockstep (it refuses to
publish if the two disagree). Bump MINOR/MAJOR by hand for feature/breaking
releases. The Python and C# packages are versioned independently (each tracks its
own patch line).

### Git repository URL rule

The `[project.urls]` `Homepage` and `Repository` point at the same repository as
the C# `.csproj` (`RepositoryUrl` / `PackageProjectUrl`):

```
https://github.com/balck3py/slugify-multilag
```

Keep these identical to the C# project so both packages resolve to one source repo.
