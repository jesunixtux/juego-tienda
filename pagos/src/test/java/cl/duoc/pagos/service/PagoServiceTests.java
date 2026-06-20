package cl.duoc.pagos.service;

import cl.duoc.pagos.client.CarritoClient;
import cl.duoc.pagos.client.UsuarioClient;
import cl.duoc.pagos.dto.CrearPagoRequest;
import cl.duoc.pagos.dto.PagoResponse;
import cl.duoc.pagos.dto.ResumenCarritoResponse;
import cl.duoc.pagos.dto.UsuarioResponse;
import cl.duoc.pagos.exception.ConflictException;
import cl.duoc.pagos.model.Pago;
import cl.duoc.pagos.repository.PagoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagoServiceTests {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private CarritoClient carritoClient;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private PagoService pagoService;

    @Test
    void crearGeneraPagoAprobadoYVaciaCarrito() {
        when(carritoClient.obtenerResumen(2L)).thenReturn(new ResumenCarritoResponse("Jesus Emilio", List.of(), 39980, 2L));
        when(pagoRepository.existsByCodigoTransaccion(any())).thenReturn(false);
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> {
            Pago pago = invocation.getArgument(0);
            pago.setId(7L);
            pago.antesDeGuardar();
            return pago;
        });

        PagoResponse response = pagoService.crear(new CrearPagoRequest(2L, "Tarjeta"));

        assertThat(response.nombreUsuario()).isEqualTo("Jesus Emilio");
        assertThat(response.monto()).isEqualTo(39980);
        assertThat(response.estado()).isEqualTo("APROBADO");
        assertThat(response.codigoTransaccion()).startsWith("PAY-");
        verify(carritoClient).vaciarPorUsuario(2L);
    }

    @Test
    void crearRechazaCarritoVacio() {
        when(carritoClient.obtenerResumen(2L)).thenReturn(new ResumenCarritoResponse("Jesus Emilio", List.of(), 0, 2L));

        assertThatThrownBy(() -> pagoService.crear(new CrearPagoRequest(2L, "Tarjeta")))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("carrito no tiene productos");
    }

    @Test
    void listarEnriqueceNombreUsuarioDesdeMicroservicioUsuarios() {
        Pago pago = new Pago();
        pago.setId(1L);
        pago.setUsuarioId(2L);
        pago.setMonto(19990);
        pago.setMetodoPago("Debito");
        pago.setEstado("APROBADO");
        pago.setCodigoTransaccion("PAY-ABC12345");

        when(pagoRepository.findAll()).thenReturn(List.of(pago));
        when(usuarioClient.buscarPorId(2L)).thenReturn(new UsuarioResponse(
                2L,
                "Jesus",
                "Emilio",
                "jesus@tiendajuegos.cl",
                null,
                null,
                "CLIENTE",
                true,
                null));

        List<PagoResponse> pagos = pagoService.listar();

        assertThat(pagos).hasSize(1);
        assertThat(pagos.getFirst().nombreUsuario()).isEqualTo("Jesus Emilio");
    }
}
