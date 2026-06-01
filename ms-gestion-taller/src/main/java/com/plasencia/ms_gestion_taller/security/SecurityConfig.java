package com.plasencia.ms_gestion_taller.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.stream.Collectors;

/**
 * Seguridad del microservicio compuesto de talleres.
 *  - Lecturas (GET de talleres e inscripciones): cualquier autenticado.
 *  - Inscribir / cancelar inscripcion: ALUMNO o ADMIN.
 *  - Crear / editar / eliminar talleres e inscripciones: INSTRUCTOR o ADMIN.
 * El orden de los matchers importa: lo mas especifico va primero.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        // Lecturas: cualquier usuario autenticado
                        .requestMatchers(HttpMethod.GET, "/api/talleres/**", "/api/inscripciones/**")
                                .authenticated()
                        // Inscribir / cancelar inscripcion (el alumno se inscribe a si mismo)
                        .requestMatchers(HttpMethod.POST, "/api/talleres/*/inscribir/**")
                                .hasAnyRole("ALUMNO", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/talleres/*/inscribir/**")
                                .hasAnyRole("ALUMNO", "ADMIN")
                        // Gestion de talleres
                        .requestMatchers("/api/talleres/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                        // Gestion directa de inscripciones
                        .requestMatchers("/api/inscripciones/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        // 401: no autenticado (sin token o token invalido/expirado)
                        .authenticationEntryPoint(puntoEntradaNoAutenticado())
                        // 403: autenticado pero el rol no tiene permiso
                        .accessDeniedHandler(manejadorAccesoDenegado()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** 401 con cuerpo JSON cuando falta el token o no es valido. */
    @Bean
    public AuthenticationEntryPoint puntoEntradaNoAutenticado() {
        return (request, response, authException) -> escribirJson(
                response,
                HttpStatus.UNAUTHORIZED,
                "No autenticado",
                "Debes iniciar sesion y enviar un token valido en la cabecera Authorization.",
                request.getRequestURI());
    }

    /** 403 con cuerpo JSON que indica el rol del usuario y por que no tiene acceso. */
    @Bean
    public AccessDeniedHandler manejadorAccesoDenegado() {
        return (request, response, accessDeniedException) -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            String usuario = (auth != null) ? auth.getName() : "desconocido";
            String roles = (auth != null)
                    ? auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(", "))
                    : "sin rol";

            String mensaje = String.format(
                    "El usuario '%s' con rol [%s] no tiene permiso para %s %s",
                    usuario, roles, request.getMethod(), request.getRequestURI());

            escribirJson(response, HttpStatus.FORBIDDEN, "Acceso denegado",
                    mensaje, request.getRequestURI());
        };
    }

    /** Escribe la respuesta de error en el mismo formato que ManejadorGlobalExcepciones. */
    private void escribirJson(jakarta.servlet.http.HttpServletResponse response,
                              HttpStatus estado, String error, String mensaje, String ruta)
            throws java.io.IOException {
        response.setStatus(estado.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        String cuerpo = String.format(
                "{\"fecha\":\"%s\",\"estado\":%d,\"error\":\"%s\",\"mensaje\":\"%s\",\"ruta\":\"%s\"}",
                java.time.LocalDateTime.now(), estado.value(), error, mensaje, ruta);
        response.getWriter().write(cuerpo);
    }
}
