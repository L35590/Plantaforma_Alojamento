package com.ae.alojamento.service;

import com.ae.alojamento.entity.User;
import com.ae.alojamento.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections; 

@Service
public class UsuarioService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registarUsuario(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        user.setRoles(Collections.singleton(User.Role.USER));
        
        user.setEnabled(true);
        userRepository.save(user);
    }
}