# Project Context and Release Notes

## Current Repository State

- The C#, Python, JavaScript/TypeScript, and Java implementations intentionally
  share the same slug pipeline, character map (583 entries), and 12 locale maps.
- JavaScript source is in `js/`; its npm package name is `slugify-multilang`.
- Java source is in `java/`; its intended Maven coordinates are
  `io.github.balck3py:slugify-multilang:1.0.0`. Temurin JDK 21 is installed
  under `C:\Users\yuanju\.jdks\temurin-21.0.11\jdk-21.0.11+10`, and the Java
  sources compile with `javac`. Maven 3.9.16 is installed under
  `C:\Users\yuanju\.tools\apache-maven-3.9.16\apache-maven-3.9.16` and
  `mvn verify` creates the main, sources, and Javadoc JARs when JVM memory is
  limited with `MAVEN_OPTS`.
- The `release` profile has been exercised through Maven's GPG signing step.
  It currently stops at `gpg: no default secret key`; create or import the
  release owner's protected GPG key before invoking `mvn -Prelease deploy`.
- `master` contains the JavaScript package and documentation in commits
  `2310a49` and `6897eb2`.
- The locally published npm version is `1.0.3`.

## JavaScript Package Rules

- Preserve C# behavior and public option names exactly: `Replacement`,
  `Remove`, `Lower`, `Strict`, `Trim`, and `Locale`. Do not add camelCase
  aliases or change character/locale mappings.
- Build from `js/` with `npm run build`. The package exports ESM, CommonJS, and
  TypeScript declarations.
- `js/test.html` is a manual browser playground. Serve `js/` through HTTP
  (for example, `python -m http.server 4173`) after building; do not open it
  directly from `file://`.

## Release Checklist and Pitfalls

1. Bump npm package version with `npm version <version> --no-git-tag-version`
   to avoid creating a Git tag automatically.
2. Run `npm run build`, CommonJS/ESM smoke checks, and `npm pack --dry-run`.
3. Publish with `npm publish`. This account requires 2FA, so publishing may
   stop with `EOTP`; use `npm publish --otp=<current-code>` locally.
4. npm displays `js/README.md`, not the repository-root README. Root README
   is multilingual and cross-platform; npm documentation must remain specific
   to JavaScript/TypeScript.
5. npm versions are immutable. README or package metadata fixes require a new
   version and republish.

## Important Follow-up

`1.0.3` was published before the final README expansion and before the local
package metadata changed to `"type": "module"` (which makes tsup emit ESM as
`dist/index.js` and CommonJS as `dist/index.cjs`). The repository now has the
newer README and packaging layout, but npm `1.0.3` does not. Release `1.0.4`
after reviewing the package if npm should match the repository exactly.
