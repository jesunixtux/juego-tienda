# Solucion de problemas comunes

Esta guia sirve para reparar rapidamente los fallos mas comunes del proyecto **Juego Tienda de Videojuegos**.

El proyecto usa Docker Compose, Java 25, Maven, Spring Boot, Spring Cloud, Eureka, Config Server, API Gateway, MySQL, phpMyAdmin y Apache con PHP.

---

## 1. Docker no conecta al motor

### Error comun

```text
failed to connect to the docker API at npipe:////./pipe/dockerDesktopLinuxEngine
El sistema no puede encontrar el archivo especificado.
```

### Causa

Docker Desktop esta cerrado, el motor Linux no inicio correctamente o Docker esta apuntando a un contexto incorrecto.

### Solucion rapida

Abrir Docker Desktop y esperar a que indique que esta funcionando.

Luego ejecutar:

```powershell
docker version
```

Debe aparecer informacion de `Client` y `Server`.

Si no aparece `Server`, reiniciar Docker Desktop.

Revisar el contexto activo:

```powershell
docker context ls
```

Usar el contexto de Docker Desktop Linux:

```powershell
docker context use desktop-linux
```

Reintentar:

```powershell
docker compose build
```

Si sigue fallando:

```powershell
wsl --shutdown
```

Despues cerrar Docker Desktop, abrirlo otra vez y repetir el build.

---

## 2. Maven falla descargando dependencias durante Docker build

### Error comun

```text
DependencyResolutionException
Could not resolve dependencies
Could not transfer artifact
Premature end of Content-Length delimited message body
```

Tambien puede aparecer asi:

```text
target videojuegos: failed to solve: process "/bin/sh -c ... mvn package ..." did not complete successfully: exit code: 1
```

### Causa

Maven estaba descargando dependencias desde Maven Central, pero la descarga se corto o quedo incompleta. Esto no significa necesariamente que el codigo este malo. Muchas veces es internet inestable, proxy, firewall, red saturada o cache corrupta de Docker/Maven.

### Solucion rapida

Primero limpiar contenedores detenidos y cache de build:

```powershell
docker compose down
docker builder prune -af
docker system prune -f
```

Preparar dependencias Maven para Docker:

```powershell
.\scripts\preparar-dependencias-docker.ps1
```

Compilar primero el microservicio que fallo. Por ejemplo, si fallo `videojuegos`:

```powershell
docker compose build --no-cache --progress=plain videojuegos
```

Si pasa correctamente, compilar todo:

```powershell
docker compose build --progress=plain
```

Luego levantar el stack:

```powershell
docker compose up -d
```

### Como confirmar que se arreglo

El build correcto muestra algo parecido a:

```text
BUILD SUCCESS
Image juego-tienda/videojuegos:local Built
```

---

## 3. Maven falla en modo offline

### Error comun

```text
Cannot access central in offline mode
```

### Causa

El `Dockerfile` intenta usar primero las dependencias locales en modo offline para que el build sea mas rapido y no dependa tanto de internet. Si esas dependencias no estan descargadas, el intento offline falla y luego Maven intenta descargar online.

### Solucion rapida

Preparar dependencias:

```powershell
.\scripts\preparar-dependencias-docker.ps1
```

Luego reconstruir:

```powershell
docker compose build --progress=plain
```

---

## 4. El build falla, pero no se ve el error completo

### Problema

Docker muestra muchas lineas y al final solo se ve:

```text
failed to solve: process did not complete successfully
```

### Solucion rapida

Usar salida completa:

```powershell
docker compose build --progress=plain
```

Para revisar solo un servicio:

```powershell
docker compose build --progress=plain videojuegos
```

Ejemplos:

```powershell
docker compose build --progress=plain usuarios
docker compose build --progress=plain authentication
docker compose build --progress=plain api-gateway
```

---

## 5. Puerto ocupado

### Error comun

```text
port is already allocated
Bind for 0.0.0.0:3307 failed
```

### Causa

Otro programa esta usando el puerto configurado. Puede ser MySQL local, XAMPP, Laragon u otro contenedor.

### Solucion rapida

Crear `.env` desde el ejemplo:

```powershell
copy .env.example .env
```

Editar `.env` y cambiar el puerto conflictivo.

Ejemplo para MySQL:

```env
MYSQL_HOST_PORT=3308
```

Ejemplo para phpMyAdmin:

```env
PHPMYADMIN_HOST_PORT=8083
```

Ejemplo para Apache PHP:

```env
APACHE_PHP_HOST_PORT=8084
```

Reiniciar:

```powershell
docker compose down
docker compose up -d
```

---

## 6. MySQL no inicia o la base de datos queda rara

### Causa comun

El volumen de MySQL quedo con datos antiguos, migraciones Flyway anteriores o una configuracion incompatible.

### Solucion sin borrar datos

```powershell
docker compose restart mysql
```

Ver logs:

```powershell
docker compose logs -f mysql
```

### Solucion borrando la base de datos Docker

Advertencia: esto borra los datos del volumen Docker de MySQL.

```powershell
docker compose down -v
docker compose up -d
```

---

## 7. Eureka no muestra los servicios

### URL

```text
http://localhost:8761
```

### Causa comun

Los microservicios aun estan iniciando o no lograron registrarse en Eureka.

### Solucion rapida

Revisar contenedores:

```powershell
docker compose ps
```

Ver logs de Eureka:

```powershell
docker compose logs -f eureka
```

Ver logs del microservicio que no aparece:

```powershell
docker compose logs -f videojuegos
```

Reiniciar:

```powershell
docker compose restart
```

---

## 8. Config Server no entrega configuracion

### URL de prueba

```text
http://localhost:8888/videojuegos/default
```

### Solucion rapida

Revisar que exista la carpeta:

```powershell
ls config-microservicios
```

Ver logs:

```powershell
docker compose logs -f config-server
```

Reiniciar:

```powershell
docker compose restart config-server
```

---

## 9. API Gateway no responde

### URL

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

### Solucion rapida

Ver logs:

```powershell
docker compose logs -f api-gateway
```

Reiniciar servicios base:

```powershell
docker compose restart eureka config-server
```

Reiniciar gateway:

```powershell
docker compose restart api-gateway
```

Si sigue igual:

```powershell
docker compose down
docker compose up -d
```

---

## 10. Swagger o Postman pide autorizacion

### Sintoma

Swagger muestra un candado, Postman intenta enviar un header de autorizacion o parece que una ruta pide credenciales.

### Causa

La coleccion, el navegador o una version antigua del proyecto puede tener configuracion de autorizacion guardada.

### Solucion rapida

Actualizar la pagina y limpiar autorizacion en Swagger/Postman. En esta version el API Gateway no usa Spring Security y no exige token.

```powershell
docker compose down
docker compose up -d --build
```

Si estas en IntelliJ, reinicia primero `api-gateway` y luego vuelve a abrir:

```powershell
http://localhost:8080/swagger-ui/index.html
```

---

## 11. Comandos de diagnostico rapido

Ver estado:

```powershell
docker compose ps
```

Ver logs de todo:

```powershell
docker compose logs -f
```

Ver logs de un servicio:

```powershell
docker compose logs -f videojuegos
docker compose logs -f usuarios
docker compose logs -f authentication
docker compose logs -f carrito
docker compose logs -f pagos
docker compose logs -f pedidos
docker compose logs -f resenas
docker compose logs -f inventario
docker compose logs -f api-gateway
docker compose logs -f eureka
docker compose logs -f config-server
docker compose logs -f mysql
```

Reconstruir un servicio:

```powershell
docker compose build --progress=plain videojuegos
```

Reconstruir todo:

```powershell
docker compose build --progress=plain
```

Reconstruir sin cache:

```powershell
docker compose build --no-cache --progress=plain
```

Reiniciar todo:

```powershell
docker compose restart
```

Bajar todo:

```powershell
docker compose down
```

Subir todo:

```powershell
docker compose up -d
```

Borrar base de datos Docker:

```powershell
docker compose down -v
```

---

## 12. Orden recomendado para levantar desde cero

```powershell
copy .env.example .env
.\scripts\preparar-dependencias-docker.ps1
docker compose build --progress=plain
docker compose up -d
docker compose ps
```

URLs principales:

```text
Swagger / API Gateway:
http://localhost:8080/swagger-ui/index.html

Apache PHP:
http://localhost:8081

phpMyAdmin:
http://localhost:8082

Eureka:
http://localhost:8761

Config Server:
http://localhost:8888/videojuegos/default
```

---

## 13. Regla de oro

Si el error dice:

```text
failed to connect to the docker API
```

El problema es Docker Desktop.

Si el error dice:

```text
DependencyResolutionException
Could not transfer artifact
```

El problema es Maven descargando dependencias.

Si el error dice:

```text
port is already allocated
```

El problema es un puerto ocupado.

Si un microservicio no responde, mirar sus logs:

```powershell
docker compose logs -f nombre-del-servicio
```

No adivinar: primero logs, luego reparar.
