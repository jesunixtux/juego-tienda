$ErrorActionPreference = "Stop"

$RootDir = Resolve-Path (Join-Path $PSScriptRoot "..")
$PidDir = Join-Path $RootDir ".local-pids"

if (-not (Test-Path $PidDir)) {
  Write-Host "No hay procesos locales registrados."
  exit 0
}

$Services = @(
  "api-gateway",
  "inventario",
  "resenas",
  "pedidos",
  "pagos",
  "carrito",
  "authentication",
  "usuarios",
  "videojuegos",
  "config-server",
  "eureka"
)

foreach ($Service in $Services) {
  $PidFile = Join-Path $PidDir "$Service.pid"
  if (Test-Path $PidFile) {
    $PidValue = Get-Content $PidFile -ErrorAction SilentlyContinue
    if ($PidValue) {
      $Process = Get-Process -Id $PidValue -ErrorAction SilentlyContinue
      if ($Process) {
        Write-Host "Deteniendo $Service PID $PidValue..."
        Stop-Process -Id $PidValue -Force
      } else {
        Write-Host "$Service no estaba corriendo."
      }
    }
    Remove-Item $PidFile -Force
  }
}

Write-Host "Servicios locales detenidos."
