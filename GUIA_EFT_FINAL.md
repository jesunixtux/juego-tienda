# Guia EFT Final Transversal - DSY1103

## 1. Lo mas importante de la pauta

El EFT vale 40% de la asignatura y se divide asi:

| Dimension | Peso |
| --- | ---: |
| Entrega grupal | 40% |
| Defensa individual | 60% |
| Total | 100% |

La defensa es individual. Aunque el proyecto este bueno, la nota depende de lo que cada estudiante demuestre: explicar, ejecutar, probar y modificar codigo sin ayuda externa durante la evaluacion.

La pauta indica que si no puedes explicar el proyecto, ejecutar la aplicacion o modificar codigo de forma funcional, se puede asumir baja participacion en el desarrollo.

## 2. Estado actual del proyecto contra la pauta

| Requisito EFT | Estado en este proyecto | Evidencia |
| --- | --- | --- |
| Minimo 10 microservicios | Cumple como ecosistema: 11 modulos Maven | `eureka`, `config-server`, `api-gateway`, `videojuegos`, `usuarios`, `authentication`, `carrito`, `pagos`, `pedidos`, `resenas`, `inventario` |
| Arquitectura CSR | Cumple en servicios de negocio | Paquetes `controller`, `service`, `repository`, `model`, `dto` |
| JPA + Hibernate | Cumple | Entidades `@Entity`, repositorios `JpaRepository` |
| Migraciones SQL/Flyway | Cumple en servicios de negocio | Carpetas `src/main/resources/db/migration` |
| Validaciones Bean Validation | Cumple | `@NotBlank`, `@Size`, `@Min`, `@Email`, etc. |
| Excepciones controladas | Cumple | Paquetes `exception`, `GlobalExceptionHandler` |
| Logs SLF4J | Cumple parcialmente/bien | Controllers y handlers registran eventos |
| Comunicacion entre microservicios | Cumple | OpenFeign entre carrito/videojuegos/usuarios/resenas, pagos/carrito/usuarios, pedidos/usuarios, etc. |
| API Gateway | Cumple | `api-gateway` centraliza rutas en puerto 8080 |
| YAML | Cumple | `application.yml` y `config-microservicios/*.yml` |
| Swagger/OpenAPI | Cumple | `http://localhost:8080/swagger-ui/index.html` |
| Docker/local | Cumple | `docker-compose.yml`, `Dockerfile`, scripts locales |
| Pruebas unitarias | Cumple funcionalmente | 45 tests pasan |
| Cobertura 80% | Riesgo | No hay JaCoCo configurado para demostrar porcentaje |
| GitHub/Trello | Depende de evidencia externa | Mostrar commits y tablero en defensa |

## 3. Prioridades reales para salvar la defensa

Los indicadores con mas peso en defensa son:

| Indicador defensa | Peso | Que debes demostrar |
| --- | ---: | --- |
| IE 3.1.2 Pruebas unitarias y nueva prueba en vivo | 12% | Explicar mocks/asserts y crear un test funcional |
| IE 2.1.4 Modificacion CRUD/regla/validacion en vivo | 10% | Cambiar codigo, compilar y probar por REST |
| IE 3.3.2 Ejecutar microservicios sin apoyo | 7% | Levantar local/Docker, revisar logs, puertos y Swagger |
| IE 1.3.3 Pruebas REST | 5% | Usar Swagger/Postman/curl y explicar HTTP status + JSON |
| IE 2.1.3 Modelado y CSR | 5% | Explicar entidades, capas y relaciones |
| IE 2.5.2 Aporte personal | 5% | Mostrar commits/tareas y explicar que hiciste |
| IE 2.2.2 Logica de negocio | 4% | Explicar services y reglas |
| IE 2.3.2 Logs/excepciones/codigos HTTP | 4% | Ajustar error controlado y explicar impacto |
| IE 2.4.2 Comunicacion remota | 4% | Explicar Feign, DTOs y datos remotos |
| IE 3.2.2 Swagger/OpenAPI | 4% | Recorrer rutas, modelos, parametros y respuestas |

Traduccion directa: para salvar, practica primero tests, modificacion en vivo, ejecucion y Swagger.

## 4. Guion de 15 minutos para defensa

### Minuto 0 a 2 - Presentacion del sistema

> Mi proyecto es una tienda de videojuegos con arquitectura de microservicios. Cada servicio tiene una responsabilidad separada: catalogo, usuarios, autenticacion funcional, carrito, pagos, pedidos, resenas e inventario. Ademas tenemos Eureka para descubrimiento, Config Server para configuracion centralizada y API Gateway como punto unico de entrada.

### Minuto 2 a 4 - Arquitectura

> El cliente consume todo por `localhost:8080` a traves del Gateway. El Gateway enruta por nombre de servicio usando Eureka. Cada microservicio mantiene su propia base de datos MySQL y usa Flyway para crear tablas y datos iniciales.

Abre:

```text
http://localhost:8761
http://localhost:8080/swagger-ui/index.html
```

### Minuto 4 a 6 - Microservicio elegido

Elige `videojuegos` o `authentication`, porque son los mas faciles de defender.

Para `videojuegos`:

> `VideoJuegoController` recibe las solicitudes REST. `VideoJuegoService` aplica reglas de negocio, por ejemplo normalizar plataforma y rechazar plataformas invalidas. `VideoJuegoRepository` accede a la base con JPA. El modelo `VideoJuego` representa la tabla.

Para `authentication`:

> `AuthenticationController` registra y valida login funcional. `AuthenticationService` crea usuario remoto mediante Feign, guarda credencial y valida password hasheada. No usamos Spring Security; los endpoints quedan abiertos para la evaluacion.

### Minuto 6 a 8 - Swagger

Abre:

```text
http://localhost:8080/swagger-ui/index.html?urls.primaryName=Videojuegos
```

Demuestra:

```text
GET /videojuegos
GET /videojuegos/buscar?precioMin=10000&precioMax=16000
POST /auth/registro con password "1234" para mostrar error 400
POST /auth/registro con password "12345" para mostrar caso valido
GET /carrito/usuario/2
```

Frase clave:

> Swagger esta centralizado en el Gateway. Cada microservicio expone su propio OpenAPI y el Gateway los muestra desde una sola UI. Puedo revisar rutas, parametros, modelos, codigos HTTP y ejemplos.

### Minuto 8 a 11 - Pruebas

Abre:

```text
authentication/src/test/java/cl/duoc/authentication/controller/AuthenticationControllerValidationTests.java
videojuegos/src/test/java/cl/duoc/videojuegos/service/VideoJuegoServiceTests.java
```

Frase clave:

> Uso Given, When, Then. En Given preparo datos y mocks. En When ejecuto el metodo o endpoint. En Then verifico con asserts, jsonPath o assertThatThrownBy. Mockito simula repositorios o servicios externos para probar la regla de negocio aislada.

Comando:

```bash
cd authentication
./mvnw test
```

### Minuto 11 a 14 - Modificacion en vivo

La modificacion mas segura es una validacion simple.

Ejemplo:

> "Cambie el minimo de password de 5 a 6 caracteres."

Pasos:

1. Abrir `authentication/src/main/java/cl/duoc/authentication/dto/RegistroRequest.java`.
2. Cambiar `@Size(min = 5)` por `@Size(min = 6)`.
3. Abrir `AuthenticationControllerValidationTests`.
4. Actualizar el test de password valida para usar `"123456"`.
5. Ejecutar:

```bash
cd authentication
./mvnw test
```

6. Probar por Swagger o curl.

### Minuto 14 a 15 - Cierre

> El sistema queda probado por JUnit/Mockito, documentado con Swagger/OpenAPI y ejecutable localmente con IntelliJ + XAMPP o con Docker Compose. El Gateway centraliza rutas, Eureka registra servicios y Config Server entrega YAML.

## 5. Como ejecutar sin Docker

Usar este modo si el profe pide IntelliJ y XAMPP.

1. Encender XAMPP MySQL en puerto `3306`.
2. Abrir la carpeta raiz `juego-tienda` en IntelliJ.
3. Ejecutar en orden:

```text
EurekaApplication
ConfigServerApplication
VideojuegosApplication
UsuariosApplication
AuthenticationApplication
CarritoApplication
PagosApplication
PedidosApplication
ResenasApplication
InventarioApplication
ApiGatewayApplication
```

4. Revisar:

```text
http://localhost:8761
http://localhost:8888/videojuegos/default
http://localhost:8080/swagger-ui/index.html
```

## 6. Como ejecutar con Docker

Comandos:

```bash
docker compose build
docker compose up -d
docker compose ps
```

URLs:

```text
Gateway: http://localhost:8080
Swagger: http://localhost:8080/swagger-ui/index.html
Eureka: http://localhost:8761
Config: http://localhost:8888/videojuegos/default
phpMyAdmin: http://localhost:8081
Apache/PHP: http://localhost:8082
```

Logs:

```bash
docker compose logs -f api-gateway
docker compose logs -f videojuegos
docker compose logs -f mysql
```

Detener:

```bash
docker compose down
```

## 7. Comandos de pruebas

Todas las pruebas:

```bash
for service in eureka config-server videojuegos usuarios authentication carrito pagos pedidos resenas inventario api-gateway; do
  echo "== $service =="
  (cd "$service" && ./mvnw test)
done
```

Prueba funcional completa con servicios levantados:

```bash
bash scripts/test-definitivo.sh
```

Resultado verificado el 2026-06-29:

```text
45 tests
0 failures
0 errors
0 skipped
```

## 8. Prueba en vivo lista para memorizar

Si te piden crear una prueba, usa esta idea en `videojuegos`.

Archivo:

```text
videojuegos/src/test/java/cl/duoc/videojuegos/service/VideoJuegoServiceTests.java
```

Ejemplo:

```java
@Test
void crearRechazaPlataformaNoPermitida() {
    VideoJuego videoJuego = new VideoJuego();
    videoJuego.setPlataforma("Game Boy");

    assertThatThrownBy(() -> videoJuegoService.crear(videoJuego))
            .isInstanceOf(PlataformaException.class)
            .hasMessageContaining("Plataforma no valida");
}
```

Explicacion:

> Esta prueba valida una regla del dominio: solo aceptamos plataformas permitidas. No necesito base de datos porque estoy probando la logica del service. Si la plataforma no pertenece al conjunto permitido, el service lanza una excepcion personalizada.

## 9. Modificaciones en vivo seguras

### Opcion A - Cambiar minimo de password

Archivos:

```text
authentication/src/main/java/cl/duoc/authentication/dto/RegistroRequest.java
authentication/src/main/java/cl/duoc/authentication/dto/LoginRequest.java
authentication/src/test/java/cl/duoc/authentication/controller/AuthenticationControllerValidationTests.java
```

Que decir:

> Esta modificacion afecta la validacion de entrada. La regla se aplica antes de entrar al servicio gracias a Bean Validation y `@Valid`.

### Opcion B - Agregar plataforma permitida

Archivos:

```text
videojuegos/src/main/java/cl/duoc/videojuegos/service/VideoJuegoService.java
videojuegos/src/test/java/cl/duoc/videojuegos/service/VideoJuegoServiceTests.java
```

Que decir:

> Esta modificacion afecta una regla de negocio del catalogo. El service normaliza la plataforma y rechaza valores no permitidos.

### Opcion C - Cambiar mensaje de error

Archivos:

```text
*/exception/GlobalExceptionHandler.java
```

Que decir:

> El manejo centralizado permite responder JSON consistente con codigo HTTP correcto sin repetir try/catch en todos los controllers.

## 10. Respuestas para preguntas probables

### Por que quitaron Spring Security?

> Porque no era parte del alcance que el docente nos enseno para este EFT. Dejamos el microservicio `authentication` para registro/login funcional y passwords hasheadas, pero el API Gateway no bloquea rutas ni valida tokens. Asi Swagger, Postman y la defensa se enfocan en microservicios, CRUD, reglas, pruebas, YAML y despliegue.

### Que hace Eureka?

> Eureka permite descubrimiento de servicios. Los microservicios se registran y el Gateway los encuentra por nombre, por ejemplo `lb://videojuegos`.

### Que hace Config Server?

> Centraliza configuracion YAML. Los microservicios leen configuraciones desde `config-microservicios`, como puertos, datasource, rutas y Eureka.

### Que hace Flyway?

> Ejecuta migraciones SQL versionadas al iniciar cada microservicio. Crea tablas y datos iniciales sin hacerlo manualmente.

### Que es Feign?

> Es un cliente REST declarativo. Permite que un microservicio consuma otro usando una interfaz Java. Por ejemplo, carrito consulta videojuegos y usuarios para enriquecer la respuesta.

### Que es DTO?

> Es un objeto para transportar datos entre capas o servicios. Evita exponer directamente entidades internas y permite controlar lo que entra o sale de la API.

### Que es Mock?

> Es una simulacion de una dependencia. En pruebas unitarias uso mocks para probar la logica del service sin depender de base de datos o servicios remotos.

### Que es Assert?

> Es la verificacion del resultado esperado. Por ejemplo, `assertThat(resultado).isEqualTo(...)` o `assertThatThrownBy(...)`.

### Que es ResponseEntity?

> Permite controlar el codigo HTTP y el cuerpo JSON que devuelve el endpoint.

## 11. Riesgos que debes controlar antes del EFT

### Riesgo 1 - Cobertura 80%

La pauta pide pruebas con al menos 80% de cobertura. El proyecto tiene pruebas y pasan, pero no tiene JaCoCo configurado para demostrar porcentaje.

No digas "tenemos 80%" si no tienes reporte. Di:

> Tenemos pruebas unitarias y de contexto ejecutadas correctamente. Si el docente exige reporte porcentual, se debe generar con JaCoCo.

### Riesgo 2 - Cambios despues de entrega

La pauta dice que cambios despues de la fecha pueden generar 1.0. Cuando entreguen oficialmente, no hagan commits posteriores antes de la defensa sin autorizacion del docente.

### Riesgo 3 - Trello/GitHub

Debes poder mostrar:

- Tus commits.
- Tareas asignadas.
- Que parte hiciste.
- Que problemas resolviste.
- Que pruebas agregaste.

### Riesgo 4 - No depender de Codex/IA durante defensa

La pauta dice que no se permite ayuda externa ni IA durante la sesion. Lleva esto estudiado y practicado antes.

## 12. Checklist final antes de entrar

- Proyecto abre en IntelliJ.
- XAMPP MySQL o Docker Desktop funcionando.
- `./mvnw test` funciona en al menos `authentication` y `videojuegos`.
- `docker compose config -q` no muestra errores.
- Swagger abre en `localhost:8080`.
- Eureka abre en `localhost:8761`.
- Puedes explicar `VideoJuegoServiceTests`.
- Puedes crear una prueba simple en vivo.
- Puedes cambiar una validacion y probarla.
- Puedes explicar por que no se usa Spring Security.
- Tienes GitHub/Trello listos para mostrar aporte personal.

