#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOG_DIR="$ROOT_DIR/.local-logs"
PID_DIR="$ROOT_DIR/.local-pids"

mkdir -p "$LOG_DIR" "$PID_DIR"

export CONFIG_SERVER_URL="${CONFIG_SERVER_URL:-http://localhost:8888}"
export CONFIG_SERVER_SEARCH_LOCATIONS="${CONFIG_SERVER_SEARCH_LOCATIONS:-file:$ROOT_DIR/config-microservicios}"
export EUREKA_SERVER_URL="${EUREKA_SERVER_URL:-http://localhost:8761/eureka/}"
export EUREKA_CLIENT_SERVICEURL_DEFAULTZONE="${EUREKA_CLIENT_SERVICEURL_DEFAULTZONE:-$EUREKA_SERVER_URL}"
export DB_HOST="${DB_HOST:-localhost}"
export DB_PORT="${DB_PORT:-3306}"
export DB_USER="${DB_USER:-root}"
export DB_PASSWORD="${DB_PASSWORD:-}"

SERVICES=(
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

start_service() {
  local service="$1"
  local pid_file="$PID_DIR/$service.pid"
  local log_file="$LOG_DIR/$service.log"

  if [[ -f "$pid_file" ]] && kill -0 "$(cat "$pid_file")" >/dev/null 2>&1; then
    echo "$service ya esta corriendo con PID $(cat "$pid_file")"
    return
  fi

  echo "Levantando $service..."
  (
    cd "$ROOT_DIR/$service"
    nohup ./mvnw spring-boot:run >"$log_file" 2>&1 &
    echo $! >"$pid_file"
  )
}

for service in "${SERVICES[@]}"; do
  start_service "$service"

  case "$service" in
    eureka|config-server)
      sleep 10
      ;;
    api-gateway)
      sleep 3
      ;;
    *)
      sleep 2
      ;;
  esac
done

echo
echo "Servicios levantandose. Revisa logs en $LOG_DIR"
echo "API Gateway: http://localhost:8080"
echo "Swagger:     http://localhost:8080/swagger-ui/index.html"
echo "Eureka:      http://localhost:8761"
