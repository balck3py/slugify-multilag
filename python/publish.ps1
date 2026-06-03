#!/usr/bin/env pwsh
# Automated PyPI packaging + publish for slugify-multilang (Python).
#
# Mirrors the NuGet workflow in ../publish.ps1:
#   1. read the current version from pyproject.toml  (this is what gets published now)
#   2. build the sdist + wheel with `uv build`
#   3. upload to pypi.org with `twine` (run via `uvx`, no separate install)
#   4. auto-increment the patch version for the next publish
#
# Credentials: twine reads them from ~/.pypirc (the [pypi] section) automatically,
# or from the TWINE_USERNAME / TWINE_PASSWORD env vars. No token is passed here.
#
# Usage:   ./publish.ps1 [-Repository <name>]
#   -Repository  optional ~/.pypirc section to use (e.g. "testpypi"); defaults to PyPI.
#
# Requires: uv (https://docs.astral.sh/uv/). Install pwsh on macOS with: brew install powershell
param(
    [string]$Repository
)

$ErrorActionPreference = "Stop"
$pyproject = Join-Path $PSScriptRoot "pyproject.toml"
$initFile  = Join-Path $PSScriptRoot "slugify_multilang/__init__.py"

# --- Read current version (the version to publish now) -------------------------
$content = Get-Content $pyproject -Raw
$match = [regex]::Match($content, '(?m)^version\s*=\s*"(\d+)\.(\d+)\.(\d+)"')
if (-not $match.Success) {
    Write-Error "Could not find a 'version = \"X.Y.Z\"' line in pyproject.toml."
    exit 1
}
$current = "$($match.Groups[1].Value).$($match.Groups[2].Value).$($match.Groups[3].Value)"
Write-Host "Publishing version: $current"

# --- Keep __init__.__version__ in sync with pyproject (single source of truth) -
$initContent = Get-Content $initFile -Raw
$initVersion = [regex]::Match($initContent, '__version__\s*=\s*"([^"]+)"').Groups[1].Value
if ($initVersion -ne $current) {
    Write-Error "Version mismatch: pyproject=$current but __init__.py=$initVersion. Fix before publishing."
    exit 1
}

# --- Sanity gate: tests must pass before anything is published -----------------
uv run --project $PSScriptRoot --extra test pytest -q
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

# --- Build sdist + wheel (clean dist/ first) -----------------------------------
$dist = Join-Path $PSScriptRoot "dist"
if (Test-Path $dist) { Remove-Item $dist -Recurse -Force }
uv build --project $PSScriptRoot
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

# --- Validate then upload with twine (reads ~/.pypirc) -------------------------
uvx twine check "$dist/*"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

if ($Repository) {
    uvx twine upload --repository $Repository "$dist/*"
} else {
    uvx twine upload "$dist/*"
}
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

# --- Increment patch version for next publish (semver: bump 3rd component) -----
$parts = $current.Split('.')
$parts[2] = [string]([int]$parts[2] + 1)
$next = $parts -join '.'

# Replace only the project version line in pyproject.toml, and __version__ in __init__.py.
$content = [regex]::Replace($content, '(?m)^version\s*=\s*"\d+\.\d+\.\d+"', "version = `"$next`"", 1)
Set-Content -Path $pyproject -Value $content -NoNewline

$initContent = [regex]::Replace($initContent, '__version__\s*=\s*"[^"]+"', "__version__ = `"$next`"", 1)
Set-Content -Path $initFile -Value $initContent -NoNewline

Write-Host "Next version set to: $next"
