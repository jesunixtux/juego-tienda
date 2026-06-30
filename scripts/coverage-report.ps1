$ErrorActionPreference = "Stop"

$services = @(
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

Write-Host "Generando reportes JaCoCo..."
Write-Host ""

foreach ($service in $services) {
    Write-Host "== $service =="
    Push-Location $service
    try {
        ./mvnw.cmd clean verify
    } finally {
        Pop-Location
    }
}

Write-Host ""
Write-Host "Resumen JaCoCo por instrucciones"
Write-Host "--------------------------------"

$totalMissed = 0
$totalCovered = 0

foreach ($service in $services) {
    $report = Join-Path $service "target/site/jacoco/jacoco.csv"
    if (-not (Test-Path $report)) {
        "{0,-15} sin reporte" -f $service
        continue
    }

    $rows = Import-Csv $report
    $missed = ($rows | Measure-Object -Property INSTRUCTION_MISSED -Sum).Sum
    $covered = ($rows | Measure-Object -Property INSTRUCTION_COVERED -Sum).Sum
    $total = $missed + $covered
    $percent = if ($total -gt 0) { ($covered / $total) * 100 } else { 0 }

    $totalMissed += $missed
    $totalCovered += $covered

    "{0,-15} {1,6:N2}%  ({2}/{3})" -f $service, $percent, $covered, $total
}

$grandTotal = $totalMissed + $totalCovered
$grandPercent = if ($grandTotal -gt 0) { ($totalCovered / $grandTotal) * 100 } else { 0 }
Write-Host "--------------------------------"
"{0,-15} {1,6:N2}%  ({2}/{3})" -f "TOTAL", $grandPercent, $totalCovered, $grandTotal

Write-Host ""
Write-Host "Reportes HTML:"
foreach ($service in $services) {
    $index = Join-Path $service "target/site/jacoco/index.html"
    if (Test-Path $index) {
        Write-Host $index
    }
}
