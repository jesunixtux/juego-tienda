# Guia De Funcionamiento Del Codigo

Esta guia es para estudiar el proyecto y responder preguntas tecnicas sin quedar en blanco.

## Flujo General De Una Peticion

1. El usuario entra por Swagger, Postman o navegador.
2. La peticion llega al API Gateway en `localhost:8080`.
3. El Gateway revisa la ruta, por ejemplo `/videojuegos`.
4. El Gateway consulta Eureka para encontrar una instancia del microservicio.
5. El microservicio recibe la peticion en su Controller.
6. El Controller valida el JSON con `@Valid` y llama al Service.
7. El Service aplica reglas de negocio.
8. El Repository consulta MySQL con JPA.
9. El resultado vuelve como DTO o entidad controlada.
10. Si ocurre error, `GlobalExceptionHandler` responde JSON con codigo HTTP correcto.

## Capas Que Debes Saber Explicar

| Capa | Que hace | Ejemplo |
| --- | --- | --- |
| Controller | Recibe HTTP y devuelve ResponseEntity | `VideoJuegoController` |
| DTO | Transporta datos de entrada/salida | `RegistroRequest` |
| Service | Reglas de negocio | valida plataforma o password |
| Repository | Acceso a BD con JPA | `JpaRepository` |
| Model/Entity | Tabla de base de datos | `VideoJuego`, `Usuario` |
| Exception | Errores de dominio | `PlataformaException` |
| Feign Client | Consumo remoto entre servicios | carrito consulta usuarios/videojuegos |

## Microservicios En Una Frase

- `eureka`: registra servicios para que el Gateway y Feign los encuentren por nombre.
- `config-server`: entrega YAML centralizado desde `config-microservicios`.
- `api-gateway`: punto unico de entrada, rutas, CORS y Swagger centralizado.
- `videojuegos`: catalogo, plataformas permitidas y busqueda por precio.
- `usuarios`: datos personales, correos unicos, roles y estado activo.
- `authentication`: registro/login con password obligatoria y hash.
- `carrito`: items por usuario, subtotal y datos enriquecidos.
- `pagos`: pagos del carrito, estados y datos de usuario.
- `pedidos`: pedidos y reportes por usuario, fecha y precio.
- `resenas`: comentarios, puntuacion y datos de usuario.
- `inventario`: stock, movimientos y bajo stock.

## Por Que No Hay Spring Security

> Lo quitamos porque no era parte del alcance ensenado para esta evaluacion y generaba bloqueo al probar Swagger. El microservicio `authentication` sigue existiendo para registro/login y password hasheada, pero el Gateway queda abierto para concentrar la defensa en microservicios, CRUD, DTOs, validaciones, pruebas, YAML y Docker.

## Como Explicar Una Prueba Unitaria

```text
Given: preparo datos y mocks.
When: ejecuto el metodo que quiero probar.
Then: verifico resultado, excepcion o llamada esperada.
```

Ejemplo:

> Esta prueba valida que una plataforma invalida no se guarde. Creo un VideoJuego con plataforma "Game Boy", ejecuto `crear`, y espero `PlataformaException`. Asi pruebo la regla de negocio sin depender de Swagger ni de MySQL.

## Preguntas Probables Del Profesor

### Que es un microservicio?

Es una parte independiente del backend con una responsabilidad clara. Por ejemplo, `videojuegos` no procesa pagos; solo administra catalogo.

### Que hace Eureka?

Permite descubrimiento. Los servicios se registran y otros componentes los encuentran por nombre, como `lb://videojuegos`.

### Que hace Config Server?

Centraliza la configuracion YAML. Asi cada microservicio lee puertos, datasource, Eureka y rutas desde `config-microservicios`.

### Que hace Flyway?

Ejecuta migraciones SQL versionadas al iniciar. Crea tablas y datos demo sin hacerlo manualmente.

### Que es Feign?

Es un cliente HTTP declarativo. Permite que un servicio llame a otro con una interfaz Java.

### Que es un DTO?

Es un objeto de transporte. Evita exponer datos internos y permite controlar lo que entra y sale de la API.

### Como se valida una password?

En `authentication`, los DTO exigen password obligatoria con minimo 5 caracteres. El Service guarda hash con sal y compara el hash al hacer login.

### Que pasa si una regla falla?

Se lanza una excepcion personalizada y `GlobalExceptionHandler` responde JSON con HTTP 400, 404, 401, 409 o 502 segun el caso.

### Como se ejecuta en otra PC?

1. Instalar Java 25 y Docker Desktop.
2. Clonar GitHub.
3. Abrir terminal en la raiz.
4. Ejecutar `docker compose up --build -d`.
5. Esperar 90 segundos.
6. Abrir Swagger en `http://localhost:8080/swagger-ui/index.html`.

## Modificaciones En Vivo Seguras

### Cambiar minimo de password

Archivos:
- `authentication/src/main/java/cl/duoc/authentication/dto/RegistroRequest.java`
- `authentication/src/test/java/cl/duoc/authentication/controller/AuthenticationControllerValidationTests.java`

Pasos:
1. Cambiar `@Size(min = 5)` a `@Size(min = 6)`.
2. Ajustar test valido con password `"123456"`.
3. Ejecutar `cd authentication && ./mvnw test`.

### Agregar plataforma nueva

Archivo:
- `videojuegos/src/main/java/cl/duoc/videojuegos/service/VideoJuegoService.java`

Pasos:
1. Agregar plataforma a la lista permitida.
2. Crear test con esa plataforma.
3. Ejecutar `cd videojuegos && ./mvnw test`.

### Agregar filtro por precio

Archivos:
- Controller de `videojuegos`.
- Service de `videojuegos`.
- Repository si se requiere query.

Explicacion:

> El Controller recibe los parametros, el Service decide la regla y el Repository consulta la base. Luego agrego una prueba para asegurar que el filtro no rompa busquedas existentes.

## Comandos Que Debes Memorizar

```bash
docker compose up --build -d
docker compose ps
bash scripts/test-definitivo.sh
bash scripts/coverage-report.sh
docker compose down
```

Windows PowerShell:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\local-up.ps1
.\scripts\coverage-report.ps1
docker compose up --build -d
```

## Respuesta Corta Si Algo Falla En Defensa

> Primero reviso si Docker Desktop o XAMPP estan activos. Luego verifico puertos, `docker compose ps`, Eureka, Config Server y logs del servicio. Si Swagger carga pero una ruta devuelve 503, espero el registro en Eureka o reinicio solo el Gateway.
