package cl.duoc.authentication.service;

import cl.duoc.authentication.client.UsuarioClient;
import cl.duoc.authentication.dto.AuthResponse;
import cl.duoc.authentication.dto.CambiarPasswordRequest;
import cl.duoc.authentication.dto.CredencialResponse;
import cl.duoc.authentication.dto.LoginRequest;
import cl.duoc.authentication.dto.RegistroRequest;
import cl.duoc.authentication.dto.UsuarioRequest;
import cl.duoc.authentication.dto.UsuarioResponse;
import cl.duoc.authentication.exception.ConflictException;
import cl.duoc.authentication.exception.ExternalServiceException;
import cl.duoc.authentication.exception.ForbiddenException;
import cl.duoc.authentication.exception.ResourceNotFoundException;
import cl.duoc.authentication.exception.UnauthorizedException;
import cl.duoc.authentication.model.Credencial;
import cl.duoc.authentication.repository.CredencialRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationService {
    private final CredencialRepository credencialRepository;
    private final UsuarioClient usuarioClient;
    private final PasswordHashService passwordHashService;

    public AuthenticationService(
            CredencialRepository credencialRepository,
            UsuarioClient usuarioClient,
            PasswordHashService passwordHashService) {
        this.credencialRepository = credencialRepository;
        this.usuarioClient = usuarioClient;
        this.passwordHashService = passwordHashService;
    }

    public List<CredencialResponse> listarCredenciales() {
        return credencialRepository.findAll().stream()
                .map(CredencialResponse::from)
                .toList();
    }

    public Optional<CredencialResponse> buscarCredencial(Long id) {
        return credencialRepository.findById(id)
                .map(CredencialResponse::from);
    }

    @Transactional
    public AuthResponse registrar(RegistroRequest request) {
        if (credencialRepository.existsByCorreoIgnoreCase(request.correo())) {
            throw new ConflictException("Ya existe una credencial con ese correo");
        }

        UsuarioResponse usuario = crearUsuario(request);

        Credencial credencial = new Credencial();
        credencial.setUsuarioId(usuario.id());
        credencial.setCorreo(usuario.correo());
        credencial.setPasswordHash(passwordHashService.hash(request.password()));
        credencial.setActivo(true);
        credencialRepository.save(credencial);

        return crearRespuestaAutenticada(usuario, "Registro exitoso");
    }

    public AuthResponse login(LoginRequest request) {
        Credencial credencial = credencialRepository.findByCorreoIgnoreCase(request.correo())
                .orElseThrow(() -> new UnauthorizedException("Credenciales invalidas"));

        if (!Boolean.TRUE.equals(credencial.getActivo())) {
            throw new ForbiddenException("Credencial desactivada");
        }

        if (!passwordHashService.matches(request.password(), credencial.getPasswordHash())) {
            throw new UnauthorizedException("Credenciales invalidas");
        }

        UsuarioResponse usuario = buscarUsuarioPorCorreo(credencial.getCorreo());
        return crearRespuestaAutenticada(usuario, "Login exitoso");
    }

    @Transactional
    public Optional<CredencialResponse> cambiarPassword(Long id, CambiarPasswordRequest request) {
        return credencialRepository.findById(id)
                .map(credencial -> {
                    if (!passwordHashService.matches(request.passwordActual(), credencial.getPasswordHash())) {
                        throw new UnauthorizedException("Password actual incorrecta");
                    }

                    credencial.setPasswordHash(passwordHashService.hash(request.nuevaPassword()));
                    return CredencialResponse.from(credencialRepository.save(credencial));
                });
    }

    @Transactional
    public boolean desactivar(Long id) {
        return credencialRepository.findById(id)
                .map(credencial -> {
                    credencial.setActivo(false);
                    credencialRepository.save(credencial);
                    return true;
                })
                .orElse(false);
    }

    private UsuarioResponse crearUsuario(RegistroRequest request) {
        try {
            UsuarioRequest usuarioRequest = new UsuarioRequest(
                    request.nombre(),
                    request.apellido(),
                    request.correo(),
                    request.telefono(),
                    request.direccion(),
                    request.rol(),
                    true
            );

            return usuarioClient.crear(usuarioRequest);
        } catch (FeignException.Conflict exception) {
            throw new ConflictException("Ya existe un usuario con ese correo");
        } catch (FeignException exception) {
            throw new ExternalServiceException("No se pudo crear el usuario");
        }
    }

    private UsuarioResponse buscarUsuarioPorCorreo(String correo) {
        try {
            return usuarioClient.buscarPorCorreo(correo);
        } catch (FeignException.NotFound exception) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        } catch (FeignException exception) {
            throw new ExternalServiceException("No se pudo consultar el usuario");
        }
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

    private AuthResponse crearRespuestaAutenticada(UsuarioResponse usuario, String mensaje) {
        return new AuthResponse(
                formatearNombreUsuario(usuario),
                usuario.correo(),
                usuario.rol(),
                mensaje,
                true,
                usuario.id()
        );
    }
}
