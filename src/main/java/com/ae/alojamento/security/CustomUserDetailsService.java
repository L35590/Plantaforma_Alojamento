package com.ae.alojamento.security;

import com.ae.alojamento.entity.User;
import com.ae.alojamento.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Método invocado pelo Spring Security ao tentar fazer login.
     * * @param username O username inserido no formulário de login.
     * @return UserDetails Objeto padrão do Spring com user, pass e permissões.
     * @throws UsernameNotFoundException Se o utilizador não existir na BD.
     */
    
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilizador não encontrado: " + username));

        if (user.getRoles() == null) {
            System.out.println(">>> AVISO: Roles nulas. A assumir lista vazia.");
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    user.isEnabled(), 
                    true, true, true,
                    Collections.emptyList()
            );
        }

        // Converte as Roles do nosso Enum para SimpleGrantedAuthority do Spring
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());

        // Retorna o utilizador configurado
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(), // Importante: Se for false, o login é bloqueado automaticamente
                true, true, true, // Contas não expiradas/bloqueadas
                authorities
        );
    }
}