package com.plasencia.ms_gestion_taller.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Propaga la cabecera Authorization (el JWT del usuario) a las llamadas Feign
 * hacia ms-gestion-instructor y ms-gestion-alumno. Sin esto, esos servicios
 * rechazarian las peticiones internas por falta de token.
 */
@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor propagarTokenInterceptor() {
        return template -> {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                String authorization = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
                if (authorization != null && !authorization.isBlank()) {
                    template.header(HttpHeaders.AUTHORIZATION, authorization);
                }
            }
        };
    }
}
