package cl.duoc.resenas.service;

import cl.duoc.resenas.client.UsuarioFeign;
import cl.duoc.resenas.dto.ResenaDTO;
import cl.duoc.resenas.dto.UsuarioDTO;
import cl.duoc.resenas.mapper.ResenaMapper;
import cl.duoc.resenas.model.Resena;
import cl.duoc.resenas.repository.ResenaRepository;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ResenaService {
    private final ResenaRepository resenaRepository;
    private final UsuarioFeign usuarioFeign;
    private final ResenaMapper resenaMapper;

    public ResenaService(ResenaRepository resenaRepository, UsuarioFeign usuarioFeign, ResenaMapper resenaMapper) {
        this.resenaRepository = resenaRepository;
        this.usuarioFeign = usuarioFeign;
        this.resenaMapper = resenaMapper;
    }

    public List<Resena> findAll() {
        return resenaRepository.findAll();
    }

    public Optional<Resena> findById(Long id) {
        return resenaRepository.findById(id);
    }

    @Transactional
    public Resena save(Resena r) {
        validarUsuario(r.getUsuarioId());

        return resenaRepository.save(r);
    }

    @Transactional
    public Optional<Resena> update(Long id, Resena r) {
        validarUsuario(r.getUsuarioId());

        return resenaRepository.findById(id)
                .map(existing -> {
                    existing.setUsuarioId(r.getUsuarioId());
                    existing.setNombreJuego(r.getNombreJuego());
                    existing.setComentario(r.getComentario());
                    existing.setPuntuacion(r.getPuntuacion());
                    existing.setFechaResena(r.getFechaResena());

                    return resenaRepository.save(existing);
                });
    }

    @Transactional
    public boolean delete(Long id) {
        if (!resenaRepository.existsById(id)) {
            return false;
        }

        resenaRepository.deleteById(id);
        return true;
    }

    public List<ResenaDTO> findConUsuario() {

        return resenaRepository.findAll().stream().map(r -> {
            UsuarioDTO usuario = obtenerUsuario(r.getUsuarioId());
            return resenaMapper.toDTO(r, usuario);
        }).toList();
    }

    public List<Resena> findByUsuario(Long usuarioId) {
        return resenaRepository.findByUsuarioId(usuarioId);
    }

    public List<Resena> findByFecha(LocalDate desde, LocalDate hasta) {
        return resenaRepository.findByFechaResenaBetween(desde, hasta);
    }

    public List<Resena> findByPuntuacion(Integer min, Integer max) {
        return resenaRepository.findByPuntuacionBetween(min, max);
    }

    private void validarUsuario(Long usuarioId) {
        obtenerUsuario(usuarioId);
    }

    private UsuarioDTO obtenerUsuario(Long usuarioId) {
        try {
            return usuarioFeign.obtenerUsuario(usuarioId);
        } catch (FeignException.NotFound exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        } catch (FeignException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo consultar el microservicio usuarios");
        }
    }
}
