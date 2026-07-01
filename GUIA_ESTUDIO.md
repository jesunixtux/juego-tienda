# Guia de estudio - Proyecto Tienda de Videojuegos

Esta guia esta pensada para estudiar la disertacion final del proyecto. Resume como funciona la aplicacion, como levantarla localmente o con Docker, como moverla a otra PC, que explicar en Swagger/Postman y que preguntas podria hacer el profesor.

## 1. Idea general del proyecto

El proyecto es una tienda de videojuegos construida con arquitectura de microservicios. En vez de tener una sola aplicacion gigante, el sistema esta separado por responsabilidades:

- `videojuegos`: catalogo de juegos.
- `usuarios`: datos de usuarios y roles.
- `authentication`: registro, login y credenciales.
- `carrito`: productos agregados por usuario.
- `pagos`: pagos generados desde el carrito.
- `pedidos`: pedidos y reportes.
- `resenas`: comentarios y puntuaciones.
- `inventario`: stock por videojuego.
- `api-gateway`: punto unico de entrada.
- `eureka`: descubrimiento de servicios.
- `config-server`: configuracion centralizada.

La idea principal para explicar:

> El cliente no llama directamente a cada microservicio. Primero entra por el API Gateway, que redirige la peticion al microservicio correcto usando Eureka. Cada microservicio tiene su propia base de datos MySQL y sus tablas se crean con Flyway. En esta version no se usa Spring Security ni se exige token para consumir endpoints.

## 2. Arquitectura

```text
Cliente / Swagger / Postman
        |
        v
API Gateway :8080
        |
        +-- enruta peticiones REST
        +-- enruta con lb://nombre-servicio
        |
        v
Eureka :8761
        |
        +-- videojuegos
        +-- usuarios
        +-- authentication
        +-- carrito
        +-- pagos
        +-- pedidos
        +-- resenas
        +-- inventario

Config Server :8888
        |
        v
config-microservicios/*.yml

MySQL Docker :3306 interno / 3307 en la PC
```

## 3. Tecnologias principales

- Java 25.
- Spring Boot 4.
- Spring Cloud Config Server.
- Spring Cloud Netflix Eureka.
- Spring Cloud Gateway WebFlux.
- OpenFeign para llamadas entre microservicios.
- Spring Data JPA.
- Flyway para migraciones SQL.
- MySQL 8.4 en Docker.
- H2 para pruebas automatizadas.
- Autenticacion funcional sin tokens obligatorios ni Spring Security.
- Hash SHA-256 con sal para guardar passwords.
- Swagger/OpenAPI para documentar y probar endpoints.
- Docker Compose para levantar todo el sistema.
- phpMyAdmin para revisar bases de datos.
- Apache con PHP como extra de apoyo.

## 4. Que hace cada componente

### API Gateway

Es la entrada principal del sistema.

URL:

```text
http://localhost:8080
```

Responsabilidades:

- Recibe todas las peticiones.
- Redirige rutas como `/videojuegos`, `/usuarios`, `/carrito`, `/pagos`, etc.
- No aplica Spring Security ni exige token.
- Permite probar todos los endpoints desde Swagger/Postman sin header de autorizacion.

Ejemplo:

```text
GET http://localhost:8080/videojuegos
```

El Gateway toma esa peticion y la manda al microservicio `videojuegos`.

### Eureka

URL:

```text
http://localhost:8761
```

Responsabilidades:

- Permite que los microservicios se registren.
- Permite que el Gateway encuentre servicios por nombre.
- Evita depender de puertos aleatorios o IP fijas.

En Eureka deberian aparecer:

- `API-GATEWAY`
- `VIDEOJUEGOS`
- `USUARIOS`
- `AUTHENTICATION`
- `CARRITO`
- `PAGOS`
- `PEDIDOS`
- `RESENAS`
- `INVENTARIO`

### Config Server

URL de ejemplo:

```text
http://localhost:8888/videojuegos/default
```

Responsabilidades:

- Lee la carpeta `config-microservicios`.
- Entrega configuracion externa a cada microservicio.
- Centraliza puertos, base de datos, Eureka y Swagger.

Ejemplo:

```text
config-microservicios/videojuegos.yml
config-microservicios/api-gateway.yml
```

### MySQL

En Docker:

```text
Contenedor: mysql:3306
PC local: localhost:3307
```

Se usa `3307` en la PC para evitar conflicto con XAMPP o MySQL local en `3306`.

### phpMyAdmin

URL:

```text
http://localhost:8082
```

Credenciales por defecto:

```text
Servidor: mysql
Usuario: root
Password: vacia
```

Sirve para revisar tablas y datos cargados por Flyway.

## 5. Bases de datos

Cada microservicio usa su propia base de datos:

| Microservicio | Base de datos |
| --- | --- |
| videojuegos | `bd_videojuegos` |
| usuarios | `bd_usuarios` |
| authentication | `bd_auth` |
| carrito | `bd_carrito` |
| pagos | `bd_pagos` |
| pedidos | `bd_pedidos` |
| resenas | `bd_resenas` |
| inventario | `bd_inventario` |

Esto permite separar responsabilidades. Cada servicio es dueno de sus datos.

## 6. Flyway

Flyway ejecuta archivos SQL ubicados en:

```text
src/main/resources/db/migration
```

Sirve para:

- Crear tablas.
- Cargar datos iniciales.
- Mantener versiones de la base de datos.
- Evitar crear tablas manualmente.

Respuesta corta para el profesor:

> Flyway versiona la base de datos. Cuando inicia el microservicio, revisa que migraciones faltan y las ejecuta automaticamente.

## 7. Autenticacion Sin Spring Security

Flujo:

1. El usuario llama a `/auth/login`.
2. `authentication` valida correo y password hasheada.
3. Si es correcto, devuelve datos funcionales del usuario.
4. El API Gateway no valida tokens; solo enruta al microservicio correspondiente.
5. En Swagger/Postman se pueden probar los endpoints sin autorizacion.

Como explicarlo al profesor:

- Se quito Spring Security para evitar bloqueos durante la evaluacion y facilitar pruebas locales/Docker.
- El servicio `authentication` sigue existiendo porque demuestra login, registro, credenciales y pruebas unitarias.
- Las passwords no quedan en texto plano; se guardan con SHA-256 y sal.

## 8. Swagger

URL principal:

```text
http://localhost:8080/swagger-ui/index.html
```

Swagger permite seleccionar APIs:

- Videojuegos
- Usuarios
- Authentication
- Carrito
- Pagos
- Pedidos
- Resenas
- Inventario

Que debes explicar:

- Cada microservicio tiene su propio documento OpenAPI.
- El Gateway agrupa todos los documentos en una sola pantalla.
- Los endpoints tienen resumen y descripcion funcional.
- Los modelos muestran ejemplos de JSON, como usuario, videojuego, carrito, pago, pedido, resena e inventario.
- Las respuestas documentan codigos comunes como 400, 401, 404, 409, 500 y 502 segun el caso.
- Ya no aparece el candado `Authorize` porque no se exige token.
- El login se puede mostrar como endpoint funcional, pero no es requisito para probar el resto.
- Los OpenAPI usan servidor relativo `/`, por eso `Execute` llama al mismo host donde abriste Swagger.
- Si `Execute` solo muestra el curl, revisar que el microservicio seleccionado este levantado y que Swagger se abra con `localhost:8080`.

Credenciales demo:

```json
{
  "correo": "jesus@tiendajuegos.cl",
  "password": "cliente123"
}
```

Admin:

```json
{
  "correo": "admin@tiendajuegos.cl",
  "password": "admin123"
}
```

## 9. Endpoints importantes para demostrar

### Videojuegos

```text
GET  /videojuegos
GET  /videojuegos/{id}
GET  /videojuegos/buscar?plataforma=PC
GET  /videojuegos/buscar?precioMin=10000&precioMax=30000
POST /videojuegos
PUT  /videojuegos/{id}
DELETE /videojuegos/{id}
```

Punto importante:

- Se valida plataforma.
- Plataformas permitidas: `PS5`, `PS4`, `XBOX`, `PC`, `PlayStation`, `Nintendo Switch`, `PC VR`.
- Si se manda una plataforma invalida, responde error personalizado.

### Auth

```text
POST /auth/registro
POST /auth/login
GET  /auth/credenciales
PUT  /auth/credenciales/{id}/password
DELETE /auth/credenciales/{id}
```

Punto importante:

- Password se guarda con SHA-256 y sal.
- Password obligatoria con minimo 5 caracteres.
- Si falta o tiene menos de 5 caracteres responde `400 Contrasena invalida`.
- No se expone `passwordHash` en las respuestas.
- Login devuelve datos de usuario.

### Usuarios

```text
GET  /usuarios
GET  /usuarios?activos=true
GET  /usuarios/{id}
GET  /usuarios/buscar?correo=...
POST /usuarios
PUT  /usuarios/{id}
DELETE /usuarios/{id}
```

Punto importante:

- `/usuarios` administra datos personales.
- La cuenta con password se crea por `/auth/registro`.

Punto importante:

- `DELETE` no borra fisicamente, desactiva el usuario.

### Carrito

```text
GET  /carrito/usuario/{usuarioId}
GET  /carrito/usuario/{usuarioId}/resumen
POST /carrito
PUT  /carrito/{id}/cantidad
DELETE /carrito/{id}
DELETE /carrito/usuario/{usuarioId}
```

Punto importante:

- Muestra `nombreUsuario`.
- Muestra `nombreVideojuego`.
- Si el usuario hizo una resena del juego, tambien muestra esa resena.
- Calcula subtotal y total.

### Pagos

```text
GET  /pagos
GET  /pagos/{id}
GET  /pagos/usuario/{usuarioId}
POST /pagos
PUT  /pagos/{id}/estado
PUT  /pagos/{id}/anular
DELETE /pagos/{id}
```

Punto importante:

- Para crear un pago, `pagos` llama a `carrito`.
- Usa el total del carrito.
- Genera codigo `PAY-XXXX`.
- Despues de pagar, vacia el carrito.

### Pedidos

```text
GET /pedidos
GET /pedidos/{id}
GET /pedidos/usuario/{usuarioId}
GET /pedidos/reportes/fecha?desde=2026-05-01&hasta=2026-05-31
GET /pedidos/reportes/precio?minimo=10000&maximo=50000
```

Punto importante:

- Muestra `nombreUsuario`.
- Tiene reportes por fecha y precio.

### Resenas

```text
GET /resenas
GET /resenas/{id}
GET /resenas/usuario/{usuarioId}
GET /resenas/reportes/fecha?desde=2026-05-01&hasta=2026-05-31
GET /resenas/reportes/puntuacion?min=4&max=5
```

Punto importante:

- Muestra `nombreUsuario`.
- Puntuacion debe estar entre 1 y 5.

### Inventario

```text
GET /inventario
GET /inventario/bajo-stock
GET /inventario/videojuego/{videojuegoId}
POST /inventario
PUT /inventario/videojuego/{videojuegoId}/stock
PUT /inventario/videojuego/{videojuegoId}/entrada
PUT /inventario/videojuego/{videojuegoId}/salida
```

Punto importante:

- Muestra `nombreVideojuego`.
- Controla stock insuficiente con error personalizado.
- `bajo-stock` devuelve juegos con stock menor o igual al minimo.

## 10. Flujo recomendado para demostrar

1. Abrir Eureka:

```text
http://localhost:8761
```

Mostrar que todos los servicios estan registrados.

2. Abrir Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

3. Login:

```json
{
  "correo": "jesus@tiendajuegos.cl",
  "password": "cliente123"
}
```

4. Probar endpoints directamente desde Swagger:

```text
GET /videojuegos
GET /videojuegos/buscar?precioMin=10000&precioMax=30000
GET /carrito/usuario/2
GET /carrito/usuario/2/resumen
POST /pagos
GET /inventario/bajo-stock
```

6. Mostrar error personalizado:

```json
{
  "nombre": "Juego Invalido",
  "categoria": "Test",
  "precio": 10000,
  "plataforma": "Dreamcast"
}
```

Resultado esperado:

```text
400 Plataforma no valida
```

## 11. Ejecucion Local Sin Docker

Este modo sirve cuando tienes MySQL/XAMPP/MariaDB instalado en la maquina y disponible en `localhost:3306`.

En macOS/Linux:

```bash
bash scripts/local-up.sh
```

Para detener:

```bash
bash scripts/local-down.sh
```

En Windows PowerShell:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/local-up.ps1
```

Para detener:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/local-down.ps1
```

Que hace el script:

- Levanta Eureka.
- Levanta Config Server usando `config-microservicios`.
- Levanta todos los microservicios.
- Levanta API Gateway al final.
- Guarda logs en `.local-logs`.

## 12. Docker

### Archivos importantes

```text
Dockerfile
docker-compose.yml
.dockerignore
.env.example
docker/apache-php/
docker/maven-repository/
scripts/preparar-dependencias-docker.sh
scripts/preparar-dependencias-docker.ps1
```

El `Dockerfile` principal usa una etapa de build con `maven:3.9.11-eclipse-temurin-25` y una etapa final con `eclipse-temurin:25-jre`. Esto evita depender del Maven Wrapper dentro de Docker.

Ademas, el build quedo preparado de forma hibrida:

- Primero copia dependencias desde `docker/maven-repository` hacia `/root/.m2/repository`.
- Luego usa cache de Docker BuildKit en `/root/.m2`.
- Intenta compilar con Maven en modo offline.
- Si falta una dependencia, hace un segundo intento online.

Respuesta corta para el profesor:

> El Dockerfile esta optimizado para no depender completamente de internet. Si ya llevo la carpeta `docker/maven-repository`, Docker puede compilar usando esas dependencias. Si falta algo y hay internet, Maven lo descarga.

### Preparar dependencias portables

En macOS/Linux:

```bash
./scripts/preparar-dependencias-docker.sh
```

En Windows PowerShell:

```powershell
.\scripts\preparar-dependencias-docker.ps1
```

Esto copia dependencias Maven locales a:

```text
docker/maven-repository
```

Esa carpeta puede pesar varios cientos de MB. No se sube a Git, pero sirve para copiar y pegar el proyecto completo a otra PC antes de la evaluacion.

### Servicios Docker

```text
mysql
eureka
config-server
api-gateway
videojuegos
usuarios
authentication
carrito
pagos
pedidos
resenas
inventario
apache-php
phpmyadmin
```

### Puertos

| Servicio | URL |
| --- | --- |
| API Gateway | `http://localhost:8080` |
| Swagger | `http://localhost:8080/swagger-ui/index.html` |
| Eureka | `http://localhost:8761` |
| Config Server | `http://localhost:8888/videojuegos/default` |
| Apache/PHP | `http://localhost:8081` |
| phpMyAdmin | `http://localhost:8082` |
| MySQL desde PC | `localhost:3307` |

### Levantar en Docker

Desde la raiz del proyecto:

```bash
cp .env.example .env
docker compose build
docker compose up -d
docker compose ps
```

### Ver logs

```bash
docker compose logs -f api-gateway
docker compose logs -f videojuegos
docker compose logs -f mysql
```

### Apagar

```bash
docker compose down
```

### Apagar y borrar base de datos Docker

```bash
docker compose down -v
```

Usar `down -v` solo si quieres reiniciar datos desde cero.

## 13. Como mover el proyecto a otra PC

### Opcion A: usando GitHub

En la otra PC:

```bash
git clone URL_DEL_REPOSITORIO
cd juego-tienda
cp .env.example .env
./scripts/preparar-dependencias-docker.sh
docker compose build
docker compose up -d
```

En Windows PowerShell:

```powershell
copy .env.example .env
.\scripts\preparar-dependencias-docker.ps1
docker compose build
docker compose up -d
```

### Opcion B: copiando carpeta

1. Comprimir la carpeta `juego-tienda`.
2. Asegurarse de incluir `docker/maven-repository` si ya preparaste dependencias.
3. Copiarla a la otra PC.
4. Descomprimirla.
5. Abrir terminal en la carpeta.
6. Ejecutar:

```bash
cp .env.example .env
docker compose build
docker compose up -d
```

En Windows:

```powershell
copy .env.example .env
docker compose build
docker compose up -d
```

Si no copiaste `docker/maven-repository`, ejecuta el script de preparacion en la otra PC antes de `docker compose build`.

### Requisitos de la otra PC

- Docker Desktop instalado.
- Docker Compose disponible.
- Internet la primera vez para descargar imagenes base de Docker.
- Internet para dependencias Maven solo si no llevas `docker/maven-repository` o si falta alguna dependencia.
- Puertos libres:
  - `8080`
  - `8081`
  - `8082`
  - `8761`
  - `8888`
  - `3307`

No es obligatorio instalar Java ni MySQL si se usa Docker, porque Java y MySQL van dentro de los contenedores.

### Si hay conflicto de puerto

Editar `.env`:

```text
MYSQL_HOST_PORT=3307
APACHE_PHP_HOST_PORT=8081
PHPMYADMIN_HOST_PORT=8082
```

Por ejemplo, si `8080` esta ocupado, hay que cambiar el mapeo del `api-gateway` en `docker-compose.yml`.

## 14. Comandos utiles para la defensa

Ver servicios:

```bash
docker compose ps
```

Ver logs del Gateway:

```bash
docker compose logs -f api-gateway
```

Probar todo:

```bash
BASE_URL=http://localhost:8080 EUREKA_URL=http://localhost:8761 CONFIG_URL=http://localhost:8888 bash scripts/test-definitivo.sh
```

Reconstruir despues de cambiar codigo:

```bash
docker compose build
docker compose up -d
```

Reiniciar un servicio:

```bash
docker compose restart api-gateway
```

Entrar a MySQL:

```bash
docker compose exec mysql mysql -u root
```

## 15. Preguntas probables del profesor

### Que es un microservicio?

Es una aplicacion pequena con una responsabilidad clara. En este proyecto, por ejemplo, `videojuegos` solo maneja catalogo y `usuarios` solo maneja usuarios.

### Por que separaron el proyecto en microservicios?

Para separar responsabilidades, facilitar mantenimiento y permitir que cada modulo evolucione de manera independiente.

### Para que sirve Eureka?

Sirve para descubrimiento de servicios. Los microservicios se registran y el Gateway puede encontrarlos por nombre.

### Para que sirve Config Server?

Centraliza configuracion. Asi no repetimos configuraciones en todos los proyectos y podemos cambiar propiedades sin modificar codigo.

### Que hace API Gateway?

Es la puerta de entrada. Redirige peticiones al microservicio correspondiente usando las rutas configuradas y los nombres registrados en Eureka.

### Como se comunican los microservicios?

Con OpenFeign. Por ejemplo, `carrito` llama a `videojuegos` para obtener el nombre y precio de un juego, y llama a `usuarios` para obtener el nombre del usuario.

### Que pasa si un microservicio no esta disponible?

Las llamadas Feign pueden fallar y el sistema responde con errores controlados, como "No se pudo consultar el microservicio videojuegos".

### Que hace Flyway?

Crea y actualiza tablas con migraciones SQL. Evita crear la base de datos manualmente.

### Como se maneja la autenticacion?

Con el microservicio `authentication`. Valida correo y password, devuelve datos del usuario y mantiene las credenciales hasheadas. El API Gateway no exige token porque se quito Spring Security para facilitar las pruebas del proyecto.

### Donde se valida el token?

En esta version no hay token que validar. El proyecto queda abierto para pruebas academicas por Swagger, Postman, Docker e IntelliJ.

### Por que se usa SHA-256 con sal?

Para no guardar passwords en texto plano sin depender de librerias de seguridad adicionales. En produccion seria mejor usar un algoritmo dedicado como Argon2, pero aqui se retiro la capa de seguridad por requisito del proyecto.

### Por que MySQL corre en puerto 3307?

Porque dentro de Docker usa 3306, pero hacia la PC se expone en 3307 para evitar conflicto con XAMPP o MySQL local.

### Como sabes que funciona?

Porque se probo con:

- Docker Compose.
- Swagger.
- Script `scripts/test-definitivo.sh`.
- Tests Maven por microservicio.
- Pruebas de flujo completo con registro, login, carrito, pago e inventario.

## 16. Preguntas de modificacion que podrian pedir

### Agregar una plataforma nueva

Editar:

```text
videojuegos/src/main/java/cl/duoc/videojuegos/service/VideoJuegoService.java
```

Agregar la plataforma en `PLATAFORMAS_PERMITIDAS` y actualizar el texto visible.

### Agregar busqueda por desarrollador

1. Agregar metodo en `VideoJuegoRepository`.
2. Agregar metodo en `VideoJuegoService`.
3. Agregar parametro en `VideoJuegoController`.
4. Actualizar Swagger/Postman.
5. Probar con `GET /videojuegos/buscar?desarrollador=...`.

### Agregar un nuevo campo a videojuegos

1. Agregar campo en entidad `VideoJuego`.
2. Crear migracion Flyway si cambia tabla.
3. Actualizar datos iniciales si corresponde.
4. Actualizar pruebas y Swagger.

### Agregar seguridad nuevamente si el profesor lo pide

Si pidieran proteger rutas, habria que agregar nuevamente una dependencia de seguridad, crear una configuracion del gateway y definir una estrategia de autorizacion. Actualmente esa capa esta retirada para que todos los endpoints sean probables sin bloqueo.

### Agregar un microservicio nuevo

Pasos:

1. Crear nuevo modulo Spring Boot.
2. Agregar dependencia Eureka Client.
3. Agregar dependencia Config Client.
4. Crear archivo en `config-microservicios`.
5. Agregar ruta en `api-gateway.yml`.
6. Agregar servicio en `docker-compose.yml`.
7. Agregar Swagger si corresponde.
8. Probar en Eureka y Gateway.

### Cambiar base de datos

Editar el YAML del microservicio en:

```text
config-microservicios/*.yml
```

En Docker normalmente se usan:

```text
DB_HOST=mysql
DB_PORT=3306
DB_USER=root
DB_PASSWORD=
```

## 17. Errores comunes y solucion

### Docker no levanta

Revisar que Docker Desktop este abierto:

```bash
docker info
```

### Puerto ocupado

Ver si `8080`, `8761`, `8888` o `3307` estan ocupados. Cambiar puertos en `.env` o `docker-compose.yml`.

### Swagger no muestra cambios

Si estas en Docker, reconstruir imagenes:

```bash
docker compose build
docker compose up -d
```

Si estas en IntelliJ con XAMPP, reiniciar `ApiGatewayApplication` y el microservicio que cambiaste. Luego abrir:

```text
http://localhost:8080/swagger-ui/index.html?urls.primaryName=Videojuegos
```

### Swagger muestra curl pero no ejecuta

1. Verificar que `ApiGatewayApplication` este corriendo.
2. Verificar que el microservicio seleccionado este corriendo.
3. Abrir Swagger con `localhost`, no con una IP distinta.
4. Presionar `Try it out`, completar JSON y luego `Execute`.

### Login devuelve 401

Revisar correo y password:

```text
jesus@tiendajuegos.cl / cliente123
admin@tiendajuegos.cl / admin123
```

### MySQL local o XAMPP da problemas

Con Docker no se necesita XAMPP. El MySQL de Docker queda aislado y se expone en `3307`.

## 18. Frases cortas para responder bien

- "El Gateway es el unico punto de entrada."
- "Eureka permite descubrir microservicios por nombre."
- "Config Server centraliza configuracion externa."
- "Flyway versiona la base de datos."
- "En esta version no usamos Spring Security; el Gateway queda abierto y solo enruta."
- "OpenFeign permite que un microservicio consuma otro por nombre registrado en Eureka."
- "Docker Compose levanta todo el ecosistema con un solo comando."
- "Cada microservicio tiene una responsabilidad y su propia base de datos."

## 19. Checklist antes de presentar

```bash
docker compose ps
BASE_URL=http://localhost:8080 EUREKA_URL=http://localhost:8761 CONFIG_URL=http://localhost:8888 bash scripts/test-definitivo.sh
```

Abrir:

```text
http://localhost:8761
http://localhost:8080/swagger-ui/index.html
http://localhost:8082
```

Probar:

- Login.
- `GET /videojuegos`.
- Busqueda por precio.
- Carrito usuario 2.
- Pago.
- Error de plataforma invalida.
- Inventario bajo stock.

## 20. Pruebas unitarias para explicar

La pauta pide JUnit, Mockito, asserts y estructura clara. En este proyecto puedes explicar estos ejemplos:

- `VideoJuegoServiceTests`: valida plataformas permitidas, plataforma invalida y busqueda por precio.
- `UsuarioServiceTests`: valida correo duplicado, activo por defecto y desactivacion.
- `AuthenticationControllerValidationTests`: valida password ausente, password corta y password valida de 5 caracteres en `/auth/registro`.
- `AuthenticationServiceTests`: valida registro, password hasheada, login invalido y credencial desactivada.
- `PasswordHashServiceTests`: valida que la password no quede en texto plano y que el hash permita comparar correctamente.
- `CarritoServiceTests`: usa mocks de Feign para videojuegos, usuarios y resenas; valida subtotal, resumen y resena del usuario.
- `PagoServiceTests`: valida que el pago consulte carrito, cree transaccion aprobada y vacie carrito.
- `PedidoServiceTests`: valida usuario remoto y DTO con nombre/correo del usuario.
- `ResenaServiceTests`: valida usuario remoto y filtro por puntuacion.
- `InventarioServiceTests`: valida inventario duplicado, bajo stock y stock insuficiente.

Frase para defensa:

> Las pruebas unitarias no levantan todo Spring. Usan Mockito para simular repositorios y clientes Feign, por eso prueban la regla de negocio aislada. Las pruebas de contexto e integracion usan H2 para validar que cada microservicio arranca correctamente.

Comando para mostrarlas:

```bash
for module in eureka config-server api-gateway videojuegos usuarios authentication carrito pagos pedidos resenas inventario; do
  (cd "$module" && ./mvnw test) || exit 1
done
```

En Windows PowerShell:

```powershell
$modules = "eureka","config-server","api-gateway","videojuegos","usuarios","authentication","carrito","pagos","pedidos","resenas","inventario"
foreach ($module in $modules) {
  Push-Location $module
  .\mvnw.cmd test
  if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
  Pop-Location
}
```
