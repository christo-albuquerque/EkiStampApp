# Uploads the local release keystore + passwords as GitHub Actions secrets for the release workflow.
# Run from the repo root:  . .\env.ps1; .\scripts\set-release-secrets.ps1
$repo = (gh repo view --json nameWithOwner --jq .nameWithOwner)
$p = Get-Content "$PSScriptRoot\..\keystore.properties" | ConvertFrom-StringData
[Convert]::ToBase64String([IO.File]::ReadAllBytes("$PSScriptRoot\..\release.keystore")) | gh secret set ANDROID_KEYSTORE_BASE64 --repo $repo
$p.storePassword | gh secret set ANDROID_KEYSTORE_PASSWORD --repo $repo
$p.keyAlias      | gh secret set ANDROID_KEY_ALIAS --repo $repo
$p.keyPassword   | gh secret set ANDROID_KEY_PASSWORD --repo $repo
gh secret list --repo $repo
