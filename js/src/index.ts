import charMapData from "./charmap.json";
import localeData from "./locales.json";

/** Options equivalent to C# `SlugifySlugOptions`. */
export interface SlugifySlugOptions {
  Replacement?: string;
  Remove?: RegExp | null;
  Lower?: boolean;
  Strict?: boolean;
  Trim?: boolean;
  Locale?: string | null;
}

const charMap = new Map<string, string>(Object.entries(charMapData));
const locales = new Map<string, Map<string, string>>(
  Object.entries(localeData).map(([locale, map]) => [locale, new Map(Object.entries(map))]),
);

// Equivalent to C# `[^\\w\\s$*_+~.()'"!\\-:@]+`; JavaScript spells .NET's
// Unicode-aware `\\w` explicitly so non-Latin letters survive this stage.
const defaultRemoveRegex = /[^\p{L}\p{Mn}\p{Nd}\p{Pc}\s$*_+~.()'"!\-:@]+/gu;
const strictRegex = /[^\p{L}\p{N}\s]/gu;
const spacesRegex = /\s+/g;

function replaceAll(value: string, expression: RegExp): string {
  const flags = expression.flags.includes("g") ? expression.flags : `${expression.flags}g`;
  return value.replace(new RegExp(expression.source, flags), "");
}

/** Convert a string into a URL-safe slug, matching C# `SlugifyHelper.Slugify`. */
export function slugify(
  input: string,
  optionsOrReplacement: SlugifySlugOptions | string = "-",
): string {
  if (input === null || input === undefined) {
    throw new Error("slugify: string argument expected");
  }

  const options: SlugifySlugOptions = typeof optionsOrReplacement === "string"
    ? { Replacement: optionsOrReplacement }
    : optionsOrReplacement ?? {};
  const locale = options.Locale != null ? locales.get(options.Locale) : undefined;
  const replacement = options.Replacement ?? "-";
  const trim = options.Trim ?? true;
  const lower = options.Lower ?? true;
  const strict = options.Strict ?? true;
  const removeRegex = options.Remove ?? defaultRemoveRegex;

  let translated = "";
  for (const character of input.normalize("NFC")) {
    let value = locale?.get(character) ?? charMap.get(character) ?? character;
    if (value === replacement) value = " ";
    translated += replaceAll(value, removeRegex);
  }

  let slug = translated;
  if (strict) slug = replaceAll(slug, strictRegex);
  if (trim) slug = slug.trim();
  slug = slug.replace(spacesRegex, replacement);
  if (lower) slug = slug.toLowerCase();
  return slug;
}

/** Add or replace global character mappings, equivalent to C# `Extend`. */
export function extend(customMap: Record<string, string> | Map<string, string>): void {
  for (const [key, value] of customMap instanceof Map ? customMap : Object.entries(customMap)) {
    charMap.set(key, value);
  }
}

/** Static-class-style C# compatibility facade. */
export const SlugifyHelper = { Slugify: slugify, Extend: extend };
