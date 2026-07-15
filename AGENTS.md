# Repository Guidelines

## Project Structure & Module Organization

This repository contains equivalent multi-language slugification libraries. The
C# implementation lives in `csharp/`: the `src/Slugify.MultiLang/` library
targets .NET Standard 2.0 and `demo/Slugify.MultiLang.Demo/` is the .NET 8
console demonstration. The Python package is self-contained in `python/`, with
implementation modules in `python/slugify_multilang/`, tests in
`python/tests/`, and package metadata in `python/pyproject.toml`. `js/src/index.ts`
is a TypeScript port currently kept as source only; do not assume a Node build
or test setup exists until package metadata is added. Root `README.md` and
`README.zh-CN.md` document the public behavior.

## Build, Test, and Development Commands

- `dotnet build csharp/Slugify.MultiLang.slnx` builds the C# library and demo.
- `dotnet run --project csharp/demo/Slugify.MultiLang.Demo` runs the
  23-language C# demonstration.
- `cd python; python -m pytest` runs the Python suite. Install its optional
  test dependency first with `python -m pip install -e ".[test]"` if needed.
- `cd python; python demo.py` prints the Python demo output for parity checks.

Publishing scripts (`publish.ps1` and `python/publish.*`) change versions and
upload packages; run them only when explicitly preparing a release.

## Coding Style & Naming Conventions

Follow the local style of the language you edit. C# uses four-space indentation,
PascalCase public APIs, nullable annotations, and explicit `using` directives.
Python uses four-space indentation, `snake_case` functions and fields,
PascalCase classes, type hints, and standard-library-only runtime code. Keep
the ports behaviorally aligned: locale maps, char maps, option defaults, and
Unicode pipeline changes should be reflected deliberately across implementations.

## Testing Guidelines

Python uses `pytest`; name files `test_*.py`, tests `test_*`, and group related
cases in `Test*` classes. Cover normal ASCII input, locale overrides, Unicode
scripts, options, and extension behavior. The C# project has no committed test
project, so use the demo for a basic regression check and add a focused test
project only when the task calls for C# test coverage.

## Commit & Pull Request Guidelines

Recent history uses concise Conventional Commit-style subjects, such as
`feat: add Python port of Slugify.MultiLang with pytest suite`. Use a scoped,
imperative summary (`feat:`, `fix:`, `docs:`) and keep unrelated changes out of
one commit. Pull requests should explain behavioral changes, list validation
commands run, link relevant issues, and include before/after slug examples when
Unicode or locale behavior changes. Screenshots are unnecessary unless a UI is
introduced.
