# Plan de salvada para la defensa tecnica

## 1. Cuanto necesitas

Tu nota de presentacion aparece como 3.7.

Si la nota final se calcula como `70% presentacion + 30% examen`, necesitas:

```text
4.0 = 3.7 * 0.70 + examen * 0.30
examen = 4.7
```

Tabla rapida:

| Formula usada | Nota minima en examen para llegar a 4.0 |
| --- | ---: |
| 70% presentacion / 30% examen | 4.7 |
| 60% presentacion / 40% examen | 4.5 |
| 50% presentacion / 50% examen | 4.3 |

Meta realista: apuntar a 5.0 o mas para tener margen.

## 2. Donde se perdieron puntos

Segun la rubrica, el proyecto esta relativamente fuerte en explicacion general, YAML y despliegue, pero faltan tres puntos criticos:

| Indicador | Problema visto | Salvacion |
| --- | --- | --- |
| IE 3.1.2 | Explicacion debil de pruebas unitarias | Saber explicar Given, When, Then, mocks, asserts y regla de negocio |
| IE 3.1.3 | No se logro crear una prueba en vivo | Memorizar una plantilla corta de JUnit + Mockito |
| IE 3.2.2 | No se explico Swagger/OpenAPI | Saber abrir Swagger, elegir microservicio, explicar ruta, parametros, request y response |
| IE 3.3.7 | Dificultad ejecutando local/remoto | Repetir el orden de arranque y tener comandos de verificacion |

## 3. Frase base para defender el proyecto

> Este proyecto es una tienda de videojuegos dividida en microservicios. Cada servicio tiene una responsabilidad clara: videojuegos maneja el catalogo, usuarios maneja datos de usuarios, authentication registra y valida cuentas, carrito agrupa compras temporales, pagos registra pagos, pedidos genera compras, resenas maneja comentarios e inventario controla stock. El API Gateway centraliza las rutas y Swagger, Eureka registra los servicios y Config Server entrega configuracion YAML. La base de datos es MySQL y se puede usar local con XAMPP o con Docker.

## 4. Como explicar una prueba unitaria

La estructura que debes decir siempre:

```text
Given: preparo los datos y configuro los mocks.
When: ejecuto el metodo que estoy probando.
Then: verifico el resultado esperado con asserts y confirmo llamadas con verify.
```

Ejemplo real del proyecto:

Archivo: `authentication/src/test/java/cl/duoc/authentication/controller/AuthenticationControllerValidationTests.java`

Prueba: `registrarRechazaPasswordCortaConErrorPersonalizado`

Explicacion oral:

> Esta prueba valida una regla de negocio de creacion de cuentas: la contrasena debe tener minimo 5 caracteres. Uso MockMvc porque estoy probando el controlador sin levantar todo el servidor. En el Given envio un JSON con password "1234". En el When hago un POST a `/auth/registro`. En el Then espero HTTP 400 y reviso que el mensaje sea "Contrasena invalida". Asi compruebo que la validacion y el error personalizado funcionan antes de llegar al servicio.

Otra prueba real:

Archivo: `videojuegos/src/test/java/cl/duoc/videojuegos/service/VideoJuegoServiceTests.java`

Prueba: `crearRechazaPlataformaNoPermitida`

Explicacion oral:

> Esta prueba valida que no se puedan crear juegos con plataformas fuera de las permitidas. En el Given creo un VideoJuego con plataforma "Game Boy". En el When llamo a `videoJuegoService.crear`. En el Then espero una `PlataformaException` y reviso que el mensaje incluya las plataformas validas: PS4, XBOX y PC. Esta prueba protege una regla del dominio.

## 5. Plantilla para crear una prueba en vivo

Si el profesor pide "agrega una prueba unitaria", elige una prueba simple de servicio.

Plantilla para servicio con Mockito:

```java
@ExtendWith(MockitoExtension.class)
class NombreServiceTests {

    @Mock
    private NombreRepository repository;

    @InjectMocks
    private NombreService service;

    @Test
    void metodoCasoEsperado() {
        // Given
        Entidad entidad = new Entidad();
        entidad.setNombre("Dato demo");

        when(repository.save(entidad)).thenReturn(entidad);

        // When
        Entidad resultado = service.crear(entidad);

        // Then
        assertThat(resultado.getNombre()).isEqualTo("Dato demo");
        verify(repository).save(entidad);
    }
}
```

Plantilla para excepcion:

```java
@Test
void crearRechazaDatoInvalido() {
    Entidad entidad = new Entidad();
    entidad.setCampo("valor invalido");

    assertThatThrownBy(() -> service.crear(entidad))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("mensaje esperado");
}
```

Plantilla para controlador con MockMvc:

```java
@WebMvcTest(NombreController.class)
class NombreControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NombreService service;

    @Test
    void crearDevuelveCreated() throws Exception {
        when(service.crear(any())).thenReturn(new DtoRespuesta(1L, "OK"));

        mockMvc.perform(post("/ruta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "campo": "valor"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }
}
```

Frase clave:

> MockMvc prueba la capa web. Mockito simula dependencias. AssertJ o jsonPath verifican el resultado. verify confirma que el repositorio o servicio fue llamado como corresponde.

## 6. Swagger/OpenAPI: como explicarlo

URL principal:

```text
http://localhost:8080/swagger-ui/index.html?urls.primaryName=Videojuegos
```

Que debes decir:

> Swagger documenta las rutas REST. En este proyecto esta centralizado en el API Gateway. Eso permite ver todos los microservicios desde una sola pantalla. Cada servicio expone su OpenAPI en `/servicio/v3/api-docs`, y el gateway muestra esas URLs en el selector de Swagger.

Endpoints OpenAPI por gateway:

```text
http://localhost:8080/videojuegos/v3/api-docs
http://localhost:8080/usuarios/v3/api-docs
http://localhost:8080/auth/v3/api-docs
http://localhost:8080/carrito/v3/api-docs
http://localhost:8080/pagos/v3/api-docs
http://localhost:8080/pedidos/v3/api-docs
http://localhost:8080/resenas/v3/api-docs
http://localhost:8080/inventario/v3/api-docs
```

Demo recomendada en Swagger:

1. Abrir `Authentication`.
2. Ejecutar `POST /auth/registro`.
3. Probar password corta `"1234"`.
4. Mostrar que responde HTTP 400 con "Contrasena invalida".
5. Cambiar password a `"12345"`.
6. Mostrar que crea la cuenta.
7. Ir a `Videojuegos`.
8. Probar busqueda por precio: `/videojuegos/buscar?precioMin=10000&precioMax=16000`.

Si Swagger no ejecuta y solo entrega el curl:

> Swagger esta generando correctamente la peticion. Si el navegador bloquea la ejecucion por configuracion local, copio el curl y lo ejecuto en terminal o Postman. La documentacion sigue siendo valida porque muestra metodo, ruta, parametros, cuerpo y respuesta esperada.

## 7. Como ejecutar local con IntelliJ y XAMPP

Orden recomendado:

1. Abrir XAMPP.
2. Encender MySQL.
3. Confirmar MySQL en `localhost:3306`.
4. Abrir el proyecto en IntelliJ desde la carpeta raiz `juego-tienda`.
5. Ejecutar `EurekaApplication`.
6. Ejecutar `ConfigServerApplication`.
7. Ejecutar microservicios:
   - `VideojuegosApplication`
   - `UsuariosApplication`
   - `AuthenticationApplication`
   - `CarritoApplication`
   - `PagosApplication`
   - `PedidosApplication`
   - `ResenasApplication`
   - `InventarioApplication`
8. Ejecutar `ApiGatewayApplication`.
9. Abrir:

```text
http://localhost:8761
http://localhost:8888/videojuegos/default
http://localhost:8080/swagger-ui/index.html?urls.primaryName=Videojuegos
```

Frase para defender:

> Localmente uso XAMPP solo para MySQL. Los microservicios corren desde IntelliJ. Docker es una alternativa para levantar todo el ecosistema con la misma configuracion, pero el proyecto quedo hibrido: sirve local y tambien dockerizado.

## 8. Como ejecutar con Docker

Comando principal:

```bash
docker compose up --build
```

Comandos utiles:

```bash
docker compose ps
docker compose logs -f api-gateway
docker compose down
```

URLs esperadas:

```text
Gateway:    http://localhost:8080
Swagger:    http://localhost:8080/swagger-ui/index.html?urls.primaryName=Videojuegos
Eureka:     http://localhost:8761
Config:     http://localhost:8888/videojuegos/default
Apache/PHP: http://localhost:8081
phpMyAdmin: http://localhost:8082
```

Frase para defender:

> Docker levanta servicios aislados en contenedores. MySQL queda dentro de Docker, los microservicios se construyen con Maven y Java, y el gateway expone la entrada principal por el puerto 8080. Esto evita depender de la configuracion exacta del computador del laboratorio.

## 9. Como moverlo a otra PC

Opcion Docker:

1. Instalar Docker Desktop.
2. Instalar Git.
3. Clonar o copiar el proyecto.
4. Entrar a la carpeta raiz.
5. Ejecutar:

```bash
docker compose up --build
```

Opcion IntelliJ + XAMPP:

1. Instalar Java 25 o Java compatible con el proyecto.
2. Instalar IntelliJ.
3. Instalar XAMPP.
4. Encender MySQL en XAMPP.
5. Abrir el proyecto en IntelliJ.
6. Ejecutar en orden: Eureka, Config Server, servicios, Gateway.

## 10. Pruebas que debes ejecutar antes de presentar

Pruebas unitarias por servicio:

```bash
for service in eureka config-server videojuegos usuarios authentication carrito pagos pedidos resenas inventario api-gateway; do
  echo "== $service =="
  (cd "$service" && ./mvnw test)
done
```

Prueba funcional completa, con servicios ya levantados:

```bash
bash scripts/test-definitivo.sh
```

Que valida ese script:

- Eureka registra servicios.
- Config Server entrega YAML.
- Swagger UI responde.
- OpenAPI responde para todos los microservicios.
- Catalogo de videojuegos responde.
- Busqueda por plataforma y precio funciona.
- Login correcto e incorrecto.
- Password corta responde error personalizado.
- Carrito muestra nombre de usuario, nombre de juego y resena.
- Pagos, pedidos, resenas e inventario responden.
- Validaciones y excepciones personalizadas responden controlado.

## 11. Preguntas probables y respuestas cortas

### Que es un microservicio en tu proyecto?

> Es una parte independiente del backend con una responsabilidad concreta. Por ejemplo, videojuegos no maneja pagos, solo catalogo. Pagos no maneja usuarios completos, solo registra informacion de pago y usa IDs o DTOs para relacionarse con otros datos.

### Para que sirve el API Gateway?

> Sirve como entrada unica. En vez de llamar cada puerto directamente, consumo todo desde `localhost:8080`. Tambien centraliza Swagger y simplifica las rutas para Postman o navegador.

### Para que sirve Eureka?

> Eureka registra los servicios activos. Asi el gateway puede encontrar los microservicios por nombre y no depender siempre de una IP fija.

### Para que sirve Config Server?

> Centraliza la configuracion YAML. Permite separar configuracion de codigo y mantener perfiles o valores distintos para local y Docker.

### Que diferencia hay entre prueba unitaria e integracion?

> La unitaria prueba una clase aislada usando mocks. La de integracion prueba mas piezas juntas, por ejemplo contexto Spring, controlador o endpoints reales.

### Que es Mockito?

> Mockito permite simular dependencias. Por ejemplo, simulo el repository para probar la logica del service sin conectarme a la base de datos.

### Que es MockMvc?

> MockMvc permite probar controladores Spring haciendo peticiones HTTP simuladas, sin levantar un servidor real.

### Que es un assert?

> Es la verificacion del resultado esperado. Si el resultado no coincide, la prueba falla.

### Que validacion agregaste a cuentas?

> La creacion de cuentas exige contrasena obligatoria y minimo 5 caracteres. Si falta o es corta, responde HTTP 400 con el mensaje personalizado "Contrasena invalida".

### Que pasa si intento crear un juego con plataforma invalida?

> El servicio rechaza la creacion con una excepcion personalizada. Solo se aceptan PS4, XBOX y PC.

### Como explicas la busqueda por precio?

> El endpoint recibe `precioMin` y `precioMax`. El servicio decide si busca por rango o por limites disponibles, y el repository ejecuta la consulta correspondiente.

## 12. Si te piden modificar algo en vivo

Ruta mas segura:

1. Modificar DTO o service, no tocar todo el ecosistema.
2. Crear una prueba unitaria que falle primero si alcanza el tiempo.
3. Implementar el cambio.
4. Ejecutar solo el test del microservicio.

Comando por microservicio:

```bash
cd authentication
./mvnw test
```

Ejemplo de cambio facil:

> "Agrega validacion para que password tenga minimo 8 caracteres".

Pasos:

1. Abrir `RegistroRequest`.
2. Cambiar `@Size(min = 5)` por `@Size(min = 8)`.
3. Cambiar el test que espera password corto.
4. Ejecutar `./mvnw test`.

## 13. Checklist de 10 minutos antes de defensa

- XAMPP MySQL encendido si vas local.
- Docker Desktop abierto si vas Docker.
- Swagger abre en `localhost:8080`.
- Eureka abre en `localhost:8761`.
- `bash scripts/test-definitivo.sh` pasa.
- Tienes abierto el test `AuthenticationControllerValidationTests`.
- Tienes abierto el test `VideoJuegoServiceTests`.
- Tienes preparado Postman o terminal para pegar curl.
- Sabes explicar Given, When, Then.
- Sabes decir por que el gateway centraliza rutas y Swagger.

## 14. Resumen para memorizar

> Para salvar, debo demostrar tres cosas: se ejecutar el proyecto, se usar Swagger y se crear o explicar pruebas. En pruebas siempre digo Given, When, Then. En Swagger muestro metodo, ruta, parametros, request y response. En ejecucion explico local con XAMPP y Docker con docker compose. Si me piden una prueba en vivo, hago una de service con Mockito porque es rapida y segura.
