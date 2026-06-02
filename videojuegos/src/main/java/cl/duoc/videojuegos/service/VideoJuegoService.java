package cl.duoc.videojuegos.service;

import cl.duoc.videojuegos.exception.PlataformaException;
import cl.duoc.videojuegos.model.VideoJuego;
import cl.duoc.videojuegos.repository.VideoJuegoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;


@Service
public class VideoJuegoService {
    private static final List<String> PLATAFORMAS_PERMITIDAS = List.of(
            "PS5",
            "PS4",
            "XBOX",
            "PC",
            "PLAYSTATION",
            "PLAYSTATION 5",
            "PLAYSTATION 4",
            "XBOX SERIES X",
            "XBOX SERIES S",
            "NINTENDO SWITCH",
            "PC VR");
    private static final String PLATAFORMAS_VISIBLES = "PS5, PS4, XBOX, PC, PlayStation, Nintendo Switch, PC VR";

    private final VideoJuegoRepository videoJuegoRepository;

    public VideoJuegoService(VideoJuegoRepository videoJuegoRepository) {
        this.videoJuegoRepository = videoJuegoRepository;
    }

    public List<VideoJuego> listar() {
        return videoJuegoRepository.findAll();
    }

    public Optional<VideoJuego> buscarPorId(Long id) {
        return videoJuegoRepository.findById(id);
    }

    public VideoJuego crear(VideoJuego videoJuego) {
        validarPlataforma(videoJuego);
        if (videoJuego.getActivo() == null) {
            videoJuego.setActivo(true);
        }

        return videoJuegoRepository.save(videoJuego);
    }

    public Optional<VideoJuego> actualizar(Long id, VideoJuego datosVideoJuego) {
        return videoJuegoRepository.findById(id)
                .map(videoJuego -> {
                    validarPlataforma(datosVideoJuego);
                    videoJuego.setNombre(datosVideoJuego.getNombre());
                    videoJuego.setCategoria(datosVideoJuego.getCategoria());
                    videoJuego.setPrecio(datosVideoJuego.getPrecio());
                    videoJuego.setPlataforma(datosVideoJuego.getPlataforma());
                    videoJuego.setDescripcion(datosVideoJuego.getDescripcion());
                    videoJuego.setDesarrollador(datosVideoJuego.getDesarrollador());
                    videoJuego.setFechaLanzamiento(datosVideoJuego.getFechaLanzamiento());
                    videoJuego.setImagenUrl(datosVideoJuego.getImagenUrl());
                    videoJuego.setActivo(datosVideoJuego.getActivo() == null ? true : datosVideoJuego.getActivo());

                    return videoJuegoRepository.save(videoJuego);
                });
    }

    public boolean eliminar(Long id) {
        if (!videoJuegoRepository.existsById(id)) {
            return false;
        }

        videoJuegoRepository.deleteById(id);
        return true;
    }

    public List<VideoJuego> buscarPorNombre(String nombre) {
        return videoJuegoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<VideoJuego> buscarPorCategoria(String categoria) {
        return videoJuegoRepository.findByCategoriaIgnoreCase(categoria);
    }

    public List<VideoJuego> buscarPorPlataforma(String plataforma) {
        return videoJuegoRepository.findByPlataformaIgnoreCase(plataforma);
    }

    public List<VideoJuego> buscarPorPrecio(Integer precioMin, Integer precioMax) {
        if (precioMin != null && precioMax != null) {
            return videoJuegoRepository.findByPrecioBetween(precioMin, precioMax);
        }

        if (precioMin != null) {
            return videoJuegoRepository.findByPrecioGreaterThanEqual(precioMin);
        }

        return videoJuegoRepository.findByPrecioLessThanEqual(precioMax);
    }

    private void validarPlataforma(VideoJuego videoJuego) {
        String plataforma = videoJuego.getPlataforma();
        if (plataforma == null || plataforma.isBlank()) {
            return;
        }

        String plataformaLimpia = plataforma.trim();
        String plataformaNormalizada = plataformaLimpia.toUpperCase(Locale.ROOT);
        if (!PLATAFORMAS_PERMITIDAS.contains(plataformaNormalizada)) {
            throw new PlataformaException("Plataforma no valida. Plataformas permitidas: " + PLATAFORMAS_VISIBLES);
        }

        videoJuego.setPlataforma(plataformaLimpia);
    }
}
