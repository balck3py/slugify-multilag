"""Behavioural tests for options, extensibility, and edge cases.

These mirror the semantics of the C# SlugifyHelper / SlugifySlugOptions.
"""

from __future__ import annotations

import re
import unicodedata

import pytest

import slugify_multilang
from slugify_multilang import SlugifySlugOptions, extend, slugify


class TestBasic:
    def test_simple_ascii(self) -> None:
        assert slugify("Hello World") == "hello-world"

    def test_ampersand_to_and(self) -> None:
        assert slugify("Café au lait & cròissant") == "cafe-au-lait-and-croissant"

    def test_cjk_preserved(self) -> None:
        assert slugify("傅总：你的马甲 又又又掉了！") == "傅总-你的马甲-又又又掉了"


class TestReplacementOverload:
    def test_string_overload(self) -> None:
        # Mirrors C#: Slugify(this string, string replacement = "-")
        assert slugify("hello world", "_") == "hello_world"

    def test_replacement_option(self) -> None:
        assert slugify("hello world", SlugifySlugOptions(replacement=".")) == "hello.world"

    def test_default_replacement_is_dash(self) -> None:
        assert slugify("a b c") == "a-b-c"


class TestOptions:
    def test_lower_false_keeps_case(self) -> None:
        assert slugify("Hello World", SlugifySlugOptions(lower=False)) == "Hello-World"

    def test_lower_true_is_default(self) -> None:
        assert slugify("HELLO") == "hello"

    def test_strict_false_keeps_extra_symbols(self) -> None:
        # Underscore survives the remove pass; without strict it is kept.
        assert slugify("a_b", SlugifySlugOptions(strict=False)) == "a_b"

    def test_strict_true_strips_underscore(self) -> None:
        # C# strict regex [^\p{L}\p{N}\s] removes underscore.
        assert slugify("a_b", SlugifySlugOptions(strict=True)) == "ab"

    def test_trim_default_trims_edges(self) -> None:
        assert slugify("  hello world  ") == "hello-world"

    def test_custom_remove_regex(self) -> None:
        # Strip vowels at the per-character remove stage.
        opts = SlugifySlugOptions(remove=re.compile(r"[aeiou]"))
        assert slugify("hello world", opts) == "hll-wrld"

    def test_locale_de(self) -> None:
        assert slugify("Müdür", SlugifySlugOptions(locale="de")) == "mueduer"

    def test_unknown_locale_falls_back(self) -> None:
        # Unknown locale -> global charmap only (ü -> u).
        assert slugify("Müdür", SlugifySlugOptions(locale="xx")) == "mudur"


class TestSymbolsAndCurrencies:
    def test_dollar(self) -> None:
        assert slugify("$100") == "dollar100"

    def test_euro(self) -> None:
        assert slugify("€50") == "euro50"

    def test_percent(self) -> None:
        assert slugify("50%") == "50percent"


class TestUnicodeMechanics:
    def test_nfc_normalization(self) -> None:
        # Decomposed "é" (e + U+0301) must behave like precomposed "é".
        decomposed = "café"
        assert unicodedata.is_normalized("NFC", decomposed) is False
        assert slugify(decomposed) == "cafe"

    def test_astral_codepoint_passthrough_then_stripped(self) -> None:
        # Emoji (astral, no charmap entry) -> stripped by strict pass.
        assert slugify("hi 😀 there") == "hi-there"

    def test_empty_string(self) -> None:
        assert slugify("") == ""


class TestExtend:
    def test_extend_registers_mapping(self) -> None:
        extend({"☂": "umbrella"})
        assert slugify("☂ rain") == "umbrella-rain"

    def test_extend_overrides_existing(self) -> None:
        # Save & restore so the global mutation does not leak to other tests.
        original = slugify("₿")
        try:
            extend({"₿": "btc"})
            assert slugify("₿") == "btc"
        finally:
            extend({"₿": "bitcoin"})
            assert slugify("₿") == original


class TestErrors:
    def test_none_raises_value_error(self) -> None:
        with pytest.raises(ValueError, match="slugify: string argument expected"):
            slugify(None)  # type: ignore[arg-type]


class TestPackage:
    def test_version_matches_package_metadata(self) -> None:
        # The literal __version__ must stay in lockstep with the packaging
        # metadata; this never goes stale across version bumps.
        from importlib.metadata import PackageNotFoundError, version

        try:
            installed = version("slugify-multilang")
        except PackageNotFoundError:
            pytest.skip("package not installed; metadata unavailable")
        assert slugify_multilang.__version__ == installed

    def test_public_api(self) -> None:
        assert set(slugify_multilang.__all__) == {"slugify", "extend", "SlugifySlugOptions"}
