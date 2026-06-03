"""Port of C# ``SlugifyHelper.cs``.

Faithful 1:1 translation of the slug-generation pipeline. Behavioural notes
where the C# / .NET runtime and Python differ are called out inline.
"""

from __future__ import annotations

import re
import unicodedata

from .char_map import build_char_map
from .locales import build_locales
from .options import SlugifySlugOptions

# Module-level mutable state, mirroring the C# ``static readonly`` fields.
# ``_CHAR_MAP`` is intentionally mutable so that ``extend()`` can register
# custom mappings at runtime, exactly like the C# ``Extend`` method.
_CHAR_MAP: dict[str, str] = build_char_map()
_LOCALES: dict[str, dict[str, str]] = build_locales()

# C#:  new Regex(@"[^\w\s$*_+~.()'""!\-:@]+", RegexOptions.Compiled)
# In .NET (non-ECMAScript) `\w` == [\p{L}\p{Mn}\p{Nd}\p{Pc}]; Python's `re` `\w`
# omits combining marks (\p{Mn}). With the default Strict=True those marks are
# stripped by _STRICT_REGEX anyway, so output is identical for the defaults.
_DEFAULT_REMOVE_REGEX = re.compile(r"""[^\w\s$*_+~.()'"!\-:@]+""")

# C#:  new Regex(@"[^\p{L}\p{N}\s]")
# Python `re` has no \p{...}; `[^\w\s]` is the closest built-in equivalent, but
# `\w` also matches the connector "_" (which \p{L}\p{N} does not), so we strip
# the underscore explicitly via the trailing `|_`.
_STRICT_REGEX = re.compile(r"[^\w\s]|_")

# C#:  new Regex(@"\s+")
_SPACES_REGEX = re.compile(r"\s+")


def slugify(
    text: str,
    options: SlugifySlugOptions | str | None = None,
) -> str:
    """Convert ``text`` into a URL-safe slug.

    Faithful port of the two C# overloads:

    * ``Slugify(string input, SlugifySlugOptions? options = null)`` -> pass a
      :class:`SlugifySlugOptions` (or ``None``).
    * ``Slugify(this string input, string replacement = "-")`` -> pass a
      ``str`` as ``options`` to only override the replacement character.
    """
    if text is None:  # type: ignore[redundant-expr]
        # C# throws ArgumentException("slugify: string argument expected").
        raise ValueError("slugify: string argument expected")

    if isinstance(options, str):
        options = SlugifySlugOptions(replacement=options)
    elif options is None:
        options = SlugifySlugOptions()

    locale = (
        _LOCALES.get(options.locale)
        if options.locale is not None
        else None
    )
    replacement = options.replacement
    trim = options.trim

    normalized = unicodedata.normalize("NFC", text)
    remove_regex = options.remove if options.remove is not None else _DEFAULT_REMOVE_REGEX

    # C# iterates UTF-16 with manual surrogate-pair handling and only consults
    # the maps for BMP (single-code-unit) characters. Python strings iterate by
    # code point, and every map key is in the BMP, so an astral code point never
    # matches a key -- functionally identical to the C# `c.Length == 1` guard.
    parts: list[str] = []
    for ch in normalized:
        append_str: str | None = None

        # 1. locale override map takes precedence
        if locale is not None:
            loc_val = locale.get(ch)
            if loc_val is not None:
                append_str = loc_val
        # 2. else global char map
        if append_str is None:
            map_val = _CHAR_MAP.get(ch)
            if map_val is not None:
                append_str = map_val
        # 3. else passthrough
        if append_str is None:
            append_str = ch

        if append_str == replacement:
            append_str = " "

        append_str = remove_regex.sub("", append_str)
        parts.append(append_str)

    slug = "".join(parts)

    if options.strict:
        slug = _STRICT_REGEX.sub("", slug)

    if trim:
        slug = slug.strip()

    # Use a function replacement so the replacement string is treated literally
    # (C# Regex.Replace does no backreference expansion on a literal string).
    slug = _SPACES_REGEX.sub(lambda _m: replacement, slug)

    if options.lower:
        # C# ToLowerInvariant(); Python str.lower() is likewise locale-independent.
        slug = slug.lower()

    return slug


def extend(custom_map: dict[str, str]) -> None:
    """Register custom character mappings at runtime.

    Faithful port of C# ``SlugifyHelper.Extend``: mutates the shared global
    char map, so the change affects every subsequent :func:`slugify` call.
    """
    for key, value in custom_map.items():
        _CHAR_MAP[key] = value
