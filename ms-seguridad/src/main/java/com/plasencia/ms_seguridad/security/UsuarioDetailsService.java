package com.plasencia.ms_seguridad.security;

import com.plasencia.ms_seguridad.entity.Usuario;
import com.plasencia.ms_seguridad.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No existe el usuario: " + username));

        Set<GrantedAuthority> autoridades = new HashSet<>();
        usuario.getRoles().forEach(rol -> {
            autoridades.add(new SimpleGrantedAuthority(rol.getNombre()));
            rol.getPermisos().forEach(p -> autoridades.add(new SimpleGrantedAuthority(p.getNombre())));
        });

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .disabled(!usuario.isActivo())
                .authorities(autoridades)
                .build();
    }
}
