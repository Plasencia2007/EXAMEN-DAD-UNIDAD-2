package com.plasencia.ms_gestion_alumno.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@Component
public class JwtUtil {

    private final SecretKey clave;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.clave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public boolean esValido(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extraerUsername(String token) {
        return parse(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extraerAutoridades(String token) {
        Claims claims = parse(token);
        List<String> roles = claims.get("roles", List.class);
        List<String> permisos = claims.get("permisos", List.class);
        List<String> autoridades = new ArrayList<>();
        if (roles != null) autoridades.addAll(roles);
        if (permisos != null) autoridades.addAll(permisos);
        return autoridades;
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
