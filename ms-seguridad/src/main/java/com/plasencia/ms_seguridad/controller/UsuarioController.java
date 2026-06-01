package com.plasencia.ms_seguridad.controller;

import com.plasencia.ms_seguridad.entity.Usuario;
import com.plasencia.ms_seguridad.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestion de usuarios (solo ADMIN)")
// Respuestas de error transversales a todos los endpoints del controlador.
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "No autenticado (falta o caduco el JWT)",
                content = @Content),
        @ApiResponse(responseCode = "403", description = "Autenticado pero sin rol ADMIN",
                content = @Content)
})
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Listar todos los usuarios")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios",
            content = @Content(schema = @Schema(implementation = Usuario.class)))
    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @Operation(summary = "Buscar un usuario por su id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con ese id",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @Operation(summary = "Eliminar un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado (sin contenido)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con ese id",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activar o desactivar un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado",
                    content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con ese id",
                    content = @Content)
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Usuario> cambiarEstado(@PathVariable Long id,
                                                 @RequestParam boolean activo) {
        return ResponseEntity.ok(usuarioService.cambiarEstado(id, activo));
    }

    /** Asigna una lista de roles (por id) al usuario: [1,2]. */
    @Operation(summary = "Asignar roles a un usuario",
            description = "Recibe la lista de ids de roles, por ejemplo: [1,2].")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Roles asignados",
                    content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "No existe el usuario o algun rol indicado",
                    content = @Content)
    })
    @PostMapping("/{id}/roles")
    public ResponseEntity<Usuario> asignarRoles(@PathVariable Long id,
                                                @RequestBody List<Long> idsRoles) {
        return ResponseEntity.ok(usuarioService.asignarRoles(id, idsRoles));
    }
}
