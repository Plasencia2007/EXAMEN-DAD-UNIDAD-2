package com.plasencia.ms_seguridad.config;

import com.plasencia.ms_seguridad.security.JwtService;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

/**
 * Adjunta un token de servicio (rol ADMIN, corta vida) a todas las llamadas
 * Feign que hace ms-seguridad. Asi puede crear la ficha del usuario en
 * instructor/alumno durante el registro, aunque el usuario aun no tenga token.
 */
@Configuration
@RequiredArgsConstructor
public class FeignClientConfig {

    private final JwtService jwtService;

    @Bean
    public RequestInterceptor tokenServicioInterceptor() {
        return template -> template.header(
                HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.generarTokenServicio());
    }
}
