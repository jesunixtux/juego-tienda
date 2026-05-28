package cl.duoc.authentication.service;

import cl.duoc.authentication.client.UsuarioClient;
import cl.duoc.authentication.dto.AuthResponse;
import cl.duoc.authentication.dto.CambiarPasswordRequest;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationService {
    private final CredencialRepository credencialRepository;
    private final UsuarioClient usuarioClient;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthenticationService(CredencialRepository credencialRepository, UsuarioClient usuarioClient) {
        this.credencialRepository = credencialRepository;
        this.usuarioClient = usuarioClient;
    }

    public List<Credencial> listarCredenciales() {
        return credencialRepository.findAll();
    }

    public Optional<Credencial> buscarCredencial(Long id) {
        return credencialRepository.findById(id);
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
        credencial.setPasswordHash(passwordEncoder.encode(request.password()));
        credencial.setActivo(true);
        credencialRepository.save(credencial);

        return new AuthResponse(formatearNombreUsuario(usuario), usuario.correo(), usuario.rol(), "Registro exitoso", true, usuario.id());
    }

    public AuthResponse login(LoginRequest request) {
        Credencial credencial = credencialRepository.findByCorreoIgnoreCase(request.correo())
                .orElseThrow(() -> new UnauthorizedException("Credenciales invalidas"));

        if (!Boolean.TRUE.equals(credencial.getActivo())) {
            throw new ForbiddenException("Credencial desactivada");
        }

        if (!passwordEncoder.matches(request.password(), credencial.getPasswordHash())) {
            throw new UnauthorizedException("Credenciales invalidas");
        }

        UsuarioResponse usuario = buscarUsuarioPorCorreo(credencial.getCorreo());
        return new AuthResponse(formatearNombreUsuario(usuario), usuario.correo(), usuario.rol(), "Login exitoso", true, usuario.id());
    }

    @Transactional
    public Optional<Credencial> cambiarPassword(Long id, CambiarPasswordRequest request) {
        return credencialRepository.findById(id)
                .map(credencial -> {
                    if (!passwordEncoder.matches(request.passwordActual(), credencial.getPasswordHash())) {
                        throw new UnauthorizedException("Password actual incorrecta");
                    }

                    credencial.setPasswordHash(passwordEncoder.encode(request.nuevaPassword()));
                    return credencialRepository.save(credencial);
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
}
