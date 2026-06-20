package cl.duoc.usuarios.service;

import cl.duoc.usuarios.exception.ConflictException;
import cl.duoc.usuarios.model.Usuario;
import cl.duoc.usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTests {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void crearAsignaActivoPorDefectoCuandoCorreoEsNuevo() {
        Usuario usuario = usuario("jesus@tiendajuegos.cl");
        usuario.setActivo(null);

        when(usuarioRepository.existsByCorreoIgnoreCase(usuario.getCorreo())).thenReturn(false);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario creado = usuarioService.crear(usuario);

        assertThat(creado.getActivo()).isTrue();
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void crearRechazaCorreoDuplicado() {
        Usuario usuario = usuario("jesus@tiendajuegos.cl");
        when(usuarioRepository.existsByCorreoIgnoreCase(usuario.getCorreo())).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.crear(usuario))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Ya existe un usuario");
    }

    @Test
    void desactivarMarcaUsuarioComoInactivo() {
        Usuario usuario = usuario("cliente@tiendajuegos.cl");
        usuario.setId(2L);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));

        boolean resultado = usuarioService.desactivar(2L);

        assertThat(resultado).isTrue();
        assertThat(usuario.getActivo()).isFalse();
        verify(usuarioRepository).save(usuario);
    }

    private Usuario usuario(String correo) {
        Usuario usuario = new Usuario();
        usuario.setNombre("Jesus");
        usuario.setApellido("Emilio");
        usuario.setCorreo(correo);
        usuario.setRol("CLIENTE");
        return usuario;
    }
}
