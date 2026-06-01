package com.plasencia.ms_seguridad.service;

import com.plasencia.ms_seguridad.entity.Permiso;
import com.plasencia.ms_seguridad.exception.RecursoNoEncontradoException;
import com.plasencia.ms_seguridad.repository.PermisoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermisoService {

    private final PermisoRepository permisoRepository;

    public List<Permiso> listar() {
        return permisoRepository.findAll();
    }

    public Permiso buscarPorId(Long id) {
        return permisoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontro el permiso con id " + id));
    }

    public Permiso guardar(Permiso permiso) {
        return permisoRepository.save(permiso);
    }

    public Permiso actualizar(Long id, Permiso datos) {
        Permiso permiso = buscarPorId(id);
        permiso.setNombre(datos.getNombre());
        permiso.setDescripcion(datos.getDescripcion());
        return permisoRepository.save(permiso);
    }

    public void eliminar(Long id) {
        permisoRepository.delete(buscarPorId(id));
    }
}
