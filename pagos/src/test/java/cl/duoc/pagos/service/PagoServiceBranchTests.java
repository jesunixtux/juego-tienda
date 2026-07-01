package cl.duoc.pagos.service;

import cl.duoc.pagos.client.CarritoClient;
import cl.duoc.pagos.client.UsuarioClient;
import cl.duoc.pagos.dto.ActualizarEstadoPagoRequest;
import cl.duoc.pagos.dto.CrearPagoRequest;
import cl.duoc.pagos.dto.PagoResponse;
import cl.duoc.pagos.dto.ResumenCarritoResponse;
import cl.duoc.pagos.dto.UsuarioResponse;
import cl.duoc.pagos.exception.ExternalServiceException;
import cl.duoc.pagos.model.Pago;
import cl.duoc.pagos.repository.PagoRepository;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagoServiceBranchTests {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private CarritoClient carritoClient;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private PagoService pagoService;

    @Test
    void listarPorUsuarioYBuscarPorIdEnriquecenNombre() {
        Pago pago = pago(1L, 2L, "APROBADO");
        when(pagoRepository.findByUsuarioId(2L)).thenReturn(List.of(pago));
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(usuarioClient.buscarPorId(2L)).thenReturn(usuario("Jesus", "Emilio", "jesus@tiendajuegos.cl"));

        List<PagoResponse> porUsuario = pagoService.listarPorUsuario(2L);
        Optional<PagoResponse> porId = pagoService.buscarPorId(1L);

        assertThat(porUsuario.getFirst().nombreUsuario()).isEqualTo("Jesus Emilio");
        assertThat(porId).isPresent();
        assertThat(porId.get().nombreUsuario()).isEqualTo("Jesus Emilio");
    }

    @Test
    void actualizarEstadoRetornaPagoActualizadoOVacio() {
        Pago pago = pago(1L, 2L, "PENDIENTE");
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(usuarioClient.buscarPorId(2L)).thenReturn(usuario("Jesus", "Emilio", "jesus@tiendajuegos.cl"));

        Optional<PagoResponse> actualizado = pagoService.actualizarEstado(1L, new ActualizarEstadoPagoRequest("RECHAZADO"));

        assertThat(actualizado).isPresent();
        assertThat(actualizado.get().estado()).isEqualTo("RECHAZADO");
        assertThat(pagoService.actualizarEstado(99L, new ActualizarEstadoPagoRequest("APROBADO"))).isEmpty();
    }

    @Test
    void anularYEliminarRetornanSegunExistencia() {
        Pago pago = pago(1L, 2L, "APROBADO");
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());
        when(pagoRepository.existsById(1L)).thenReturn(true);
        when(pagoRepository.existsById(99L)).thenReturn(false);

        assertThat(pagoService.anular(1L)).isTrue();
        assertThat(pago.getEstado()).isEqualTo("ANULADO");
        assertThat(pagoService.anular(99L)).isFalse();
        assertThat(pagoService.eliminar(1L)).isTrue();
        assertThat(pagoService.eliminar(99L)).isFalse();
        verify(pagoRepository).deleteById(1L);
        verify(pagoRepository, never()).deleteById(99L);
    }

    @Test
    void crearTraduceFalloAlConsultarOVaciarCarrito() {
        when(carritoClient.obtenerResumen(2L)).thenThrow(feignStatus(500));

        assertThatThrownBy(() -> pagoService.crear(new CrearPagoRequest(2L, "TARJETA")))
                .isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("consultar el carrito");

        when(carritoClient.obtenerResumen(3L)).thenReturn(new ResumenCarritoResponse("Cliente", List.of(), 20000, 3L));
        when(pagoRepository.existsByCodigoTransaccion(any())).thenReturn(false);
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));
        org.mockito.Mockito.doThrow(feignStatus(500)).when(carritoClient).vaciarPorUsuario(3L);

        assertThatThrownBy(() -> pagoService.crear(new CrearPagoRequest(3L, "TARJETA")))
                .isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("vaciar el carrito");
    }

    @Test
    void obtenerNombreUsuarioUsaFallbacks() {
        Pago sinNombre = pago(1L, 2L, "APROBADO");
        Pago noEncontrado = pago(2L, 3L, "APROBADO");
        Pago noDisponible = pago(3L, 4L, "APROBADO");
        when(pagoRepository.findAll()).thenReturn(List.of(sinNombre, noEncontrado, noDisponible));
        when(usuarioClient.buscarPorId(2L)).thenReturn(usuario(" ", null, "cliente@tiendajuegos.cl"));
        when(usuarioClient.buscarPorId(3L)).thenThrow(feignStatus(404));
        when(usuarioClient.buscarPorId(4L)).thenThrow(feignStatus(500));

        List<PagoResponse> responses = pagoService.listar();

        assertThat(responses).extracting(PagoResponse::nombreUsuario)
                .containsExactly("cliente@tiendajuegos.cl", "Usuario no encontrado", "Usuario no disponible");
    }

    @Test
    void pagoLifecycleCompletaEstadoYFecha() {
        Pago pago = pago(1L, 2L, null);
        pago.setFechaPago(null);

        pago.antesDeGuardar();

        assertThat(pago.getEstado()).isEqualTo("APROBADO");
        assertThat(pago.getFechaPago()).isNotNull();
    }

    private Pago pago(Long id, Long usuarioId, String estado) {
        Pago pago = new Pago();
        pago.setId(id);
        pago.setUsuarioId(usuarioId);
        pago.setMonto(19990);
        pago.setMetodoPago("TARJETA");
        pago.setEstado(estado);
        pago.setCodigoTransaccion("PAY-ABC12345");
        return pago;
    }

    private UsuarioResponse usuario(String nombre, String apellido, String correo) {
        return new UsuarioResponse(2L, nombre, apellido, correo, null, null, "CLIENTE", true, null);
    }

    private FeignException feignStatus(int status) {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/test",
                Map.of(),
                null,
                StandardCharsets.UTF_8,
                null);
        Response response = Response.builder()
                .status(status)
                .reason("test")
                .request(request)
                .headers(Map.of())
                .build();
        return FeignException.errorStatus("test", response);
    }
}
