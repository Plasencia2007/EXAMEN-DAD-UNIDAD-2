package com.plasencia.ms_seguridad.service;

import com.plasencia.ms_seguridad.entity.Permiso;
import com.plasencia.ms_seguridad.entity.Rol;
import com.plasencia.ms_seguridad.exception.RecursoNoEncontradoException;
import com.plasencia.ms_seguridad.repository.PermisoRepository;
import com.plasencia.ms_seguridad.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;

    public List<Rol> listar() {
        return rolRepository.findAll();
    }

    public Rol buscarPorId(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontro el rol con id " + id));
    }

    public Rol guardar(Rol rol) {
        return rolRepository.save(rol);
    }

    public Rol actualizar(Long id, Rol datos) {
        Rol rol = buscarPorId(id);
        rol.setNombre(datos.getNombre());
        rol.setDescripcion(datos.getDescripcion());
        return rolRepository.save(rol);
    }

    public void eliminar(Long id) {
        rolRepository.delete(buscarPorId(id));
    }

    /** Reemplaza el conjunto de permisos del rol por los indicados. */
    @Transactional
    public Rol asignarPermisos(Long idRol, List<Long> idsPermisos) {
        Rol rol = buscarPorId(idRol);
        idsPermisos.forEach(idPermiso -> {
            Permiso permiso = permisoRepository.findById(idPermiso)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No se encontro el permiso con id " + idPermiso));
            rol.getPermisos().add(permiso);
        });
        return rolRepository.save(rol);
    }
}
