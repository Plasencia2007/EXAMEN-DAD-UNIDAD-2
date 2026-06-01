package com.plasencia.ms_seguridad.config;

import com.plasencia.ms_seguridad.entity.Permiso;
import com.plasencia.ms_seguridad.entity.Rol;
import com.plasencia.ms_seguridad.entity.Usuario;
import com.plasencia.ms_seguridad.repository.PermisoRepository;
import com.plasencia.ms_seguridad.repository.RolRepository;
import com.plasencia.ms_seguridad.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Crea los permisos, roles (ROLE_ALUMNO, ROLE_INSTRUCTOR, ROLE_ADMIN) y un
 * usuario administrador inicial la primera vez que arranca el servicio.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PermisoRepository permisoRepository;
    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (rolRepository.count() > 0) {
            return; // ya inicializado
        }

        // 1) Permisos
        Map<String, Permiso> permisos = new HashMap<>();
        crearPermiso(permisos, "taller:listar", "Ver el listado de talleres");
        crearPermiso(permisos, "taller:crear", "Crear talleres");
        crearPermiso(permisos, "taller:editar", "Editar talleres");
        crearPermiso(permisos, "taller:eliminar", "Eliminar talleres");
        crearPermiso(permisos, "inscripcion:gestionar", "Inscribir y cancelar inscripciones");
        crearPermiso(permisos, "alumno:gestionar", "Gestionar alumnos");
        crearPermiso(permisos, "instructor:gestionar", "Gestionar instructores");
        crearPermiso(permisos, "usuario:gestionar", "Gestionar usuarios, roles y permisos");

        // 2) Roles con sus permisos
        Rol rolAlumno = rolRepository.save(Rol.builder()
                .nombre("ROLE_ALUMNO")
                .descripcion("Alumno de la academia")
                .permisos(Set.of(
                        permisos.get("taller:listar"),
                        permisos.get("inscripcion:gestionar")))
                .build());

        Rol rolInstructor = rolRepository.save(Rol.builder()
                .nombre("ROLE_INSTRUCTOR")
                .descripcion("Instructor que dicta talleres")
                .permisos(Set.of(
                        permisos.get("taller:listar"),
                        permisos.get("taller:crear"),
                        permisos.get("taller:editar"),
                        permisos.get("taller:eliminar"),
                        permisos.get("alumno:gestionar")))
                .build());

        Rol rolAdmin = rolRepository.save(Rol.builder()
                .nombre("ROLE_ADMIN")
                .descripcion("Administrador del sistema")
                .permisos(Set.copyOf(permisos.values()))
                .build());

        // 3) Usuario administrador inicial
        usuarioRepository.save(Usuario.builder()
                .username("admin")
                .correo("admin@academia.com")
                .password(passwordEncoder.encode("admin123"))
                .activo(true)
                .roles(Set.of(rolAdmin))
                .build());
    }

    private void crearPermiso(Map<String, Permiso> destino, String nombre, String descripcion) {
        Permiso permiso = permisoRepository.save(Permiso.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .build());
        destino.put(nombre, permiso);
    }
}
