param(
    [string]$MavenRepositoryDir = "",
    [string]$MavenRepositorySource = ""
)

$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = Split-Path -Parent $ScriptDir

if ([string]::IsNullOrWhiteSpace($MavenRepositoryDir)) {
    $MavenRepositoryDir = Join-Path $ProjectRoot "docker\maven-repository"
}

if ([string]::IsNullOrWhiteSpace($MavenRepositorySource)) {
    $MavenRepositorySource = Join-Path $HOME ".m2\repository"
}

$Modules = @(
    "eureka",
    "config-server",
    "api-gateway",
    "videojuegos",
    "usuarios",
    "authentication",
    "carrito",
    "pagos",
    "pedidos",
    "resenas",
    "inventario"
)

New-Item -ItemType Directory -Force -Path $MavenRepositoryDir | Out-Null
New-Item -ItemType File -Force -Path (Join-Path $MavenRepositoryDir ".gitkeep") | Out-Null

Write-Host "Preparando repositorio Maven local para Docker"
Write-Host "Destino: $MavenRepositoryDir"
Write-Host ""

if (Test-Path $MavenRepositorySource) {
    Write-Host "Copiando cache Maven local desde:"
    Write-Host $MavenRepositorySource

    if ($IsWindows -or $env:OS -eq "Windows_NT") {
        robocopy $MavenRepositorySource $MavenRepositoryDir /E /XF *.lastUpdated /NFL /NDL /NJH /NJS /NC /NS | Out-Null
        if ($LASTEXITCODE -gt 7) {
            throw "robocopy fallo con codigo $LASTEXITCODE"
        }
        $global:LASTEXITCODE = 0
    } else {
        Copy-Item -Path (Join-Path $MavenRepositorySource "*") -Destination $MavenRepositoryDir -Recurse -Force
    }

    Write-Host ""
}

foreach ($Module in $Modules) {
    $ModuleDir = Join-Path $ProjectRoot $Module
    $Pom = Join-Path $ModuleDir "pom.xml"

    if (!(Test-Path $Pom)) {
        Write-Host "WARN $Module: no existe pom.xml, se omite"
        continue
    }

    Write-Host "==> Descargando dependencias de $Module"
    Push-Location $ModuleDir
    try {
        if (Test-Path ".\mvnw.cmd") {
            & .\mvnw.cmd "-o" "-Dmaven.repo.local=$MavenRepositoryDir" "-DskipTests" "package"
            if ($LASTEXITCODE -ne 0) {
                & .\mvnw.cmd "-Dmaven.repo.local=$MavenRepositoryDir" "-DskipTests" "package"
            }
        } else {
            & ./mvnw "-o" "-Dmaven.repo.local=$MavenRepositoryDir" "-DskipTests" "package"
            if ($LASTEXITCODE -ne 0) {
                & ./mvnw "-Dmaven.repo.local=$MavenRepositoryDir" "-DskipTests" "package"
            }
        }

        if ($LASTEXITCODE -ne 0) {
            exit $LASTEXITCODE
        }
    } finally {
        Pop-Location
    }
}

Get-ChildItem -Path $MavenRepositoryDir -Recurse -Include "*.lastUpdated" -Force |
    Remove-Item -Force

Write-Host ""
Write-Host "Dependencias listas en:"
Write-Host $MavenRepositoryDir
