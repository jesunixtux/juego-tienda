# Tienda de Videojuegos - Microservicios

Proyecto de tienda de videojuegos hecho con Spring Boot, Spring Cloud Config,
Eureka, API Gateway, MySQL/XAMPP y Flyway.

## 1. Servicios del proyecto

Infraestructura:

- `config-server`: entrega configuracion centralizada desde `config-microservicios`.
- `eureka`: registra los microservicios.
- `api-gateway`: entrada principal para probar todo desde Postman.

Microservicios:

- `usuarios`
- `videojuegos`
- `inventario`
- `carrito`
- `authentication`
- `pagos`
- `pedidos`
- `resenas`

## 2. Requisitos

Necesitas tener:

- Java 25.
- IntelliJ IDEA.
- XAMPP con MySQL activo.
- Postman.
- Puerto `8888` libre para Config Server.
- Puerto `8761` libre para Eureka.
- Puerto `8080` libre para API Gateway.

## 3. Base de datos con Flyway

La base de datos se llama:

```text
tiendajuegos
```

Cada microservicio tiene migraciones Flyway en:

```text
src/main/resources/db/migration
```

Cuando levantas los microservicios, Flyway crea automaticamente las tablas y
carga datos iniciales.

Ejemplos de tablas que se crean:

```text
usuario
video_juego
inventario
item_carrito
credencial
pago
pedidos
resenas
```

Tambien se crean tablas de historial Flyway por servicio:

```text
flyway_schema_history_usuarios
flyway_schema_history_videojuegos
flyway_schema_history_inventario
flyway_schema_history_carrito
flyway_schema_history_authentication
flyway_schema_history_pagos
flyway_schema_history_pedidos
flyway_schema_history_resenas
```

Esto evita conflictos porque todos los microservicios usan la misma base
`tiendajuegos`.

## 4. Probar Flyway desde cero

1. Abre XAMPP.
2. Activa MySQL.
3. En phpMyAdmin borra solo la base:

```text
tiendajuegos
```

No borres bases internas como `mysql`, `phpmyadmin`, `information_schema` o
`performance_schema`.

Despues levanta los servicios en el orden indicado abajo. El primer
microservicio que se conecte creara la base `tiendajuegos`, y Flyway creara las
tablas.

## 5. Orden para levantar los servicios

En IntelliJ, levanta las aplicaciones en este orden:

1. `config-server`
2. `eureka`
3. `usuarios`
4. `videojuegos`
5. `inventario`
6. `carrito`
7. `authentication`
8. `pagos`
9. `pedidos`
10. `resenas`
11. `api-gateway`

El gateway va al final porque necesita que los microservicios ya esten
registrados en Eureka.

## 6. URLs importantes

Config Server:

```text
http://localhost:8888
```

Eureka:

```text
http://localhost:8761
```

API Gateway:

```text
http://localhost:8080
```

Puertos fijos de los microservicios:

| Servicio | Puerto |
| --- | ---: |
| `api-gateway` | `8080` |
| `videojuegos` | `8081` |
| `usuarios` | `8082` |
| `inventario` | `8083` |
| `carrito` | `8084` |
| `authentication` | `8085` |
| `pagos` | `8086` |
| `pedidos` | `8087` |
| `resenas` | `8088` |

En Postman se recomienda probar siempre por el gateway:

```text
http://localhost:8080
```

## 7. Verificar que la configuracion funciona

Config Server:

```http
GET http://localhost:8888/videojuegos/default
GET http://localhost:8888/usuarios/default
GET http://localhost:8888/api-gateway/default
```

Eureka:

```http
GET http://localhost:8761
```

Tambien puedes entrar desde el navegador a:

```text
http://localhost:8761
```

Deberias ver registrados servicios como:

```text
USUARIOS
VIDEOJUEGOS
INVENTARIO
CARRITO
AUTHENTICATION
PAGOS
PEDIDOS
RESENAS
API-GATEWAY
```

## 8. Login en Postman

Metodo:

```http
POST http://localhost:8080/auth/login
```

En Postman:

- Body
- raw
- JSON

Body:

```json
{
  "correo": "admin@tiendajuegos.cl",
  "password": "password"
}
```

Respuesta esperada:

```json
{
  "usuarioId": 1,
  "correo": "admin@tiendajuegos.cl",
  "rol": "ADMIN",
  "mensaje": "Login exitoso",
  "autenticado": true
}
```

## 9. Endpoints principales

### Usuarios

Listar usuarios:

```http
GET http://localhost:8080/usuarios
```

Buscar por ID:

```http
GET http://localhost:8080/usuarios/1
```

Buscar por correo:

```http
GET http://localhost:8080/usuarios/buscar?correo=admin@tiendajuegos.cl
```

Crear usuario:

```http
POST http://localhost:8080/usuarios
```

Body:

```json
{
  "nombre": "Pedro",
  "apellido": "Gonzalez",
  "correo": "pedro@tiendajuegos.cl",
  "telefono": "+56955555555",
  "direccion": "Santiago",
  "rol": "CLIENTE",
  "activo": true
}
```

### Videojuegos

Listar videojuegos:

```http
GET http://localhost:8080/videojuegos
```

Buscar por ID:

```http
GET http://localhost:8080/videojuegos/1
```

Buscar por nombre:

```http
GET http://localhost:8080/videojuegos/buscar?nombre=Portal
```

Crear videojuego:

```http
POST http://localhost:8080/videojuegos
```

Body:

```json
{
  "nombre": "DOOM Eternal",
  "categoria": "Shooter",
  "precio": 29990,
  "plataforma": "PC",
  "descripcion": "Shooter rapido con accion intensa.",
  "desarrollador": "id Software",
  "fechaLanzamiento": "2020-03-20",
  "imagenUrl": "https://cdn.cloudflare.steamstatic.com/steam/apps/782330/header.jpg",
  "activo": true
}
```

### Inventario

Listar inventario:

```http
GET http://localhost:8080/inventario
```

Buscar inventario por videojuego:

```http
GET http://localhost:8080/inventario/videojuego/1
```

Ver bajo stock:

```http
GET http://localhost:8080/inventario/bajo-stock
```

Crear inventario:

```http
POST http://localhost:8080/inventario
```

Body:

```json
{
  "videojuegoId": 1,
  "stock": 20,
  "stockMinimo": 5
}
```

Actualizar stock exacto:

```http
PUT http://localhost:8080/inventario/videojuego/1/stock
```

Body:

```json
{
  "stock": 25
}
```

Entrada de stock:

```http
PUT http://localhost:8080/inventario/videojuego/1/entrada
```

Body:

```json
{
  "cantidad": 5
}
```

Salida de stock:

```http
PUT http://localhost:8080/inventario/videojuego/1/salida
```

Body:

```json
{
  "cantidad": 2
}
```

### Carrito

Ver carrito de usuario:

```http
GET http://localhost:8080/carrito/usuario/2
```

Ver resumen del carrito:

```http
GET http://localhost:8080/carrito/usuario/2/resumen
```

Agregar item:

```http
POST http://localhost:8080/carrito
```

Body:

```json
{
  "usuarioId": 2,
  "videojuegoId": 24,
  "cantidad": 1
}
```

Actualizar cantidad:

```http
PUT http://localhost:8080/carrito/1/cantidad
```

Body:

```json
{
  "cantidad": 3
}
```

Vaciar carrito de usuario:

```http
DELETE http://localhost:8080/carrito/usuario/2
```

### Pagos

Listar pagos:

```http
GET http://localhost:8080/pagos
```

Pagos por usuario:

```http
GET http://localhost:8080/pagos/usuario/2
```

Crear pago desde carrito:

```http
POST http://localhost:8080/pagos
```

Body:

```json
{
  "usuarioId": 2,
  "metodoPago": "TARJETA"
}
```

Actualizar estado:

```http
PUT http://localhost:8080/pagos/1/estado
```

Body:

```json
{
  "estado": "APROBADO"
}
```

Anular pago:

```http
PUT http://localhost:8080/pagos/1/anular
```

### Pedidos

Listar pedidos:

```http
GET http://localhost:8080/pedidos
```

Pedidos con detalle de usuario:

```http
GET http://localhost:8080/pedidos/detalle
```

Pedidos por usuario:

```http
GET http://localhost:8080/pedidos/usuario/2
```

Crear pedido:

```http
POST http://localhost:8080/pedidos
```

Body:

```json
{
  "usuarioId": 2,
  "nombreJuego": "Portal 2",
  "precio": 9990,
  "fechaPedido": "2026-05-17"
}
```

### Resenas

Listar resenas:

```http
GET http://localhost:8080/resenas
```

Resenas con detalle de usuario:

```http
GET http://localhost:8080/resenas/detalle
```

Resenas por usuario:

```http
GET http://localhost:8080/resenas/usuario/2
```

Crear resena:

```http
POST http://localhost:8080/resenas
```

Body:

```json
{
  "usuarioId": 2,
  "nombreJuego": "Portal 2",
  "comentario": "Muy buen juego de puzzles.",
  "puntuacion": 5,
  "fechaResena": "2026-05-17"
}
```

## 10. Flujo de prueba recomendado en Postman

1. Login:

```http
POST http://localhost:8080/auth/login
```

2. Ver juegos:

```http
GET http://localhost:8080/videojuegos
```

3. Ver inventario:

```http
GET http://localhost:8080/inventario
```

4. Agregar juego al carrito:

```http
POST http://localhost:8080/carrito
```

Body:

```json
{
  "usuarioId": 2,
  "videojuegoId": 24,
  "cantidad": 1
}
```

5. Ver resumen:

```http
GET http://localhost:8080/carrito/usuario/2/resumen
```

6. Pagar carrito:

```http
POST http://localhost:8080/pagos
```

Body:

```json
{
  "usuarioId": 2,
  "metodoPago": "TARJETA"
}
```

7. Ver pagos:

```http
GET http://localhost:8080/pagos
```

## 11. Datos iniciales importantes

Usuarios iniciales:

```text
1 - admin@tiendajuegos.cl - ADMIN
2 - jesus@tiendajuegos.cl - CLIENTE
3 - camila@tiendajuegos.cl - CLIENTE
4 - matias@tiendajuegos.cl - CLIENTE
```

Credencial inicial:

```text
correo: admin@tiendajuegos.cl
password: password
```

Videojuegos iniciales:

```text
35 videojuegos cargados por Flyway
```

Incluye juegos como:

```text
Cyberpunk 2077
Minecraft
God of War Ragnarok
Portal
Portal 2
Half-Life
Half-Life 2
Left 4 Dead 2
Hollow Knight
Terraria
Cuphead
```

## 12. Errores comunes

### Error 401 en login

Verifica que el body sea JSON y que uses:

```json
{
  "correo": "admin@tiendajuegos.cl",
  "password": "password"
}
```

Tambien verifica que el header sea:

```text
Content-Type: application/json
```

### Gateway devuelve 500 o se queda cargando

Reinicia en este orden:

1. `eureka`
2. microservicios
3. `api-gateway`

Tambien verifica en:

```text
http://localhost:8761
```

que todos los servicios esten `UP`.

### Un servicio no aparece en Eureka

Detenlo y levantalo otra vez. Si sigue sin aparecer, revisa que tenga:

```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.hostname=localhost
eureka.instance.prefer-ip-address=false
```

### Flyway no crea la base

Verifica que MySQL este activo en XAMPP.

El datasource usa:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tiendajuegos?createDatabaseIfNotExist=true
```

### Error de MariaDB mysql.proc

Si aparece un error parecido a:

```text
Column count of mysql.proc is wrong
Please use mysql_upgrade
```

Ejecuta:

```bash
/Applications/XAMPP/xamppfiles/bin/mysql_upgrade -u root --force --force
```

Despues reinicia MySQL desde XAMPP.

## 13. Comandos utiles

Compilar un microservicio sin tests:

```bash
./mvnw package -DskipTests
```

Levantar un microservicio por consola:

```bash
./mvnw spring-boot:run
```

Ver si Config Server responde:

```bash
curl http://localhost:8888/usuarios/default
```

Ver si Eureka responde:

```bash
curl http://localhost:8761/eureka/apps
```

## 14. Nota final

Si cambias un archivo dentro de `config-microservicios`, debes reiniciar el
microservicio afectado para que lea la configuracion nueva.

Para probar desde Postman, usa siempre el gateway:

```text
http://localhost:8080
```
