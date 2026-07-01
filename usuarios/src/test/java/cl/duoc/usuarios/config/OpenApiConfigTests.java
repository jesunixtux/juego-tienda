package cl.duoc.usuarios.config;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTests {

    private final OpenApiConfig config = new OpenApiConfig();

    @Test
    void configuraInfoServidorYRespuestasComunes() {
        assertThat(config.usuariosOpenApi().getInfo().getTitle()).contains("Usuarios");
        assertThat(config.usuariosOpenApi().getServers().getFirst().getUrl()).isEqualTo("/");

        Operation operation = new Operation().responses(new ApiResponses());
        config.usuariosResponsesCustomizer().customize(operation, null);

        assertThat(operation.getResponses()).containsKeys("400", "404", "500");
    }
}
