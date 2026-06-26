package cl.duoc.authentication.controller;

import cl.duoc.authentication.dto.AuthResponse;
import cl.duoc.authentication.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerValidationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Test
    void registrarRechazaPasswordAusenteConErrorPersonalizado() throws Exception {
        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Jesus",
                                  "apellido": "Emilio",
                                  "correo": "jesus.validacion@tiendajuegos.cl",
                                  "telefono": "+56912345678",
                                  "direccion": "Santiago",
                                  "rol": "CLIENTE"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Contrasena invalida"))
                .andExpect(jsonPath("$.validationErrors.password").value("La contrasena invalida: es obligatoria"));
    }

    @Test
    void registrarRechazaPasswordCortaConErrorPersonalizado() throws Exception {
        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Jesus",
                                  "apellido": "Emilio",
                                  "correo": "jesus.validacion@tiendajuegos.cl",
                                  "telefono": "+56912345678",
                                  "direccion": "Santiago",
                                  "rol": "CLIENTE",
                                  "password": "1234"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Contrasena invalida"))
                .andExpect(jsonPath("$.validationErrors.password").value("La contrasena invalida: debe tener minimo 5 caracteres"));
    }

    @Test
    void registrarAceptaPasswordDeCincoCaracteres() throws Exception {
        when(authenticationService.registrar(any())).thenReturn(new AuthResponse(
                "Jesus Emilio",
                "jesus.validacion@tiendajuegos.cl",
                "CLIENTE",
                "Registro exitoso",
                true,
                99L,
                "token-demo",
                "Bearer",
                7200L
        ));

        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Jesus",
                                  "apellido": "Emilio",
                                  "correo": "jesus.validacion@tiendajuegos.cl",
                                  "telefono": "+56912345678",
                                  "direccion": "Santiago",
                                  "rol": "CLIENTE",
                                  "password": "12345"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.autenticado").value(true))
                .andExpect(jsonPath("$.usuarioId").value(99));
    }
}
