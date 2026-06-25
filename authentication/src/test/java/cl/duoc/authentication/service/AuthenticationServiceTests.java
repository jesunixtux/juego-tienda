package cl.duoc.authentication.service;

import cl.duoc.authentication.client.UsuarioClient;
import cl.duoc.authentication.dto.AuthResponse;
import cl.duoc.authentication.dto.LoginRequest;
import cl.duoc.authentication.dto.RegistroRequest;
import cl.duoc.authentication.dto.UsuarioResponse;
import cl.duoc.authentication.exception.ConflictException;
import cl.duoc.authentication.exception.ForbiddenException;
import cl.duoc.authentication.exception.UnauthorizedException;
import cl.duoc.authentication.model.Credencial;
import cl.duoc.authentication.repository.CredencialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTests {

    @Mock
    private CredencialRepository credencialRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private JwtService jwtService;

    private AuthenticationService authenticationService;
    private PasswordHashService passwordHashService;

    @BeforeEach
    void setUp() {
        passwordHashService = new PasswordHashService();
        authenticationService = new AuthenticationService(
                credencialRepository,
                usuarioClient,
                jwtService,
                passwordHashService
        );
    }

    @Test
    void registrarCreaUsuarioCredencialHasheadaYToken() {
        RegistroRequest request = new RegistroRequest(
                "Jesus",
                "Emilio",
                "jesus@tiendajuegos.cl",
                "+56912345678",
                "Santiago",
                "CLIENTE",
                "cliente123");
        UsuarioResponse usuario = usuario();

        when(credencialRepository.existsByCorreoIgnoreCase(request.correo())).thenReturn(false);
        when(usuarioClient.crear(any())).thenReturn(usuario);
        when(jwtService.generarToken(usuario)).thenReturn("jwt-demo");
        when(jwtService.getExpirationSeconds()).thenReturn(3600L);

        AuthResponse response = authenticationService.registrar(request);

        ArgumentCaptor<Credencial> captor = ArgumentCaptor.forClass(Credencial.class);
        verify(credencialRepository).save(captor.capture());
        Credencial credencialGuardada = captor.getValue();

        assertThat(response.autenticado()).isTrue();
        assertThat(response.usuarioId()).isEqualTo(2L);
        assertThat(response.nombreUsuario()).isEqualTo("Jesus Emilio");
        assertThat(response.token()).isEqualTo("jwt-demo");
        assertThat(credencialGuardada.getPasswordHash()).isNotEqualTo("cliente123");
        assertThat(passwordHashService.matches("cliente123", credencialGuardada.getPasswordHash())).isTrue();
    }

    @Test
    void registrarRechazaCredencialDuplicada() {
        RegistroRequest request = new RegistroRequest(
                "Jesus",
                "Emilio",
                "jesus@tiendajuegos.cl",
                null,
                null,
                "CLIENTE",
                "cliente123");
        when(credencialRepository.existsByCorreoIgnoreCase(request.correo())).thenReturn(true);

        assertThatThrownBy(() -> authenticationService.registrar(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Ya existe una credencial");
    }

    @Test
    void loginRechazaCredencialInactiva() {
        Credencial credencial = credencial("cliente123");
        credencial.setActivo(false);
        when(credencialRepository.findByCorreoIgnoreCase("jesus@tiendajuegos.cl")).thenReturn(Optional.of(credencial));

        assertThatThrownBy(() -> authenticationService.login(new LoginRequest("jesus@tiendajuegos.cl", "cliente123")))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("desactivada");
    }

    @Test
    void loginRechazaPasswordIncorrecta() {
        Credencial credencial = credencial("cliente123");
        when(credencialRepository.findByCorreoIgnoreCase("jesus@tiendajuegos.cl")).thenReturn(Optional.of(credencial));

        assertThatThrownBy(() -> authenticationService.login(new LoginRequest("jesus@tiendajuegos.cl", "mala")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Credenciales invalidas");
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

    private UsuarioResponse usuario() {
        return new UsuarioResponse(
                2L,
                "Jesus",
                "Emilio",
                "jesus@tiendajuegos.cl",
                "+56912345678",
                "Santiago",
                "CLIENTE",
                true,
                LocalDateTime.now());
    }
}
