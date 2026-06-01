package com.plasencia.ms_admin_api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

        @Bean
        public RouteLocator rutas(RouteLocatorBuilder builder) {
                return builder.routes()
                                // Microservicio de seguridad (login/register + usuarios/roles/permisos)
                                .route("ms-seguridad", r -> r
                                                .path("/api/auth/**", "/api/usuarios/**",
                                                                "/api/roles/**", "/api/permisos/**")
                                                .uri("lb://ms-seguridad"))
                                // Microservicio de instructores
                                .route("ms-gestion-instructor", r -> r
                                                .path("/api/instructores/**")
                                                .uri("lb://ms-gestion-instructor"))
                                // Microservicio de alumnos
                                .route("ms-gestion-alumno", r -> r
                                                .path("/api/alumnos/**")
                                                .uri("lb://ms-gestion-alumno"))
                                // Microservicio compuesto de talleres
                                .route("ms-gestion-taller", r -> r
                                                .path("/api/talleres/**", "/api/inscripciones/**")
                                                .uri("lb://ms-gestion-taller"))
                                .build();
        }
}
