package com.ae.alojamento.repository;

import com.ae.alojamento.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; 

/**
 * Interface que herda do JpaRepository.
 * O Spring cria automaticamente a implementação SQL em tempo de execução.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Encontra um utilizador pelo seu username.
     * @param username O nome de utilizador a procurar.
     * @return Optional vazio se não encontrar, ou com o User se encontrar.
     */
    Optional<User> findByUsername(String username);
}