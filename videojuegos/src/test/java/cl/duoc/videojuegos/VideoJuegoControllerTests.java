package cl.duoc.videojuegos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VideoJuegoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void crearAceptaPlataformasPermitidas() throws Exception {
        crearVideojuego("PC", "Juego Test PC");
        crearVideojuego("PS4", "Juego Test PS4");
        crearVideojuego("XBOX", "Juego Test XBOX");
    }

    @Test
    void crearRechazaPlataformaNoPermitidaConErrorPersonalizado() throws Exception {
        mockMvc.perform(post("/videojuegos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Juego Plataforma Invalida",
                                  "categoria": "Test",
                                  "precio": 10000,
                                  "plataforma": "Game Boy",
                                  "descripcion": "Juego usado para probar la validacion de plataformas"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("Plataforma no valida")))
                .andExpect(jsonPath("$.message").value(containsString("PS4")))
                .andExpect(jsonPath("$.message").value(containsString("XBOX")))
                .andExpect(jsonPath("$.message").value(containsString("PC")))
                .andExpect(jsonPath("$.path").value("/videojuegos"));
    }

    private void crearVideojuego(String plataforma, String nombre) throws Exception {
        mockMvc.perform(post("/videojuegos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "%s",
                                  "categoria": "Test",
                                  "precio": 10000,
                                  "plataforma": "%s",
                                  "descripcion": "Juego usado para probar plataformas permitidas"
                                }
                                """.formatted(nombre, plataforma)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.plataforma").value(plataforma));
    }
}
