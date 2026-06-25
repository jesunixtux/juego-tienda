package cl.duoc.usuarios.config;

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
    OpenAPI usuariosOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tienda Videojuegos - Usuarios")
                        .version("1.0.0")
                        .description("API para administrar usuarios, consultar usuarios activos y buscar cuentas por correo.")
                        .license(new License().name("Proyecto academico Duoc UC")))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("API Gateway Docker/local"));
    }
    @Bean
    OperationCustomizer usuariosResponsesCustomizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            operation.getResponses().addApiResponse("400", new ApiResponse()
                    .description("Solicitud invalida: revise parametros, validaciones o formato JSON."));
            operation.getResponses().addApiResponse("404", new ApiResponse()
                    .description("Recurso no encontrado."));
            operation.getResponses().addApiResponse("500", new ApiResponse()
                    .description("Error interno del microservicio."));
            return operation;
        };
    }
}
