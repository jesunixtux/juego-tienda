package cl.duoc.authentication.service;

import cl.duoc.authentication.client.UsuarioClient;
import cl.duoc.authentication.dto.AuthResponse;
import cl.duoc.authentication.dto.CambiarPasswordRequest;
import cl.duoc.authentication.dto.CredencialResponse;
import cl.duoc.authentication.dto.LoginRequest;
import cl.duoc.authentication.dto.RegistroRequest;
import cl.duoc.authentication.dto.UsuarioResponse;
import cl.duoc.authentication.exception.ConflictException;
import cl.duoc.authentication.exception.ResourceNotFoundException;
import cl.duoc.authentication.exception.UnauthorizedException;
import cl.duoc.authentication.model.Credencial;
import cl.duoc.authentication.repository.CredencialRepository;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceBranchTests {

    @Mock
    private CredencialRepository credencialRepository;

    @Mock
    private UsuarioClient usuarioClient;

    private AuthenticationService authenticationService;
    private PasswordHashService passwordHashService;

    @BeforeEach
    void setUp() {
        passwordHashService = new PasswordHashService();
        authenticationService = new AuthenticationService(
                credencialRepository,
                usuarioClient,
                passwordHashService
        );
    }

    @Test
    void listarYBuscarCredencialesNoExponenPasswordHash() {
        Credencial credencial = credencial("clave123");
        credencial.setFechaCreacion(LocalDateTime.of(2026, 6, 1, 10, 0));
        credencial.setFechaActualizacion(LocalDateTime.of(2026, 6, 2, 10, 0));
        when(credencialRepository.findAll()).thenReturn(List.of(credencial));
        when(credencialRepository.findById(1L)).thenReturn(Optional.of(credencial));

        List<CredencialResponse> credenciales = authenticationService.listarCredenciales();
        Optional<CredencialResponse> encontrada = authenticationService.buscarCredencial(1L);

        assertThat(credenciales).hasSize(1);
        assertThat(credenciales.getFirst().correo()).isEqualTo("jesus@tiendajuegos.cl");
        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().usuarioId()).isEqualTo(2L);
    }

    @Test
    void loginExitosoUsaCorreoSiUsuarioNoTieneNombre() {
        Credencial credencial = credencial("clave123");
        UsuarioResponse usuario = new UsuarioResponse(
                2L,
                " ",
                null,
                "jesus@tiendajuegos.cl",
                null,
                null,
                "CLIENTE",
                true,
                null);
        when(credencialRepository.findByCorreoIgnoreCase("jesus@tiendajuegos.cl")).thenReturn(Optional.of(credencial));
        when(usuarioClient.buscarPorCorreo("jesus@tiendajuegos.cl")).thenReturn(usuario);

        AuthResponse response = authenticationService.login(new LoginRequest("jesus@tiendajuegos.cl", "clave123"));

        assertThat(response.autenticado()).isTrue();
        assertThat(response.nombreUsuario()).isEqualTo("jesus@tiendajuegos.cl");
        assertThat(response.mensaje()).isEqualTo("Login exitoso");
    }

    @Test
    void loginSinCredencialRechazaConUnauthorized() {
        when(credencialRepository.findByCorreoIgnoreCase("nadie@tiendajuegos.cl")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.login(new LoginRequest("nadie@tiendajuegos.cl", "clave123")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Credenciales invalidas");
    }

    @Test
    void loginConUsuarioRemotoNoEncontradoEntregaErrorControlado() {
        Credencial credencial = credencial("clave123");
        when(credencialRepository.findByCorreoIgnoreCase("jesus@tiendajuegos.cl")).thenReturn(Optional.of(credencial));
        when(usuarioClient.buscarPorCorreo("jesus@tiendajuegos.cl")).thenThrow(feignStatus(404));

        assertThatThrownBy(() -> authenticationService.login(new LoginRequest("jesus@tiendajuegos.cl", "clave123")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    @Test
    void cambiarPasswordActualizaHashCuandoPasswordActualCoincide() {
        Credencial credencial = credencial("clave123");
        when(credencialRepository.findById(1L)).thenReturn(Optional.of(credencial));
        when(credencialRepository.save(any(Credencial.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<CredencialResponse> response = authenticationService.cambiarPassword(
                1L,
                new CambiarPasswordRequest("clave123", "nueva123"));

        assertThat(response).isPresent();
        assertThat(passwordHashService.matches("nueva123", credencial.getPasswordHash())).isTrue();
        verify(credencialRepository).save(credencial);
    }

    @Test
    void cambiarPasswordRechazaPasswordActualIncorrecta() {
        when(credencialRepository.findById(1L)).thenReturn(Optional.of(credencial("clave123")));

        assertThatThrownBy(() -> authenticationService.cambiarPassword(
                1L,
                new CambiarPasswordRequest("mala123", "nueva123")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Password actual incorrecta");
    }

    @Test
    void cambiarPasswordYDesactivarRetornanVacioOFalsoSiNoExiste() {
        when(credencialRepository.findById(99L)).thenReturn(Optional.empty());
        when(credencialRepository.findById(100L)).thenReturn(Optional.empty());

        assertThat(authenticationService.cambiarPassword(99L, new CambiarPasswordRequest("clave123", "nueva123")))
                .isEmpty();
        assertThat(authenticationService.desactivar(100L)).isFalse();
    }

    @Test
    void desactivarMarcaCredencialComoInactiva() {
        Credencial credencial = credencial("clave123");
        when(credencialRepository.findById(1L)).thenReturn(Optional.of(credencial));

        boolean resultado = authenticationService.desactivar(1L);

        assertThat(resultado).isTrue();
        assertThat(credencial.getActivo()).isFalse();
        verify(credencialRepository).save(credencial);
    }

    @Test
    void registrarTraduceConflictoDelMicroservicioUsuarios() {
        RegistroRequest request = registro();
        when(credencialRepository.existsByCorreoIgnoreCase(request.correo())).thenReturn(false);
        when(usuarioClient.crear(any())).thenThrow(feignStatus(409));

        assertThatThrownBy(() -> authenticationService.registrar(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Ya existe un usuario");
    }

    @Test
    void credencialLifecycleAsignaFechasYActivo() {
        Credencial credencial = credencial("clave123");
        credencial.setActivo(null);

        credencial.antesDeGuardar();
        LocalDateTime fechaCreacion = credencial.getFechaCreacion();
        credencial.antesDeActualizar();

        assertThat(credencial.getActivo()).isTrue();
        assertThat(fechaCreacion).isNotNull();
        assertThat(credencial.getFechaActualizacion()).isNotNull();
    }

    private Credencial credencial(String password) {
        Credencial credencial = new Credencial();
        credencial.setId(1L);
        credencial.setUsuarioId(2L);
        credencial.setCorreo("jesus@tiendajuegos.cl");
        credencial.setPasswordHash(passwordHashService.hash(password));
        credencial.setActivo(true);
        return credencial;
    }

    private RegistroRequest registro() {
        return new RegistroRequest(
                "Jesus",
                "Emilio",
                "jesus@tiendajuegos.cl",
                "+56912345678",
                "Santiago",
                "CLIENTE",
                "clave123");
    }

    private FeignException feignStatus(int status) {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/usuarios",
                Map.of(),
                null,
                StandardCharsets.UTF_8,
                null);
        Response response = Response.builder()
                .status(status)
                .reason("test")
                .request(request)
                .headers(Map.of())
                .build();
        return FeignException.errorStatus("test", response);
    }
}
