#!/usr/bin/env bash
# Automated PyPI packaging + publish for slugify-multilang (Python).
#
# Bash port of ./publish.ps1, mirroring the NuGet workflow in ../publish.ps1:
#   1. read the current version from pyproject.toml  (this is what gets published now)
#   2. build the sdist + wheel with `uv build`
#   3. upload to pypi.org with `twine` (run via `uvx`, no separate install)
#   4. auto-increment the patch version for the next publish
#
# Credentials: twine reads them from ~/.pypirc (the [pypi] section) automatically,
# or from the TWINE_USERNAME / TWINE_PASSWORD env vars. No token is passed here.
#
# Usage:   ./publish.sh [-r <repository>]
#   -r  optional ~/.pypirc section to use (e.g. "testpypi"); defaults to PyPI.
#
# Requires: uv (https://docs.astral.sh/uv/)
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PYPROJECT="$SCRIPT_DIR/pyproject.toml"
INIT_FILE="$SCRIPT_DIR/slugify_multilang/__init__.py"

REPOSITORY=""
while getopts ":r:" opt; do
  case "$opt" in
    r) REPOSITORY="$OPTARG" ;;
    *) echo "Usage: $0 [-r <repository>]" >&2; exit 2 ;;
  esac
done

# --- Read current version (the version to publish now) -------------------------
CURRENT="$(python3 - "$PYPROJECT" <<'PY'
import re, sys
content = open(sys.argv[1], encoding="utf-8").read()
m = re.search(r'(?m)^version\s*=\s*"(\d+)\.(\d+)\.(\d+)"', content)
if not m:
    sys.exit("Could not find a 'version = \"X.Y.Z\"' line in pyproject.toml.")
print(f"{m.group(1)}.{m.group(2)}.{m.group(3)}")
PY
)"
echo "Publishing version: $CURRENT"

# --- Keep __init__.__version__ in sync with pyproject (single source of truth) -
INIT_VERSION="$(python3 - "$INIT_FILE" <<'PY'
import re, sys
content = open(sys.argv[1], encoding="utf-8").read()
m = re.search(r'__version__\s*=\s*"([^"]+)"', content)
print(m.group(1) if m else "")
PY
)"
if [ "$INIT_VERSION" != "$CURRENT" ]; then
  echo "Error: version mismatch: pyproject=$CURRENT but __init__.py=$INIT_VERSION. Fix before publishing." >&2
  exit 1
fi

# --- Sanity gate: tests must pass before anything is published -----------------
uv run --project "$SCRIPT_DIR" --extra test pytest -q

# --- Build sdist + wheel (clean dist/ first) -----------------------------------
rm -rf "$SCRIPT_DIR/dist"
uv build --project "$SCRIPT_DIR"

# --- Validate then upload with twine (reads ~/.pypirc) -------------------------
uvx twine check "$SCRIPT_DIR/dist/"*
if [ -n "$REPOSITORY" ]; then
  uvx twine upload --repository "$REPOSITORY" "$SCRIPT_DIR/dist/"*
else
  uvx twine upload "$SCRIPT_DIR/dist/"*
fi

# --- Increment patch version for next publish (semver: bump 3rd component) -----
NEXT="$(python3 - "$PYPROJECT" "$INIT_FILE" "$CURRENT" <<'PY'
import re, sys
pyproject, init_file, current = sys.argv[1], sys.argv[2], sys.argv[3]
major, minor, patch = current.split(".")
nxt = f"{major}.{minor}.{int(patch) + 1}"

c = open(pyproject, encoding="utf-8").read()
c = re.sub(r'(?m)^version\s*=\s*"\d+\.\d+\.\d+"', f'version = "{nxt}"', c, count=1)
open(pyproject, "w", encoding="utf-8").write(c)

i = open(init_file, encoding="utf-8").read()
i = re.sub(r'__version__\s*=\s*"[^"]+"', f'__version__ = "{nxt}"', i, count=1)
open(init_file, "w", encoding="utf-8").write(i)

print(nxt)
PY
)"
echo "Next version set to: $NEXT"
