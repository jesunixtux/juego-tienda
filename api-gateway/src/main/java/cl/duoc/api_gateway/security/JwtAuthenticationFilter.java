package cl.duoc.api_gateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (isPublicRequest(request)) {
            return chain.filter(exchange);
        }

        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return unauthorized(exchange, "Token JWT requerido");
        }

        try {
            JwtClaims claims = jwtService.validar(authorization.substring(7).trim());
            ServerHttpRequest securedRequest = request.mutate()
                    .headers(headers -> {
                        headers.set("X-Usuario-Id", String.valueOf(claims.usuarioId()));
                        headers.set("X-Usuario-Correo", claims.correo());
                        headers.set("X-Usuario-Nombre", claims.nombreUsuario());
                        headers.set("X-Usuario-Rol", claims.rol());
                    })
                    .build();
            return chain.filter(exchange.mutate().request(securedRequest).build());
        } catch (InvalidJwtException exception) {
            LOGGER.warn("JWT rechazado path={} message={}", request.getURI().getPath(), exception.getMessage());
            return unauthorized(exchange, exception.getMessage());
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }

    private boolean isPublicRequest(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        if (HttpMethod.OPTIONS.equals(method)) {
            return true;
        }

        if (path.equals("/swagger-ui.html")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/webjars")
                || path.startsWith("/v3/api-docs")
                || path.contains("/v3/api-docs")) {
            return true;
        }

        if (HttpMethod.POST.equals(method) && (path.equals("/auth/login") || path.equals("/auth/registro"))) {
            return true;
        }

        return HttpMethod.GET.equals(method)
                && (path.equals("/videojuegos")
                || path.startsWith("/videojuegos/")
                || path.equals("/inventario")
                || path.startsWith("/inventario/")
                || path.equals("/resenas")
                || path.startsWith("/resenas/"));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = """
                {"timestamp":"%s","status":401,"error":"Unauthorized","message":"%s","path":"%s"}
                """.formatted(Instant.now(), escapeJson(message), escapeJson(exchange.getRequest().getURI().getPath()));
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private String escapeJson(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
