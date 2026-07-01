package cl.duoc.usuarios.service;

import cl.duoc.usuarios.exception.ConflictException;
import cl.duoc.usuarios.model.Usuario;
import cl.duoc.usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceBranchTests {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void listarYBuscarDeleganEnRepositorio() {
        Usuario usuario = usuario(1L, "jesus@tiendajuegos.cl");
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));
        when(usuarioRepository.findByActivoTrue()).thenReturn(List.of(usuario));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByCorreoIgnoreCase("jesus@tiendajuegos.cl")).thenReturn(Optional.of(usuario));

        assertThat(usuarioService.listar()).containsExactly(usuario);
        assertThat(usuarioService.listarActivos()).containsExactly(usuario);
        assertThat(usuarioService.buscarPorId(1L)).contains(usuario);
        assertThat(usuarioService.buscarPorCorreo("jesus@tiendajuegos.cl")).contains(usuario);
    }

    @Test
    void actualizarPermiteMismoCorreoYCompletaActivoPorDefecto() {
        Usuario existente = usuario(2L, "cliente@tiendajuegos.cl");
        Usuario datos = usuario(null, "cliente@tiendajuegos.cl");
        datos.setNombre("Cliente");
        datos.setApellido("Actualizado");
        datos.setTelefono("+56911112222");
        datos.setDireccion("Santiago Centro");
        datos.setRol("ADMIN");
        datos.setActivo(null);

        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.findByCorreoIgnoreCase("cliente@tiendajuegos.cl")).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Usuario> resultado = usuarioService.actualizar(2L, datos);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Cliente");
        assertThat(resultado.get().getApellido()).isEqualTo("Actualizado");
        assertThat(resultado.get().getRol()).isEqualTo("ADMIN");
        assertThat(resultado.get().getActivo()).isTrue();
    }

    @Test
    void actualizarRechazaCorreoUsadoPorOtroUsuario() {
        Usuario existente = usuario(2L, "cliente@tiendajuegos.cl");
        Usuario otro = usuario(3L, "cliente@tiendajuegos.cl");
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.findByCorreoIgnoreCase("cliente@tiendajuegos.cl")).thenReturn(Optional.of(otro));

        assertThatThrownBy(() -> usuarioService.actualizar(2L, usuario(null, "cliente@tiendajuegos.cl")))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Ya existe un usuario");
    }

    @Test
    void actualizarYDesactivarRetornanFalseOVacioCuandoNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        when(usuarioRepository.findById(100L)).thenReturn(Optional.empty());

        assertThat(usuarioService.actualizar(99L, usuario(null, "nuevo@tiendajuegos.cl"))).isEmpty();
        assertThat(usuarioService.desactivar(100L)).isFalse();
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void antesDeGuardarAsignaActivoYFechaRegistro() {
        Usuario usuario = usuario(1L, "jesus@tiendajuegos.cl");
        usuario.setActivo(null);

        usuario.antesDeGuardar();

        assertThat(usuario.getActivo()).isTrue();
        assertThat(usuario.getFechaRegistro()).isNotNull();
    }

    private Usuario usuario(Long id, String correo) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre("Jesus");
        usuario.setApellido("Emilio");
        usuario.setCorreo(correo);
        usuario.setRol("CLIENTE");
        usuario.setActivo(true);
        return usuario;
    }
}
