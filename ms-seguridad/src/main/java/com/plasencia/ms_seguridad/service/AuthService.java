package com.plasencia.ms_seguridad.service;

import com.plasencia.ms_seguridad.client.AlumnoClient;
import com.plasencia.ms_seguridad.client.InstructorClient;
import com.plasencia.ms_seguridad.dto.AlumnoFichaDTO;
import com.plasencia.ms_seguridad.dto.AuthResponse;
import com.plasencia.ms_seguridad.dto.InstructorFichaDTO;
import com.plasencia.ms_seguridad.dto.LoginRequest;
import com.plasencia.ms_seguridad.dto.RegistroRequest;
import com.plasencia.ms_seguridad.entity.Rol;
import com.plasencia.ms_seguridad.entity.Usuario;
import com.plasencia.ms_seguridad.exception.RecursoNoEncontradoException;
import com.plasencia.ms_seguridad.exception.ReglaNegocioException;
import com.plasencia.ms_seguridad.repository.RolRepository;
import com.plasencia.ms_seguridad.repository.UsuarioRepository;
import com.plasencia.ms_seguridad.security.JwtService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Logica de registro e inicio de sesion. Solo permite auto-registrarse como
 * ALUMNO o INSTRUCTOR; el rol ADMIN se asigna desde el CRUD de usuarios.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final InstructorClient instructorClient;
    private final AlumnoClient alumnoClient;

    @Transactional
    public AuthResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new ReglaNegocioException("El nombre de usuario ya esta en uso");
        }
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new ReglaNegocioException("El correo ya esta registrado");
        }

        String nombreRol = normalizarRol(request.getRol());
        if (!nombreRol.equals("ROLE_ALUMNO") && !nombreRol.equals("ROLE_INSTRUCTOR")) {
            throw new ReglaNegocioException(
                    "Solo puede registrarse como ALUMNO o INSTRUCTOR");
        }

        Rol rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el rol " + nombreRol));

        String tipoFicha = nombreRol.equals("ROLE_INSTRUCTOR") ? "INSTRUCTOR" : "ALUMNO";

        // ============================================================
        // SAGA (orquestada) - el registro abarca DOS bases de datos:
        //   Paso 1: crear la ficha en instructor/alumno (remoto)
        //   Paso 2: crear la cuenta en seguridad (local)
        // Si el Paso 2 falla, se COMPENSA el Paso 1 (se elimina la ficha)
        // para no dejar fichas huerfanas sin cuenta.
        // ============================================================

        // --- Paso 1 ---
        Long idFicha = crearFicha(tipoFicha, request);
        log.info("SAGA registro: ficha {} creada con id {}", tipoFicha, idFicha);

        // --- Paso 2 (con compensacion si falla) ---
        try {
            Usuario usuario = Usuario.builder()
                    .username(request.getUsername())
                    .correo(request.getCorreo())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .activo(true)
                    .idReferencia(idFicha)
                    .tipoFicha(tipoFicha)
                    .roles(Set.of(rol))
                    .build();

            // saveAndFlush fuerza el INSERT ahora (no al cerrar la transaccion),
            // para que cualquier error caiga DENTRO de este try y se pueda compensar.
            usuario = usuarioRepository.saveAndFlush(usuario);
            log.info("SAGA registro: cuenta '{}' creada -> registro completado", usuario.getUsername());
            return construirRespuesta(usuario);

        } catch (RuntimeException ex) {
            // --- COMPENSACION del Paso 1 ---
            compensarFicha(tipoFicha, idFicha);
            throw new ReglaNegocioException(
                    "No se pudo crear la cuenta; se revirtio la ficha asociada (Saga). Detalle: "
                            + ex.getMessage());
        }
    }

    /**
     * Accion compensatoria: elimina la ficha creada en el Paso 1 cuando el
     * registro no se pudo completar. Si la compensacion falla, se registra el
     * problema (en un sistema real dispararia un reintento o una alerta), pero
     * no se oculta el error original del registro.
     */
    private void compensarFicha(String tipoFicha, Long idFicha) {
        try {
            if ("INSTRUCTOR".equals(tipoFicha)) {
                instructorClient.eliminar(idFicha);
            } else {
                alumnoClient.eliminar(idFicha);
            }
            log.warn("SAGA compensacion: ficha {} con id {} eliminada (rollback)", tipoFicha, idFicha);
        } catch (FeignException ex) {
            log.error("SAGA compensacion FALLIDA: no se pudo eliminar la ficha {} id {} -> "
                    + "queda inconsistente y requiere intervencion. Causa: {}",
                    tipoFicha, idFicha, ex.getMessage());
        }
    }

    /** Crea la ficha (instructor o alumno) y devuelve su id generado. */
    private Long crearFicha(String tipoFicha, RegistroRequest request) {
        try {
            if (tipoFicha.equals("INSTRUCTOR")) {
                if (request.getEspecialidad() == null || request.getEspecialidad().isBlank()) {
                    throw new ReglaNegocioException(
                            "La especialidad es obligatoria para registrar un instructor");
                }
                InstructorFichaDTO ficha = instructorClient.crear(InstructorFichaDTO.builder()
                        .nombres(request.getNombres())
                        .apellidos(request.getApellidos())
                        .especialidad(request.getEspecialidad())
                        .correo(request.getCorreo())
                        .telefono(request.getTelefono())
                        .build());
                return ficha.getId();
            } else {
                if (request.getDni() == null || request.getDni().isBlank()) {
                    throw new ReglaNegocioException(
                            "El DNI es obligatorio para registrar un alumno");
                }
                AlumnoFichaDTO ficha = alumnoClient.crear(AlumnoFichaDTO.builder()
                        .nombres(request.getNombres())
                        .apellidos(request.getApellidos())
                        .dni(request.getDni())
                        .correo(request.getCorreo())
                        .telefono(request.getTelefono())
                        .build());
                return ficha.getId();
            }
        } catch (FeignException ex) {
            // Datos rechazados por el otro microservicio (correo/dni duplicado, validacion, etc.)
            throw new ReglaNegocioException(
                    "No se pudo crear la ficha en el microservicio de " + tipoFicha.toLowerCase()
                            + ": " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Lanza BadCredentialsException si usuario/clave no coinciden
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el usuario " + request.getUsername()));

        return construirRespuesta(usuario);
    }

    private AuthResponse construirRespuesta(Usuario usuario) {
        String token = jwtService.generarToken(usuario);
        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .usuarioId(usuario.getId())
                .username(usuario.getUsername())
                .roles(usuario.getRoles().stream().map(Rol::getNombre).toList())
                .permisos(usuario.getRoles().stream()
                        .flatMap(r -> r.getPermisos().stream())
                        .map(p -> p.getNombre())
                        .distinct().sorted().toList())
                .idReferencia(usuario.getIdReferencia())
                .tipoFicha(usuario.getTipoFicha())
                .expiraEnSegundos(jwtService.getExpiracionSegundos())
                .build();
    }

    /** Acepta "alumno", "ALUMNO", "ROLE_ALUMNO" y lo normaliza a "ROLE_ALUMNO". */
    private String normalizarRol(String rol) {
        String limpio = rol.trim().toUpperCase();
        return limpio.startsWith("ROLE_") ? limpio : "ROLE_" + limpio;
    }
}
