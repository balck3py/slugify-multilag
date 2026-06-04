#!/usr/bin/env bash
# Automated Maven Central packaging + publish for slugify-multilang (Java).
#
# Bash port of the NuGet workflow in ../publish.ps1:
#   1. read the current version from pom.xml  (this is what gets published now)
#   2. build + test + sign, then deploy to Maven Central via the `release` profile
#   3. auto-increment the patch version for the next publish
#
# Credentials: Maven reads the Central Portal token from ~/.m2/settings.xml
# (a <server> with id "central"), and GPG uses your local signing key. Nothing
# secret is passed on the command line.
#
# Usage:   ./publish.sh
# Requires: mvn (https://maven.apache.org/) and gpg. Install on macOS: brew install maven gnupg
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
POM="$SCRIPT_DIR/pom.xml"

# --- Read current version (the version to publish now) -------------------------
CURRENT="$(mvn -q -f "$POM" help:evaluate -Dexpression=project.version -DforceStdout)"
echo "Publishing version: $CURRENT"

# --- Build, test, sign, and deploy to Maven Central ----------------------------
mvn -f "$POM" -P release clean deploy

# --- Increment patch version for next publish (semver: bump 3rd component) -----
IFS='.' read -r MAJOR MINOR PATCH <<< "$CURRENT"
NEXT="$MAJOR.$MINOR.$((PATCH + 1))"
mvn -q -f "$POM" versions:set -DnewVersion="$NEXT" -DgenerateBackupPoms=false
echo "Next version set to: $NEXT"
