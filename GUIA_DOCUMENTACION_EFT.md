# Guia Para Documentar Los Microservicios

Esta guia sirve para armar un Word o PDF de respaldo para la defensa. No es necesario subirlo al repositorio si el profesor pidio solo link de GitHub; usalo como material de estudio.

## Estructura Recomendada

1. Portada
   - Proyecto: Tienda de Videojuegos.
   - Asignatura: Desarrollo FullStack I.
   - Integrantes.
   - Fecha.
   - Link GitHub.

2. Resumen ejecutivo
   - Explicar que es una tienda de videojuegos con microservicios.
   - Mencionar API Gateway, Eureka, Config Server, MySQL, Swagger, Docker y pruebas.

3. Arquitectura general

```text
Cliente / Postman / Swagger
        |
        v
API Gateway :8080
        |
        v
Eureka :8761 + Config Server :8888
        |
        v
Microservicios de negocio + MySQL
```

4. Tabla de microservicios

| Modulo | Responsabilidad | Rutas principales |
| --- | --- | --- |
| eureka | Registro y descubrimiento | `http://localhost:8761` |
| config-server | Configuracion central YAML | `/videojuegos/default` |
| api-gateway | Entrada unica y Swagger | `/swagger-ui/index.html` |
| videojuegos | Catalogo y busqueda por precio/plataforma | `/videojuegos` |
| usuarios | Datos personales y roles | `/usuarios` |
| authentication | Registro/login y credenciales | `/auth` |
| carrito | Carrito enriquecido con usuario, juego y resena | `/carrito` |
| pagos | Pagos y estados | `/pagos` |
| pedidos | Pedidos y reportes | `/pedidos` |
| resenas | Resenas y filtros | `/resenas` |
| inventario | Stock y bajo stock | `/inventario` |

5. Por cada microservicio documenta
   - Proposito.
   - Entidades principales.
   - DTOs relevantes.
   - Endpoints GET/POST/PUT/DELETE.
   - Reglas de negocio.
   - Excepciones personalizadas.
   - Pruebas que lo validan.

6. Swagger/OpenAPI
   - Captura de `http://localhost:8080/swagger-ui/index.html`.
   - Mostrar selector de APIs: Videojuegos, Usuarios, Authentication, Carrito, Pagos, Pedidos, Resenas, Inventario.
   - Para cada API documentar metodo HTTP, ruta, parametros, JSON de ejemplo y respuesta esperada.

7. Pruebas
   - Comando: `bash scripts/coverage-report.sh`.
   - Resultado: 145 tests, 0 failures, 0 errors.
   - Cobertura global: 90.96%.
   - Explicar Given-When-Then, Mockito y asserts.

8. Despliegue
   - Local: XAMPP/MySQL en `localhost:3306` + scripts `local-up`.
   - Docker: `docker compose up --build -d`.
   - Puertos:
     - Gateway 8080.
     - Eureka 8761.
     - Config Server 8888.
     - Apache/PHP 8081.
     - phpMyAdmin 8082.
     - MySQL Docker 3307 hacia 3306 interno.

## Plantilla Para Un Endpoint

```text
Nombre: Buscar videojuegos por precio
Metodo: GET
Ruta: /videojuegos/buscar?precioMin=10000&precioMax=16000
Servicio: videojuegos
Entrada: parametros precioMin y precioMax
Salida: lista de videojuegos filtrados
Regla: devuelve solo juegos dentro del rango indicado
Errores: parametros invalidos devuelven 400 si aplica
Prueba: VideoJuegoServiceBranchTests
```

## Evidencias Que Conviene Guardar

- Swagger abierto.
- Eureka con aplicaciones registradas.
- `docker compose ps`.
- Resultado de `scripts/test-definitivo.sh`.
- Resultado de JaCoCo global.
- Una prueba unitaria abierta en IntelliJ.
- Un ejemplo de error personalizado en Swagger.

## Frase Para Defender La Documentacion

> La documentacion combina Swagger como documentacion viva de la API, README como guia de ejecucion, Postman como coleccion REST y documentos de apoyo para explicar arquitectura, endpoints, pruebas y despliegue.
