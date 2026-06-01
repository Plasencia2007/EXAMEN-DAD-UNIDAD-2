package com.plasencia.ms_seguridad.controller;

import com.plasencia.ms_seguridad.entity.Permiso;
import com.plasencia.ms_seguridad.service.PermisoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/permisos")
@RequiredArgsConstructor
public class PermisoController {

    private final PermisoService permisoService;

    @GetMapping
    public ResponseEntity<List<Permiso>> listar() {
        return ResponseEntity.ok(permisoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Permiso> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(permisoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Permiso> crear(@Valid @RequestBody Permiso permiso) {
        return new ResponseEntity<>(permisoService.guardar(permiso), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Permiso> actualizar(@PathVariable Long id,
                                              @Valid @RequestBody Permiso permiso) {
        return ResponseEntity.ok(permisoService.actualizar(id, permiso));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        permisoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
