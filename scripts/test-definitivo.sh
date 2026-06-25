#!/usr/bin/env bash
set -u

BASE_URL="${BASE_URL:-http://localhost:8080}"
EUREKA_URL="${EUREKA_URL:-http://localhost:8761}"
CONFIG_URL="${CONFIG_URL:-http://localhost:8888}"

failures=0
TOKEN=""

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

login_and_capture_token() {
  local body_file
  body_file="$(mktemp)"

  local status
  status="$(curl --max-time 10 -s -o "$body_file" -w "%{http_code}" \
    -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"correo":"jesus@tiendajuegos.cl","password":"cliente123"}')"

  if [ "$status" != "200" ]; then
    echo "FAIL Login cliente demo - esperado HTTP 200, recibido HTTP $status"
    echo "Respuesta:"
    cat "$body_file"
    echo
    failures=$((failures + 1))
    rm -f "$body_file"
    return
  fi

  TOKEN="$(python3 -c 'import json,sys; print(json.load(open(sys.argv[1])).get("token",""))' "$body_file")"

  if [ -z "$TOKEN" ]; then
    echo "FAIL Login cliente demo - no se recibio token JWT"
    echo "Respuesta:"
    cat "$body_file"
    echo
    failures=$((failures + 1))
    rm -f "$body_file"
    return
  fi

  if ! grep -q '"nombreUsuario"' "$body_file"; then
    echo "FAIL Login cliente demo - no se encontro nombreUsuario"
    echo "Respuesta:"
    cat "$body_file"
    echo
    failures=$((failures + 1))
    rm -f "$body_file"
    return
  fi

  echo "OK   Login cliente demo con JWT"
  rm -f "$body_file"
}

echo "Prueba definitiva Tienda Videojuegos"
echo "Gateway: $BASE_URL"
echo

run_request "Eureka registra aplicaciones" "200" "applications" "$EUREKA_URL/eureka/apps"
run_request "Config Server entrega videojuegos/default" "200" "videojuegos" "$CONFIG_URL/videojuegos/default"
run_request "Swagger UI disponible" "200" "" "$BASE_URL/swagger-ui/index.html"
run_request "Swagger UI configura selector de APIs" "200" "Videojuegos" "$BASE_URL/v3/api-docs/swagger-config"
run_request "OpenAPI videojuegos disponible" "200" "openapi" "$BASE_URL/videojuegos/v3/api-docs"
run_request "OpenAPI usuarios disponible" "200" "openapi" "$BASE_URL/usuarios/v3/api-docs"
run_request "OpenAPI authentication disponible" "200" "openapi" "$BASE_URL/auth/v3/api-docs"
run_request "OpenAPI carrito disponible" "200" "openapi" "$BASE_URL/carrito/v3/api-docs"
run_request "OpenAPI pagos disponible" "200" "openapi" "$BASE_URL/pagos/v3/api-docs"
run_request "OpenAPI pedidos disponible" "200" "openapi" "$BASE_URL/pedidos/v3/api-docs"
run_request "OpenAPI resenas disponible" "200" "openapi" "$BASE_URL/resenas/v3/api-docs"
run_request "OpenAPI inventario disponible" "200" "openapi" "$BASE_URL/inventario/v3/api-docs"
run_request "Videojuegos lista catalogo" "200" "Cyberpunk 2077" "$BASE_URL/videojuegos"
run_request "Videojuegos incluye catalogo realista nuevo" "200" "Dead Cells" "$BASE_URL/videojuegos"
run_request "Videojuegos busca por plataforma PC" "200" "Cyberpunk 2077" "$BASE_URL/videojuegos/buscar?plataforma=PC"
run_request "Videojuegos busca por rango de precio" "200" "Dead Cells" "$BASE_URL/videojuegos/buscar?precioMin=10000&precioMax=16000"

login_and_capture_token

AUTH_HEADER=(-H "Authorization: Bearer $TOKEN")

run_request "Gateway enruta usuarios sin JWT" "200" "jesus@tiendajuegos.cl" "$BASE_URL/usuarios"

run_request "Usuarios lista cuentas demo" "200" "jesus@tiendajuegos.cl" "${AUTH_HEADER[@]}" "$BASE_URL/usuarios"
run_request "Usuarios incluye clientes realistas nuevos" "200" "catalina@tiendajuegos.cl" "${AUTH_HEADER[@]}" "$BASE_URL/usuarios"
run_request "Auth lista credenciales sin passwordHash" "200" "jesus@tiendajuegos.cl" "${AUTH_HEADER[@]}" "$BASE_URL/auth/credenciales"

run_request "Login admin demo" "200" "\"rol\":\"ADMIN\"" \
  -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"correo":"admin@tiendajuegos.cl","password":"admin123"}'

run_request "Login fallido controlado" "401" "Credenciales invalidas" \
  -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"correo":"jesus@tiendajuegos.cl","password":"incorrecta"}'

run_request "Carrito muestra nombreUsuario" "200" "nombreUsuario" "${AUTH_HEADER[@]}" "$BASE_URL/carrito/usuario/2"
run_request "Carrito muestra nombreVideojuego" "200" "nombreVideojuego" "${AUTH_HEADER[@]}" "$BASE_URL/carrito/usuario/2"
run_request "Carrito incluye campo resena" "200" "resena" "${AUTH_HEADER[@]}" "$BASE_URL/carrito/usuario/2"
run_request "Resumen carrito muestra total" "200" "\"total\"" "${AUTH_HEADER[@]}" "$BASE_URL/carrito/usuario/2/resumen"
run_request "Pagos muestra nombreUsuario" "200" "nombreUsuario" "${AUTH_HEADER[@]}" "$BASE_URL/pagos"
run_request "Pagos lista pagos" "200" "APROBADO" "${AUTH_HEADER[@]}" "$BASE_URL/pagos"
run_request "Pedidos muestra nombreUsuario" "200" "nombreUsuario" "${AUTH_HEADER[@]}" "$BASE_URL/pedidos"
run_request "Resenas muestra nombreUsuario" "200" "nombreUsuario" "$BASE_URL/resenas"
run_request "Inventario muestra nombreVideojuego" "200" "nombreVideojuego" "$BASE_URL/inventario"
run_request "Inventario bajo stock responde" "200" "" "$BASE_URL/inventario/bajo-stock"

run_request "Validacion devuelve errores controlados" "400" "validationErrors" \
  -X POST "$BASE_URL/videojuegos" \
  "${AUTH_HEADER[@]}" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"","categoria":"","precio":-1,"plataforma":""}'

run_request "Plataforma invalida devuelve error personalizado" "400" "Plataforma no valida" \
  -X POST "$BASE_URL/videojuegos" \
  "${AUTH_HEADER[@]}" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Prueba Plataforma Invalida","categoria":"Test","precio":10000,"plataforma":"Game Boy"}'

echo
if [ "$failures" -eq 0 ]; then
  echo "Resultado: OK - todas las pruebas pasaron."
  exit 0
fi

echo "Resultado: FAIL - pruebas fallidas: $failures"
exit 1
