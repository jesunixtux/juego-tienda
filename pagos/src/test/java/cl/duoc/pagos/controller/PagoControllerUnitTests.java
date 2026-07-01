package cl.duoc.pagos.controller;

import cl.duoc.pagos.dto.ActualizarEstadoPagoRequest;
import cl.duoc.pagos.dto.CrearPagoRequest;
import cl.duoc.pagos.dto.PagoResponse;
import cl.duoc.pagos.service.PagoService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PagoControllerUnitTests {

    private final PagoService pagoService = mock(PagoService.class);
    private final PagoController controller = new PagoController(pagoService);

    @Test
    void consultasRetornanPagos() {
        PagoResponse pago = pago();
        when(pagoService.listar()).thenReturn(List.of(pago));
        when(pagoService.buscarPorId(1L)).thenReturn(Optional.of(pago));
        when(pagoService.buscarPorId(99L)).thenReturn(Optional.empty());
        when(pagoService.listarPorUsuario(2L)).thenReturn(List.of(pago));

        assertThat(controller.listar().getBody()).containsExactly(pago);
        assertThat(controller.buscarPorId(1L).getStatusCode().value()).isEqualTo(200);
        assertThat(controller.buscarPorId(99L).getStatusCode().value()).isEqualTo(404);
        assertThat(controller.listarPorUsuario(2L).getBody()).containsExactly(pago);
    }

    @Test
    void escrituraRetornaEstadosCorrectos() {
        PagoResponse pago = pago();
        CrearPagoRequest crear = new CrearPagoRequest(2L, "TARJETA");
        ActualizarEstadoPagoRequest actualizar = new ActualizarEstadoPagoRequest("PENDIENTE");
        when(pagoService.crear(crear)).thenReturn(pago);
        when(pagoService.actualizarEstado(1L, actualizar)).thenReturn(Optional.of(pago));
        when(pagoService.actualizarEstado(99L, actualizar)).thenReturn(Optional.empty());
        when(pagoService.anular(1L)).thenReturn(true);
        when(pagoService.anular(99L)).thenReturn(false);
        when(pagoService.eliminar(1L)).thenReturn(true);
        when(pagoService.eliminar(99L)).thenReturn(false);

        assertThat(controller.crear(crear).getStatusCode().value()).isEqualTo(201);
        assertThat(controller.actualizarEstado(1L, actualizar).getStatusCode().value()).isEqualTo(200);
        assertThat(controller.actualizarEstado(99L, actualizar).getStatusCode().value()).isEqualTo(404);
        assertThat(controller.anular(1L).getStatusCode().value()).isEqualTo(204);
        assertThat(controller.anular(99L).getStatusCode().value()).isEqualTo(404);
        assertThat(controller.eliminar(1L).getStatusCode().value()).isEqualTo(204);
        assertThat(controller.eliminar(99L).getStatusCode().value()).isEqualTo(404);
    }

    private PagoResponse pago() {
        return new PagoResponse("Jesus Emilio", 19990, "TARJETA", "APROBADO", "PAY-ABC12345", null, 1L, 2L);
    }
}
