#!/usr/bin/env bash
set -euo pipefail

services=(
  eureka
  config-server
  videojuegos
  usuarios
  authentication
  carrito
  pagos
  pedidos
  resenas
  inventario
  api-gateway
)

echo "Generando reportes JaCoCo..."
echo

for service in "${services[@]}"; do
  echo "== $service =="
  (cd "$service" && ./mvnw clean verify)
done

echo
python3 - <<'PY'
from pathlib import Path
import csv

services = [
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
    "api-gateway",
]

total_missed = 0
total_covered = 0

print("Resumen JaCoCo por instrucciones")
print("--------------------------------")

for service in services:
    report = Path(service) / "target" / "site" / "jacoco" / "jacoco.csv"
    if not report.exists():
        print(f"{service:<15} sin reporte")
        continue

    missed = 0
    covered = 0
    with report.open(newline="") as file:
        for row in csv.DictReader(file):
            missed += int(row["INSTRUCTION_MISSED"])
            covered += int(row["INSTRUCTION_COVERED"])

    total = missed + covered
    percent = (covered / total * 100) if total else 0
    total_missed += missed
    total_covered += covered
    print(f"{service:<15} {percent:6.2f}%  ({covered}/{total})")

grand_total = total_missed + total_covered
grand_percent = (total_covered / grand_total * 100) if grand_total else 0
print("--------------------------------")
print(f"{'TOTAL':<15} {grand_percent:6.2f}%  ({total_covered}/{grand_total})")
print()
print("Reportes HTML:")
for service in services:
    index = Path(service) / "target" / "site" / "jacoco" / "index.html"
    if index.exists():
        print(index)
PY
