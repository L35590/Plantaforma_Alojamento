package com.ae.alojamento.entity;

import jakarta.persistence.*;
import java.util.Set;

/**
 * Entidade JPA mapeada para a tabela "app_user".
 * (O nome 'User' é reservado em alguns SQLs, por isso mudamos o nome da tabela).
 */
@Entity
@Table(name = "app_user") 
public class User {

    // Identificador único auto-incrementado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Username único no sistema
    @Column(unique = true)
    private String username;
    
    private String password; // Guardada encriptada (Hash)
    private String email;
    
    private String primeiroNome;
    private String ultimoNome;

    // Controla se a conta está ativa ou bloqueada
    private boolean enabled = false;

    // Armazena as permissões (USER, ADMIN). 
    // FetchType.EAGER garante que as roles são carregadas assim que o user é lido.
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    // Construtor vazio obrigatório para o funcionamento do JPA
    public User() {}

    // --- Getters e Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPrimeiroNome() { return primeiroNome; }
    public void setPrimeiroNome(String primeiroNome) { this.primeiroNome = primeiroNome; }

    public String getUltimoNome() { return ultimoNome; }
    public void setUltimoNome(String ultimoNome) { this.ultimoNome = ultimoNome; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    // Enumeração interna para definir os tipos de permissão
    public enum Role {
        USER, ADMIN
    }
}