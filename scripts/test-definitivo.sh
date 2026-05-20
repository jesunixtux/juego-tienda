#!/usr/bin/env bash
set -u

BASE_URL="${BASE_URL:-http://localhost:8080}"
EUREKA_URL="${EUREKA_URL:-http://localhost:8761}"
CONFIG_URL="${CONFIG_URL:-http://localhost:8888}"

failures=0

run_request() {
  local name="$1"
  local expected_status="$2"
  local expected_text="$3"
  shift 3

  local body_file
  body_file="$(mktemp)"

  local status
  status="$(curl --max-time 10 -s -o "$body_file" -w "%{http_code}" "$@")"

  if [ "$status" != "$expected_status" ]; then
    echo "FAIL $name - esperado HTTP $expected_status, recibido HTTP $status"
    echo "Respuesta:"
    cat "$body_file"
    echo
    failures=$((failures + 1))
    rm -f "$body_file"
    return
  fi

  if [ -n "$expected_text" ] && ! grep -q "$expected_text" "$body_file"; then
    echo "FAIL $name - no se encontro texto esperado: $expected_text"
    echo "Respuesta:"
    cat "$body_file"
    echo
    failures=$((failures + 1))
    rm -f "$body_file"
    return
  fi

  echo "OK   $name"
  rm -f "$body_file"
}

echo "Prueba definitiva Tienda Videojuegos"
echo "Gateway: $BASE_URL"
echo

run_request "Eureka registra aplicaciones" "200" "applications" "$EUREKA_URL/eureka/apps"
run_request "Config Server entrega videojuegos/default" "200" "videojuegos" "$CONFIG_URL/videojuegos/default"
run_request "Videojuegos lista catalogo" "200" "Cyberpunk 2077" "$BASE_URL/videojuegos"
run_request "Usuarios lista cuentas demo" "200" "jesus@tiendajuegos.cl" "$BASE_URL/usuarios"
run_request "Auth lista credenciales" "200" "jesus@tiendajuegos.cl" "$BASE_URL/auth/credenciales"

run_request "Login cliente demo" "200" "\"autenticado\":true" \
  -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"correo":"jesus@tiendajuegos.cl","password":"cliente123"}'

run_request "Login admin demo" "200" "\"rol\":\"ADMIN\"" \
  -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"correo":"admin@tiendajuegos.cl","password":"admin123"}'

run_request "Login fallido controlado" "401" "Credenciales invalidas" \
  -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"correo":"jesus@tiendajuegos.cl","password":"incorrecta"}'

run_request "Carrito muestra nombreVideojuego" "200" "nombreVideojuego" "$BASE_URL/carrito/usuario/2"
run_request "Resumen carrito muestra total" "200" "\"total\"" "$BASE_URL/carrito/usuario/2/resumen"
run_request "Pagos lista pagos" "200" "APROBADO" "$BASE_URL/pagos"
run_request "Pedidos muestra nombreUsuario" "200" "nombreUsuario" "$BASE_URL/pedidos"
run_request "Resenas muestra nombreUsuario" "200" "nombreUsuario" "$BASE_URL/resenas"
run_request "Inventario muestra nombreVideojuego" "200" "nombreVideojuego" "$BASE_URL/inventario"
run_request "Inventario bajo stock responde" "200" "" "$BASE_URL/inventario/bajo-stock"

run_request "Validacion devuelve errores controlados" "400" "validationErrors" \
  -X POST "$BASE_URL/videojuegos" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"","categoria":"","precio":-1,"plataforma":""}'

echo
if [ "$failures" -eq 0 ]; then
  echo "Resultado: OK - todas las pruebas pasaron."
  exit 0
fi

echo "Resultado: FAIL - pruebas fallidas: $failures"
exit 1
