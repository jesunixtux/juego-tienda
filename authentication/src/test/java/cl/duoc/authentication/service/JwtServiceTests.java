package cl.duoc.authentication.service;

import cl.duoc.authentication.dto.UsuarioResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTests {
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void generarTokenIncluyeClaimsEsperados() throws Exception {
        JwtService jwtService = new JwtService(objectMapper, "tienda-videojuegos-test-secret-change-me-2026", 60);
        UsuarioResponse usuario = new UsuarioResponse(
                7L,
                "Jesus",
                "Emilio",
                "jesus@tiendajuegos.cl",
                "+56912345678",
                "Santiago",
                "CLIENTE",
                true,
                LocalDateTime.now()
        );

        String token = jwtService.generarToken(usuario);
        String[] parts = token.split("\\.");
        Map<String, Object> payload = objectMapper.readValue(Base64.getUrlDecoder().decode(parts[1]), MAP_TYPE);

        assertThat(parts).hasSize(3);
        assertThat(payload)
                .containsEntry("sub", "jesus@tiendajuegos.cl")
                .containsEntry("usuarioId", 7)
                .containsEntry("nombreUsuario", "Jesus Emilio")
                .containsEntry("rol", "CLIENTE");
        assertThat(payload).containsKeys("iat", "exp");
        assertThat(payload).doesNotContainKey("password");
        assertThat(payload).doesNotContainKey("passwordHash");
    }
}
