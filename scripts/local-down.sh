#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PID_DIR="$ROOT_DIR/.local-pids"

if [[ ! -d "$PID_DIR" ]]; then
  echo "No hay procesos locales registrados."
  exit 0
fi

SERVICES=(
  api-gateway
  inventario
  resenas
  pedidos
  pagos
  carrito
  authentication
  usuarios
  videojuegos
  config-server
  eureka
)

for service in "${SERVICES[@]}"; do
  pid_file="$PID_DIR/$service.pid"

  if [[ -f "$pid_file" ]]; then
    pid="$(cat "$pid_file")"
    if kill -0 "$pid" >/dev/null 2>&1; then
      echo "Deteniendo $service PID $pid..."
      kill "$pid" || true
    else
      echo "$service no estaba corriendo."
    fi
    rm -f "$pid_file"
  fi
done

echo "Servicios locales detenidos."
