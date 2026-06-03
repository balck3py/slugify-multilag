"""slugify_multilang -- a faithful Python port of the C# Slugify.MultiLang library.

Multi-language slug generator supporting 23+ languages including CJK, Arabic,
Cyrillic, and more. Zero third-party dependencies (standard library only).

Basic usage::

    from slugify_multilang import slugify, SlugifySlugOptions

    slugify("Director Fu: Your alt account got exposed again!")
    # -> "director-fu-your-alt-account-got-exposed-again"

    slugify("Müdür Fu", SlugifySlugOptions(locale="de"))

    # Replacement-only overload (mirrors the C# string overload):
    slugify("hello world", "_")          # -> "hello_world"

    # Register custom mappings at runtime:
    from slugify_multilang import extend
    extend({"☂": "umbrella"})
"""

from __future__ import annotations

from .helper import extend, slugify
from .options import SlugifySlugOptions

__all__ = ["slugify", "extend", "SlugifySlugOptions"]
__version__ = "1.0.3"
