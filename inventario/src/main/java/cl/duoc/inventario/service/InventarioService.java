package cl.duoc.inventario.service;

import cl.duoc.inventario.client.VideoJuegoClient;
import cl.duoc.inventario.dto.ActualizarStockRequest;
import cl.duoc.inventario.dto.CrearInventarioRequest;
import cl.duoc.inventario.dto.MovimientoStockRequest;
import cl.duoc.inventario.model.Inventario;
import cl.duoc.inventario.repository.InventarioRepository;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class InventarioService {
    private final InventarioRepository inventarioRepository;
    private final VideoJuegoClient videoJuegoClient;

    public InventarioService(InventarioRepository inventarioRepository, VideoJuegoClient videoJuegoClient) {
        this.inventarioRepository = inventarioRepository;
        this.videoJuegoClient = videoJuegoClient;
    }

    public List<Inventario> listar() {
        return inventarioRepository.findAll();
    }

    public List<Inventario> listarBajoStock() {
        return inventarioRepository.findAll().stream()
                .filter(inventario -> inventario.getStock() <= inventario.getStockMinimo())
                .toList();
    }

    public Optional<Inventario> buscarPorId(Long id) {
        return inventarioRepository.findById(id);
    }

    public Optional<Inventario> buscarPorVideojuego(Long videojuegoId) {
        return inventarioRepository.findByVideojuegoId(videojuegoId);
    }

    @Transactional
    public Inventario crear(CrearInventarioRequest request) {
        validarVideojuegoExiste(request.videojuegoId());

        if (inventarioRepository.existsByVideojuegoId(request.videojuegoId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe inventario para este videojuego");
        }

        Inventario inventario = new Inventario();
        inventario.setVideojuegoId(request.videojuegoId());
        inventario.setStock(request.stock());
        inventario.setStockMinimo(request.stockMinimo());

        return inventarioRepository.save(inventario);
    }

    @Transactional
    public Optional<Inventario> actualizar(Long id, CrearInventarioRequest request) {
        validarVideojuegoExiste(request.videojuegoId());
        validarVideojuegoDisponibleParaActualizar(id, request.videojuegoId());

        return inventarioRepository.findById(id)
                .map(inventario -> {
                    inventario.setVideojuegoId(request.videojuegoId());
                    inventario.setStock(request.stock());
                    inventario.setStockMinimo(request.stockMinimo());

                    return inventarioRepository.save(inventario);
                });
    }

    @Transactional
    public Optional<Inventario> actualizarStockPorVideojuego(Long videojuegoId, ActualizarStockRequest request) {
        return inventarioRepository.findByVideojuegoId(videojuegoId)
                .map(inventario -> {
                    inventario.setStock(request.stock());
                    return inventarioRepository.save(inventario);
                });
    }

    @Transactional
    public Optional<Inventario> aumentarStock(Long videojuegoId, MovimientoStockRequest request) {
        return inventarioRepository.findByVideojuegoId(videojuegoId)
                .map(inventario -> {
                    inventario.setStock(inventario.getStock() + request.cantidad());
                    return inventarioRepository.save(inventario);
                });
    }

    @Transactional
    public Optional<Inventario> disminuirStock(Long videojuegoId, MovimientoStockRequest request) {
        return inventarioRepository.findByVideojuegoId(videojuegoId)
                .map(inventario -> {
                    if (inventario.getStock() < request.cantidad()) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuficiente");
                    }

                    inventario.setStock(inventario.getStock() - request.cantidad());
                    return inventarioRepository.save(inventario);
                });
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (!inventarioRepository.existsById(id)) {
            return false;
        }

        inventarioRepository.deleteById(id);
        return true;
    }

    private void validarVideojuegoExiste(Long videojuegoId) {
        try {
            videoJuegoClient.buscarPorId(videojuegoId);
        } catch (FeignException.NotFound exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Videojuego no encontrado");
        } catch (FeignException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo consultar el microservicio videojuegos");
        }
    }

    private void validarVideojuegoDisponibleParaActualizar(Long inventarioId, Long videojuegoId) {
        inventarioRepository.findByVideojuegoId(videojuegoId)
                .filter(inventario -> !inventario.getId().equals(inventarioId))
                .ifPresent(inventario -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe inventario para este videojuego");
                });
    }
}
