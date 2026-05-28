package cl.duoc.videojuegos.service;

import cl.duoc.videojuegos.model.VideoJuego;
import cl.duoc.videojuegos.repository.VideoJuegoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class VideoJuegoService {
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
        if (videoJuego.getActivo() == null) {
            videoJuego.setActivo(true);
        }

        return videoJuegoRepository.save(videoJuego);
    }

    public Optional<VideoJuego> actualizar(Long id, VideoJuego datosVideoJuego) {
        return videoJuegoRepository.findById(id)
                .map(videoJuego -> {
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
}
