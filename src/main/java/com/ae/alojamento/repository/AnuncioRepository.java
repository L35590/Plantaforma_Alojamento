package com.ae.alojamento.repository;

import com.ae.alojamento.entity.Anuncio;
import com.ae.alojamento.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {
    
    // Busca apenas anúncios marcados como ativos
    List<Anuncio> findByAtivoTrue();

    // Busca os 3 anúncios mais recentes (pelo ID mais alto)
    List<Anuncio> findTop3ByOrderByIdDesc();
    
    // Remove todos os anúncios de um certo utilizador
    void deleteByAnunciante(User anunciante);
}