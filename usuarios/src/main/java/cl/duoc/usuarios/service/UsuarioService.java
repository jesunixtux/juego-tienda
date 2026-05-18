package cl.duoc.usuarios.service;

import cl.duoc.usuarios.model.Usuario;
import cl.duoc.usuarios.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreoIgnoreCase(correo);
    }

    public Usuario crear(Usuario usuario) {
        if (usuarioRepository.existsByCorreoIgnoreCase(usuario.getCorreo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese correo");
        }

        if (usuario.getActivo() == null) {
            usuario.setActivo(true);
        }

        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> actualizar(Long id, Usuario datosUsuario) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    validarCorreoDisponible(id, datosUsuario.getCorreo());

                    usuario.setNombre(datosUsuario.getNombre());
                    usuario.setApellido(datosUsuario.getApellido());
                    usuario.setCorreo(datosUsuario.getCorreo());
                    usuario.setTelefono(datosUsuario.getTelefono());
                    usuario.setDireccion(datosUsuario.getDireccion());
                    usuario.setRol(datosUsuario.getRol());
                    usuario.setActivo(datosUsuario.getActivo() == null ? true : datosUsuario.getActivo());

                    return usuarioRepository.save(usuario);
                });
    }

    public boolean desactivar(Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setActivo(false);
                    usuarioRepository.save(usuario);
                    return true;
                })
                .orElse(false);
    }

    private void validarCorreoDisponible(Long usuarioId, String correo) {
        usuarioRepository.findByCorreoIgnoreCase(correo)
                .filter(usuario -> !usuario.getId().equals(usuarioId))
                .ifPresent(usuario -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese correo");
                });
    }
}
