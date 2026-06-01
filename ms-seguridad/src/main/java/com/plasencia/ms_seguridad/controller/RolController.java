package com.plasencia.ms_seguridad.controller;

import com.plasencia.ms_seguridad.entity.Rol;
import com.plasencia.ms_seguridad.service.RolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Gestion de roles y asignacion de permisos (solo ADMIN)")
// Respuestas de error transversales a todos los endpoints del controlador.
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "No autenticado (falta o caduco el JWT)",
                content = @Content),
        @ApiResponse(responseCode = "403", description = "Autenticado pero sin rol ADMIN",
                content = @Content)
})
public class RolController {

    private final RolService rolService;

    @Operation(summary = "Listar todos los roles")
    @ApiResponse(responseCode = "200", description = "Lista de roles",
            content = @Content(schema = @Schema(implementation = Rol.class)))
    @GetMapping
    public ResponseEntity<List<Rol>> listar() {
        return ResponseEntity.ok(rolService.listar());
    }

    @Operation(summary = "Buscar un rol por su id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rol encontrado",
                    content = @Content(schema = @Schema(implementation = Rol.class))),
            @ApiResponse(responseCode = "404", description = "No existe un rol con ese id",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Rol> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(rolService.buscarPorId(id));
    }

    @Operation(summary = "Crear un nuevo rol")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rol creado",
                    content = @Content(schema = @Schema(implementation = Rol.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos (validacion del body)",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<Rol> crear(@Valid @RequestBody Rol rol) {
        return new ResponseEntity<>(rolService.guardar(rol), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un rol existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rol actualizado",
                    content = @Content(schema = @Schema(implementation = Rol.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos (validacion del body)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un rol con ese id",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Rol> actualizar(@PathVariable Long id, @Valid @RequestBody Rol rol) {
        return ResponseEntity.ok(rolService.actualizar(id, rol));
    }

    @Operation(summary = "Eliminar un rol")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Rol eliminado (sin contenido)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un rol con ese id",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        rolService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /** Asigna una lista de permisos (por id) al rol: { "ids": [1,2,3] }. */
    @Operation(summary = "Asignar permisos a un rol",
            description = "Recibe la lista de ids de permisos, por ejemplo: [1,2,3].")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permisos asignados",
                    content = @Content(schema = @Schema(implementation = Rol.class))),
            @ApiResponse(responseCode = "404", description = "No existe el rol o algun permiso indicado",
                    content = @Content)
    })
    @PostMapping("/{id}/permisos")
    public ResponseEntity<Rol> asignarPermisos(@PathVariable Long id,
                                               @RequestBody List<Long> idsPermisos) {
        return ResponseEntity.ok(rolService.asignarPermisos(id, idsPermisos));
    }
}
