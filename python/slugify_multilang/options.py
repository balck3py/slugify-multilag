"""Port of C# ``SlugifySlugOptions.cs``.

Faithful 1:1 translation. Every field name maps to the original C# property
(PascalCase -> snake_case) and every default value is identical.
"""

from __future__ import annotations

import re
from dataclasses import dataclass


@dataclass
class SlugifySlugOptions:
    """Options controlling slug generation.

    Mirrors the C# ``SlugifySlugOptions`` class:

    ============================  =========================  =========
    C#                            Python                     Default
    ============================  =========================  =========
    ``string Replacement``        ``replacement: str``       ``"-"``
    ``Regex? Remove``             ``remove: re.Pattern|None``  ``None``
    ``bool Lower``                ``lower: bool``            ``True``
    ``bool Strict``               ``strict: bool``           ``True``
    ``bool Trim``                 ``trim: bool``             ``True``
    ``string? Locale``            ``locale: str|None``       ``None``
    ============================  =========================  =========
    """

    replacement: str = "-"
    remove: re.Pattern[str] | None = None
    lower: bool = True
    strict: bool = True
    trim: bool = True
    locale: str | None = None
