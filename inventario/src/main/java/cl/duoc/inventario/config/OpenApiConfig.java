package cl.duoc.inventario.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI inventarioOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tienda Videojuegos - Inventario")
                        .version("1.0.0")
                        .description("API para controlar stock por videojuego, consultar bajo stock y registrar entradas o salidas de inventario.")
                        .license(new License().name("Proyecto academico Duoc UC")))
                .addServersItem(new Server()
                        .url("/")
                        .description("API Gateway actual"));
    }
    @Bean
    OperationCustomizer inventarioResponsesCustomizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            operation.getResponses().addApiResponse("400", new ApiResponse()
                    .description("Solicitud invalida: revise parametros, validaciones o formato JSON."));
            operation.getResponses().addApiResponse("401", new ApiResponse()
                    .description("Solicitud no autorizada por reglas funcionales del microservicio."));
            operation.getResponses().addApiResponse("404", new ApiResponse()
                    .description("Recurso no encontrado."));
            operation.getResponses().addApiResponse("409", new ApiResponse()
                    .description("Operacion no permitida por reglas de negocio, por ejemplo stock insuficiente o inventario duplicado."));
            operation.getResponses().addApiResponse("502", new ApiResponse()
                    .description("Error al consultar videojuegos desde otro microservicio."));
            operation.getResponses().addApiResponse("500", new ApiResponse()
                    .description("Error interno del microservicio."));
            return operation;
        };
    }
}
