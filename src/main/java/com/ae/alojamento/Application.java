package com.ae.alojamento;

import com.ae.alojamento.entity.User;
import com.ae.alojamento.repository.UserRepository;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

/**
 * Ponto de entrada da aplicação Spring Boot.
 * A implementação de 'AppShellConfigurator' ativa as configurações automáticas do Vaadin (PWA, temas).
 */
@SpringBootApplication
public class Application implements AppShellConfigurator {

    /**
     * Método principal Java.
     * @param args Argumentos de linha de comando (não usados diretamente aqui).
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Define um 'Bean' que executa lógica logo após a aplicação arrancar.
     * Objetivo: Garantir que existe sempre um Administrador no sistema.
     * * @param userRepository Repositório para verificar e salvar utilizadores.
     * @param passwordEncoder Codificador para encriptar a password.
     * @return CommandLineRunner Uma função lambda executada no startup.
     */
    @Bean
    public CommandLineRunner loadData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return (args) -> {
            // Verifica se o admin já existe pelo username 'admin'
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setPrimeiroNome("Administrador");
                admin.setUltimoNome("Sistema");
                admin.setUsername("admin");
                // Importante: Encriptar a password antes de salvar na BD
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setEmail("admin@ae.pt");
                
                // Define o papel (Role) como ADMIN
                admin.setRoles(Collections.singleton(User.Role.ADMIN));
                
                userRepository.save(admin);
                System.out.println(">>> Conta de ADMIN criada (user: admin / pass: admin)");
            }
        };
    }
}