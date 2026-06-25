$ErrorActionPreference = "Stop"

$RootDir = Resolve-Path (Join-Path $PSScriptRoot "..")
$LogDir = Join-Path $RootDir ".local-logs"
$PidDir = Join-Path $RootDir ".local-pids"

New-Item -ItemType Directory -Force -Path $LogDir | Out-Null
New-Item -ItemType Directory -Force -Path $PidDir | Out-Null

if (-not $env:CONFIG_SERVER_URL) { $env:CONFIG_SERVER_URL = "http://localhost:8888" }
if (-not $env:CONFIG_SERVER_SEARCH_LOCATIONS) { $env:CONFIG_SERVER_SEARCH_LOCATIONS = "file:$RootDir/config-microservicios" }
if (-not $env:EUREKA_SERVER_URL) { $env:EUREKA_SERVER_URL = "http://localhost:8761/eureka/" }
if (-not $env:EUREKA_CLIENT_SERVICEURL_DEFAULTZONE) { $env:EUREKA_CLIENT_SERVICEURL_DEFAULTZONE = $env:EUREKA_SERVER_URL }
if (-not $env:DB_HOST) { $env:DB_HOST = "localhost" }
if (-not $env:DB_PORT) { $env:DB_PORT = "3306" }
if (-not $env:DB_USER) { $env:DB_USER = "root" }
if (-not $env:DB_PASSWORD) { $env:DB_PASSWORD = "" }

$Services = @(
  "eureka",
  "config-server",
  "videojuegos",
  "usuarios",
  "authentication",
  "carrito",
  "pagos",
  "pedidos",
  "resenas",
  "inventario",
  "api-gateway"
)

foreach ($Service in $Services) {
  $PidFile = Join-Path $PidDir "$Service.pid"
  $LogFile = Join-Path $LogDir "$Service.log"

  if (Test-Path $PidFile) {
    $ExistingPid = Get-Content $PidFile -ErrorAction SilentlyContinue
    if ($ExistingPid -and (Get-Process -Id $ExistingPid -ErrorAction SilentlyContinue)) {
      Write-Host "$Service ya esta corriendo con PID $ExistingPid"
      continue
    }
  }

  Write-Host "Levantando $Service..."
  $WorkDir = Join-Path $RootDir $Service
  $Process = Start-Process -FilePath "cmd.exe" -ArgumentList "/c", ".\mvnw.cmd spring-boot:run > `"$LogFile`" 2>&1" -WorkingDirectory $WorkDir -PassThru -WindowStyle Minimized
  Set-Content -Path $PidFile -Value $Process.Id

  if ($Service -eq "eureka" -or $Service -eq "config-server") {
    Start-Sleep -Seconds 10
  } elseif ($Service -eq "api-gateway") {
    Start-Sleep -Seconds 3
  } else {
    Start-Sleep -Seconds 2
  }
}

Write-Host ""
Write-Host "Servicios levantandose. Revisa logs en $LogDir"
Write-Host "API Gateway: http://localhost:8080"
Write-Host "Swagger:     http://localhost:8080/swagger-ui/index.html"
Write-Host "Eureka:      http://localhost:8761"
