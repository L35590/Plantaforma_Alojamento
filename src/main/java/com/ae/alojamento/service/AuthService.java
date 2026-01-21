package com.ae.alojamento.service;

import com.ae.alojamento.entity.User;
import com.ae.alojamento.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Cria e guarda um novo utilizador na base de dados.
     * * @param pNome Primeiro Nome.
     * @param uNome Último Nome.
     * @param username Nome de utilizador único.
     * @param email Email de contacto.
     * @param rawPassword Password em texto simples (será encriptada aqui).
     * @throws RuntimeException Se o username já existir.
     */

    public void registarUtilizador(String pNome, String uNome, String username, String email, String rawPassword) {
        
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Este nome de utilizador já está em uso.");
        }

        User user = new User();
        user.setPrimeiroNome(pNome);
        user.setUltimoNome(uNome);
        user.setUsername(username);
        user.setEmail(email);

        // Encriptação BCrypt antes de persistir
        user.setPassword(passwordEncoder.encode(rawPassword));
        
        // Por defeito, é USER e começa desativado (enabled=false) até aprovação (opcional) ou lógica de negócio
        user.setRoles(Collections.singleton(User.Role.USER));
        user.setEnabled(false);

        userRepository.save(user);
    }
}