#!/usr/bin/env pwsh
# Automated Maven Central packaging + publish for slugify-multilang (Java).
#
# Mirrors the NuGet workflow in ../publish.ps1:
#   1. read the current version from pom.xml  (this is what gets published now)
#   2. build + test + sign, then deploy to Maven Central via the `release` profile
#   3. auto-increment the patch version for the next publish
#
# Credentials: Maven reads the Central Portal token from ~/.m2/settings.xml
# (a <server> with id "central"), and GPG uses your local signing key. Nothing
# secret is passed on the command line.
#
# Usage:   ./publish.ps1
# Requires: mvn + gpg. Install pwsh on macOS with: brew install powershell
param()

$ErrorActionPreference = "Stop"
$pom = Join-Path $PSScriptRoot "pom.xml"

# --- Read current version (the version to publish now) -------------------------
$current = (mvn -q -f $pom help:evaluate -Dexpression=project.version -DforceStdout).Trim()
Write-Host "Publishing version: $current"

# --- Build, test, sign, and deploy to Maven Central ----------------------------
mvn -f $pom -P release clean deploy
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

# --- Increment patch version for next publish (semver: bump 3rd component) ------
$parts = $current.Split('.')
$parts[2] = [string]([int]$parts[2] + 1)
$next = $parts -join '.'
mvn -q -f $pom versions:set -DnewVersion=$next -DgenerateBackupPoms=false
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
Write-Host "Next version set to: $next"
