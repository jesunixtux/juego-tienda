package cl.duoc.resenas.service;

import cl.duoc.resenas.client.UsuarioFeign;
import cl.duoc.resenas.dto.ResenaDTO;
import cl.duoc.resenas.dto.UsuarioDTO;
import cl.duoc.resenas.mapper.ResenaMapper;
import cl.duoc.resenas.model.Resena;
import cl.duoc.resenas.repository.ResenaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResenaServiceTests {

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private UsuarioFeign usuarioFeign;

    @Spy
    private ResenaMapper resenaMapper;

    @InjectMocks
    private ResenaService resenaService;

    @Test
    void saveValidaUsuarioAntesDeGuardar() {
        Resena resena = resena();
        when(usuarioFeign.obtenerUsuario(2L)).thenReturn(usuario());
        when(resenaRepository.save(resena)).thenReturn(resena);

        Resena guardada = resenaService.save(resena);

        assertThat(guardada.getNombreJuego()).isEqualTo("Minecraft");
        verify(usuarioFeign).obtenerUsuario(2L);
        verify(resenaRepository).save(resena);
    }

    @Test
    void findByUsuarioConDetalleEnriqueceNombreYCorreo() {
        Resena resena = resena();
        when(resenaRepository.findByUsuarioId(2L)).thenReturn(List.of(resena));
        when(usuarioFeign.obtenerUsuario(2L)).thenReturn(usuario());

        List<ResenaDTO> resultado = resenaService.findByUsuarioConDetalle(2L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().getNombreUsuario()).isEqualTo("Jesus Emilio");
        assertThat(resultado.getFirst().getCorreoUsuario()).isEqualTo("jesus@tiendajuegos.cl");
    }

    @Test
    void findByPuntuacionDelegaEnRepositorio() {
        Resena resena = resena();
        when(resenaRepository.findByPuntuacionBetween(4, 5)).thenReturn(List.of(resena));

        List<Resena> resultado = resenaService.findByPuntuacion(4, 5);

        assertThat(resultado).containsExactly(resena);
        verify(resenaRepository).findByPuntuacionBetween(4, 5);
    }

    private Resena resena() {
        Resena resena = new Resena();
        resena.setId(1L);
        resena.setUsuarioId(2L);
        resena.setNombreJuego("Minecraft");
        resena.setComentario("Muy entretenido");
        resena.setPuntuacion(5);
        resena.setFechaResena(LocalDate.of(2026, 5, 20));
        return resena;
    }

    private UsuarioDTO usuario() {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setId(2L);
        usuario.setNombre("Jesus");
        usuario.setApellido("Emilio");
        usuario.setCorreo("jesus@tiendajuegos.cl");
        return usuario;
    }
}
