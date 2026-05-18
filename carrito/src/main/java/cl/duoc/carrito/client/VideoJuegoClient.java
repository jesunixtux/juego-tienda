package cl.duoc.carrito.client;

import cl.duoc.carrito.dto.VideoJuegoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "videojuegos")
public interface VideoJuegoClient {

    @GetMapping("/videojuegos/{id}")
    VideoJuegoResponse buscarPorId(@PathVariable("id") Long id);
}
