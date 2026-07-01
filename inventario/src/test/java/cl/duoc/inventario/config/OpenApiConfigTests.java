package cl.duoc.inventario.config;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTests {

    private final OpenApiConfig config = new OpenApiConfig();

    @Test
    void configuraInfoServidorYRespuestasComunes() {
        assertThat(config.inventarioOpenApi().getInfo().getTitle()).contains("Inventario");
        assertThat(config.inventarioOpenApi().getServers().getFirst().getUrl()).isEqualTo("/");

        Operation operation = new Operation().responses(new ApiResponses());
        config.inventarioResponsesCustomizer().customize(operation, null);

        assertThat(operation.getResponses()).containsKeys("400", "401", "404", "409", "502", "500");
    }
}
