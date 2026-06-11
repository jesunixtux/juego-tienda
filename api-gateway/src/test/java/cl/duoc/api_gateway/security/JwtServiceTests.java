package cl.duoc.api_gateway.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTests {
    private static final String SECRET = "tienda-videojuegos-test-secret-change-me-2026";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void validarAceptaTokenFirmado() throws Exception {
        JwtService jwtService = new JwtService(objectMapper, SECRET);

        JwtClaims claims = jwtService.validar(crearToken("jesus@tiendajuegos.cl", 2L, "Jesus Emilio", "CLIENTE", 120));

        assertThat(claims.correo()).isEqualTo("jesus@tiendajuegos.cl");
        assertThat(claims.usuarioId()).isEqualTo(2L);
        assertThat(claims.nombreUsuario()).isEqualTo("Jesus Emilio");
        assertThat(claims.rol()).isEqualTo("CLIENTE");
    }

    @Test
    void validarRechazaFirmaInvalida() throws Exception {
        JwtService jwtService = new JwtService(objectMapper, SECRET);
        String token = crearToken("jesus@tiendajuegos.cl", 2L, "Jesus Emilio", "CLIENTE", 120);
        String alterado = token.substring(0, token.length() - 2) + "xx";

        assertThatThrownBy(() -> jwtService.validar(alterado))
                .isInstanceOf(InvalidJwtException.class)
                .hasMessageContaining("firma invalida");
    }

    @Test
    void validarRechazaTokenExpirado() throws Exception {
        JwtService jwtService = new JwtService(objectMapper, SECRET);

        assertThatThrownBy(() -> jwtService.validar(crearToken("admin@tiendajuegos.cl", 1L, "Admin Tienda", "ADMIN", -10)))
                .isInstanceOf(InvalidJwtException.class)
                .hasMessageContaining("expirado");
    }

    private String crearToken(String correo, Long usuarioId, String nombreUsuario, String rol, long secondsToLive) throws Exception {
        long now = Instant.now().getEpochSecond();

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", correo);
        payload.put("usuarioId", usuarioId);
        payload.put("nombreUsuario", nombreUsuario);
        payload.put("rol", rol);
        payload.put("iat", now);
        payload.put("exp", now + secondsToLive);

        String unsignedToken = encode(objectMapper.writeValueAsBytes(header)) + "." + encode(objectMapper.writeValueAsBytes(payload));
        return unsignedToken + "." + encode(sign(unsignedToken));
    }

    private String encode(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    private byte[] sign(String content) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
    }
}
