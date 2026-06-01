package com.plasencia.ms_gestion_taller.controller;

import com.plasencia.ms_gestion_taller.dto.TallerDetalleDTO;
import com.plasencia.ms_gestion_taller.entity.Inscripcion;
import com.plasencia.ms_gestion_taller.entity.Taller;
import com.plasencia.ms_gestion_taller.service.TallerService;
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
@RequestMapping("/api/talleres")
@RequiredArgsConstructor
public class TallerController {

    private final TallerService tallerService;

    // ---------- CRUD ----------

    @GetMapping
    public ResponseEntity<List<Taller>> listar() {
        return ResponseEntity.ok(tallerService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Taller> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tallerService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Taller> crear(@Valid @RequestBody Taller taller) {
        return new ResponseEntity<>(tallerService.crear(taller), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Taller> actualizar(@PathVariable Long id,
                                             @Valid @RequestBody Taller taller) {
        return ResponseEntity.ok(tallerService.actualizar(id, taller));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tallerService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- Vista compuesta y inscripciones ----------

    @GetMapping("/{id}/detalle")
    public ResponseEntity<TallerDetalleDTO> obtenerDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(tallerService.obtenerDetalle(id));
    }

    @PostMapping("/{idTaller}/inscribir/{idAlumno}")
    public ResponseEntity<Inscripcion> inscribirAlumno(@PathVariable Long idTaller,
                                                       @PathVariable Long idAlumno) {
        Inscripcion inscripcion = tallerService.inscribirAlumno(idTaller, idAlumno);
        return new ResponseEntity<>(inscripcion, HttpStatus.CREATED);
    }

    @DeleteMapping("/{idTaller}/inscribir/{idAlumno}")
    public ResponseEntity<Void> cancelarInscripcion(@PathVariable Long idTaller,
                                                    @PathVariable Long idAlumno) {
        tallerService.cancelarInscripcion(idTaller, idAlumno);
        return ResponseEntity.noContent().build();
    }
}
