package cl.duoc.pedidos.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI pedidosOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tienda Videojuegos - Pedidos")
                        .version("1.0.0")
                        .description("API para registrar pedidos y consultar reportes por usuario, fecha o rango de precio.")
                        .license(new License().name("Proyecto academico Duoc UC")))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("API Gateway Docker/local"))
                .components(jwtComponents());
    }

    private Components jwtComponents() {
        return new Components().addSecuritySchemes("bearer-jwt", new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Pegar el token entregado por /auth/login sin escribir la palabra Bearer."));
    }

    @Bean
    OperationCustomizer pedidosResponsesCustomizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            operation.getResponses().addApiResponse("400", new ApiResponse()
                    .description("Solicitud invalida: revise parametros, validaciones o formato JSON."));
            operation.getResponses().addApiResponse("401", new ApiResponse()
                    .description("Token JWT ausente o invalido en rutas protegidas."));
            operation.getResponses().addApiResponse("404", new ApiResponse()
                    .description("Recurso no encontrado."));
            operation.getResponses().addApiResponse("502", new ApiResponse()
                    .description("Error al consultar usuarios desde otro microservicio."));
            operation.getResponses().addApiResponse("500", new ApiResponse()
                    .description("Error interno del microservicio."));
            return operation;
        };
    }
}
