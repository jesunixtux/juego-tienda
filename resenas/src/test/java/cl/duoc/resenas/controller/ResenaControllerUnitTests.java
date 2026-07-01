package cl.duoc.resenas.controller;

import cl.duoc.resenas.dto.ResenaDTO;
import cl.duoc.resenas.model.Resena;
import cl.duoc.resenas.service.ResenaService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResenaControllerUnitTests {

    private final ResenaService resenaService = mock(ResenaService.class);
    private final ResenaController controller = new ResenaController(resenaService);

    @Test
    void consultasCubrenListadoDetalleUsuarioFechaYPuntuacion() {
        ResenaDTO dto = dto();
        when(resenaService.findAllConUsuario()).thenReturn(List.of(dto));
        when(resenaService.findByIdConUsuario(1L)).thenReturn(Optional.of(dto));
        when(resenaService.findByIdConUsuario(99L)).thenReturn(Optional.empty());
        when(resenaService.findConUsuario()).thenReturn(List.of(dto));
        when(resenaService.findByUsuarioConDetalle(2L)).thenReturn(List.of(dto));
        when(resenaService.findByFechaConDetalle(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31))).thenReturn(List.of(dto));
        when(resenaService.findByPuntuacionConDetalle(4, 5)).thenReturn(List.of(dto));

        assertThat(controller.listar().getBody()).containsExactly(dto);
        assertThat(controller.buscar(1L).getStatusCode().value()).isEqualTo(200);
        assertThat(controller.buscar(99L).getStatusCode().value()).isEqualTo(404);
        assertThat(controller.detalle().getBody()).containsExactly(dto);
        assertThat(controller.porUsuario(2L).getBody()).containsExactly(dto);
        assertThat(controller.porFecha(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)).getBody()).containsExactly(dto);
        assertThat(controller.porPuntuacion(4, 5).getBody()).containsExactly(dto);
    }

    @Test
    void escrituraCubreCrearActualizarYBorrar() {
        Resena resena = resena();
        ResenaDTO dto = dto();
        when(resenaService.saveConUsuario(resena)).thenReturn(dto);
        when(resenaService.updateConUsuario(1L, resena)).thenReturn(Optional.of(dto));
        when(resenaService.updateConUsuario(99L, resena)).thenReturn(Optional.empty());
        when(resenaService.delete(1L)).thenReturn(true);
        when(resenaService.delete(99L)).thenReturn(false);

        assertThat(controller.crear(resena).getStatusCode().value()).isEqualTo(201);
        assertThat(controller.actualizar(1L, resena).getStatusCode().value()).isEqualTo(200);
        assertThat(controller.actualizar(99L, resena).getStatusCode().value()).isEqualTo(404);
        assertThat(controller.borrar(1L).getStatusCode().value()).isEqualTo(204);
        assertThat(controller.borrar(99L).getStatusCode().value()).isEqualTo(404);
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

    private ResenaDTO dto() {
        ResenaDTO dto = new ResenaDTO();
        dto.setId(1L);
        dto.setUsuarioId(2L);
        dto.setNombreUsuario("Jesus Emilio");
        dto.setCorreoUsuario("jesus@tienda.cl");
        dto.setNombreJuego("Minecraft");
        dto.setComentario("Muy entretenido");
        dto.setPuntuacion(5);
        dto.setFechaResena(LocalDate.of(2026, 5, 20));
        return dto;
    }
}
