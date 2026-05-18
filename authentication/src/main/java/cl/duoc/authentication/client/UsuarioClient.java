package cl.duoc.authentication.client;

import cl.duoc.authentication.dto.UsuarioRequest;
import cl.duoc.authentication.dto.UsuarioResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "usuarios")
public interface UsuarioClient {

    @PostMapping("/usuarios")
    UsuarioResponse crear(@RequestBody UsuarioRequest request);

    @GetMapping("/usuarios/buscar")
    UsuarioResponse buscarPorCorreo(@RequestParam("correo") String correo);
}
