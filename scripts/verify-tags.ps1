# 모든 chNN-end 태그가 빌드되는지 확인한다. (저장소 루트에서 실행)
$ErrorActionPreference = "Continue"
$root = git rev-parse --show-toplevel
$work = Join-Path $env:TEMP ("minibank-verify-" + [guid]::NewGuid())
$tags = git -C $root tag -l "ch*-end" | Sort-Object
$last = $tags[-1]
$fail = @()

foreach ($tag in $tags) {
    $dir = Join-Path $work $tag
    git -C $root worktree add --detach -q $dir $tag
    Write-Host "=== $tag ==="
    Push-Location (Join-Path $dir "mini-bank")
    if ($tag -eq $last) { .\gradlew.bat -q build } else { .\gradlew.bat -q compileJava compileTestJava }
    if ($LASTEXITCODE -ne 0) { $fail += "${tag}:backend" }
    Pop-Location
    $web = Join-Path $dir "minibank-web"
    if (Test-Path $web) {
        Push-Location $web
        npm ci --silent; if ($LASTEXITCODE -eq 0) { npm run build --silent }
        if ($LASTEXITCODE -ne 0) { $fail += "${tag}:web" }
        Pop-Location
    }
    git -C $root worktree remove --force $dir
}

if ($fail.Count -eq 0) { Write-Host "모든 태그 빌드 성공" } else { Write-Host "실패: $($fail -join ', ')"; exit 1 }
