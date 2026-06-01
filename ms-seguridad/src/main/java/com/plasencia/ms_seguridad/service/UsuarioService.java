package com.plasencia.ms_seguridad.service;

import com.plasencia.ms_seguridad.entity.Rol;
import com.plasencia.ms_seguridad.entity.Usuario;
import com.plasencia.ms_seguridad.exception.RecursoNoEncontradoException;
import com.plasencia.ms_seguridad.repository.RolRepository;
import com.plasencia.ms_seguridad.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontro el usuario con id " + id));
    }

    public void eliminar(Long id) {
        usuarioRepository.delete(buscarPorId(id));
    }

    /** Activa o desactiva la cuenta (sin borrarla). */
    public Usuario cambiarEstado(Long id, boolean activo) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(activo);
        return usuarioRepository.save(usuario);
    }

    /** Reemplaza los roles del usuario por los indicados (por id). */
    @Transactional
    public Usuario asignarRoles(Long idUsuario, List<Long> idsRoles) {
        Usuario usuario = buscarPorId(idUsuario);
        Set<Rol> roles = new HashSet<>();
        idsRoles.forEach(idRol -> {
            Rol rol = rolRepository.findById(idRol)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No se encontro el rol con id " + idRol));
            roles.add(rol);
        });
        usuario.setRoles(roles);
        return usuarioRepository.save(usuario);
    }
}
