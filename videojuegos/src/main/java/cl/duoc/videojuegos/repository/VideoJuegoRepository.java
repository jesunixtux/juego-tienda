package cl.duoc.videojuegos.repository;

import cl.duoc.videojuegos.model.VideoJuego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoJuegoRepository extends JpaRepository <VideoJuego,Long>{

    List<VideoJuego> findByNombreContainingIgnoreCase(String nombre);

    List<VideoJuego> findByCategoriaIgnoreCase(String categoria);

    List<VideoJuego> findByPlataformaIgnoreCase(String plataforma);

    List<VideoJuego> findByPrecioBetween(Integer precioMin, Integer precioMax);

    List<VideoJuego> findByPrecioGreaterThanEqual(Integer precioMin);

    List<VideoJuego> findByPrecioLessThanEqual(Integer precioMax);

}
