#!/usr/bin/env pwsh
# Usage: ./publish.ps1 [-ApiKey <key>]
# API key can also be set via env var: $env:NUGET_API_KEY
param(
    [string]$ApiKey = $env:NUGET_API_KEY
)

$csproj = "$PSScriptRoot/csharp/src/Slugify.MultiLang/Slugify.MultiLang.csproj"

if (-not $ApiKey) {
    Write-Error "NuGet API key required. Pass -ApiKey or set NUGET_API_KEY env var."
    exit 1
}

# Read current version (this is the version to publish now)
$xml = [xml](Get-Content $csproj)
$current = $xml.Project.PropertyGroup.Version
if (-not $current) { $current = "1.0.0" }
Write-Host "Publishing version: $current"

# Pack with current version
dotnet pack $csproj -c Release --no-restore -o "$PSScriptRoot/nupkg"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

# Push
$nupkg = Get-ChildItem "$PSScriptRoot/nupkg/Slugify.MultiLang.$current.nupkg" | Select-Object -First 1
dotnet nuget push $nupkg.FullName --api-key $ApiKey --source https://api.nuget.org/v3/index.json
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

# Increment patch version for next publish
$parts = $current.Split('.')
$parts[2] = [string]([int]$parts[2] + 1)
$next = $parts -join '.'
$xml.Project.PropertyGroup.Version = $next
$xml.Save($csproj)
Write-Host "Next version set to: $next"
