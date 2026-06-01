package com.plasencia.ms_seguridad.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String tipo;
    private Long usuarioId;
    private String username;
    private List<String> roles;
    private List<String> permisos;
    private Long idReferencia;
    private String tipoFicha;
    private long expiraEnSegundos;
}
