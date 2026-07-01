package cl.duoc.usuarios.controller;

import cl.duoc.usuarios.model.Usuario;
import cl.duoc.usuarios.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsuarioControllerUnitTests {

    private final UsuarioService usuarioService = mock(UsuarioService.class);
    private final UsuarioController controller = new UsuarioController(usuarioService);

    @Test
    void listarUsaActivosCuandoParametroEsTrue() {
        Usuario usuario = usuario(1L);
        when(usuarioService.listarActivos()).thenReturn(List.of(usuario));

        ResponseEntity<List<Usuario>> response = controller.listar(true);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsExactly(usuario);
        verify(usuarioService).listarActivos();
    }

    @Test
    void listarSinFiltroDevuelveTodos() {
        Usuario usuario = usuario(1L);
        when(usuarioService.listar()).thenReturn(List.of(usuario));

        ResponseEntity<List<Usuario>> response = controller.listar(null);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsExactly(usuario);
        verify(usuarioService).listar();
    }

    @Test
    void buscarPorIdRetornaOkONotFound() {
        Usuario usuario = usuario(2L);
        when(usuarioService.buscarPorId(2L)).thenReturn(Optional.of(usuario));
        when(usuarioService.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThat(controller.buscarPorId(2L).getStatusCode().value()).isEqualTo(200);
        assertThat(controller.buscarPorId(99L).getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void buscarPorCorreoRetornaOkONotFound() {
        Usuario usuario = usuario(2L);
        when(usuarioService.buscarPorCorreo("jesus@tienda.cl")).thenReturn(Optional.of(usuario));
        when(usuarioService.buscarPorCorreo("nadie@tienda.cl")).thenReturn(Optional.empty());

        assertThat(controller.buscarPorCorreo("jesus@tienda.cl").getStatusCode().value()).isEqualTo(200);
        assertThat(controller.buscarPorCorreo("nadie@tienda.cl").getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void crearYActualizarDeleganEnService() {
        Usuario usuario = usuario(3L);
        when(usuarioService.crear(usuario)).thenReturn(usuario);
        when(usuarioService.actualizar(3L, usuario)).thenReturn(Optional.of(usuario));
        when(usuarioService.actualizar(99L, usuario)).thenReturn(Optional.empty());

        assertThat(controller.crear(usuario).getStatusCode().value()).isEqualTo(201);
        assertThat(controller.actualizar(3L, usuario).getStatusCode().value()).isEqualTo(200);
        assertThat(controller.actualizar(99L, usuario).getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void desactivarRetornaNoContentONotFound() {
        when(usuarioService.desactivar(3L)).thenReturn(true);
        when(usuarioService.desactivar(99L)).thenReturn(false);

        assertThat(controller.desactivar(3L).getStatusCode().value()).isEqualTo(204);
        assertThat(controller.desactivar(99L).getStatusCode().value()).isEqualTo(404);
    }

    private Usuario usuario(Long id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre("Jesus");
        usuario.setApellido("Emilio");
        usuario.setCorreo("jesus@tienda.cl");
        usuario.setRol("CLIENTE");
        usuario.setActivo(true);
        return usuario;
    }
}
