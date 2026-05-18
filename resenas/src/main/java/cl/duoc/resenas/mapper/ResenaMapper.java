package cl.duoc.resenas.mapper;

import cl.duoc.resenas.dto.ResenaDTO;
import cl.duoc.resenas.dto.UsuarioDTO;
import cl.duoc.resenas.model.Resena;
import org.springframework.stereotype.Component;

@Component
public class ResenaMapper {

    public ResenaDTO toDTO(Resena resena, UsuarioDTO usuario) {
        ResenaDTO dto = new ResenaDTO();

        dto.setUsuarioId(resena.getUsuarioId());
        dto.setNombreJuego(resena.getNombreJuego());
        dto.setComentario(resena.getComentario());
        dto.setPuntuacion(resena.getPuntuacion());
        dto.setFechaResena(resena.getFechaResena());

        if (usuario != null) {
            dto.setNombreUsuario(usuario.getNombre() + " " + usuario.getApellido());
            dto.setCorreoUsuario(usuario.getCorreo());
        }

        return dto;
    }
}
