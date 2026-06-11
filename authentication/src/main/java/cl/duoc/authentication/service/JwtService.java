package cl.duoc.authentication.service;

import cl.duoc.authentication.dto.UsuarioResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class JwtService {
    private static final String ALGORITHM = "HmacSHA256";
    private static final String TOKEN_TYPE = "JWT";

    private final ObjectMapper objectMapper;
    private final String secret;
    private final long expirationMinutes;

    public JwtService(
            ObjectMapper objectMapper,
            @Value("${security.jwt.secret:tienda-videojuegos-local-dev-secret-change-me-2026}") String secret,
            @Value("${security.jwt.expiration-minutes:120}") long expirationMinutes) {
        this.objectMapper = objectMapper;
        this.secret = secret;
        this.expirationMinutes = expirationMinutes;
    }

    public String generarToken(UsuarioResponse usuario) {
        long issuedAt = Instant.now().getEpochSecond();
        long expiresAt = issuedAt + getExpirationSeconds();

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", TOKEN_TYPE);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", usuario.correo());
        payload.put("usuarioId", usuario.id());
        payload.put("nombreUsuario", formatearNombreUsuario(usuario));
        payload.put("rol", usuario.rol());
        payload.put("iat", issuedAt);
        payload.put("exp", expiresAt);

        try {
            String unsignedToken = encodeJson(header) + "." + encodeJson(payload);
            return unsignedToken + "." + encode(sign(unsignedToken));
        } catch (Exception exception) {
            throw new IllegalStateException("No se pudo generar el token JWT", exception);
        }
    }

    public long getExpirationSeconds() {
        return expirationMinutes * 60;
    }

    private String encodeJson(Map<String, Object> data) throws Exception {
        return encode(objectMapper.writeValueAsBytes(data));
    }

    private String encode(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    private byte[] sign(String content) throws GeneralSecurityException {
        Mac mac = Mac.getInstance(ALGORITHM);
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM));
        return mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
    }

    private String formatearNombreUsuario(UsuarioResponse usuario) {
        String nombre = usuario.nombre() == null ? "" : usuario.nombre().trim();
        String apellido = usuario.apellido() == null ? "" : usuario.apellido().trim();
        String nombreCompleto = (nombre + " " + apellido).trim();

        if (!nombreCompleto.isBlank()) {
            return nombreCompleto;
        }

        if (usuario.correo() != null && !usuario.correo().isBlank()) {
            return usuario.correo();
        }

        return "Usuario " + usuario.id();
    }
}
