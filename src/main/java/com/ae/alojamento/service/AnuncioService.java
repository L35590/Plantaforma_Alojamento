package com.ae.alojamento.service;

import com.ae.alojamento.entity.Anuncio;
import com.ae.alojamento.entity.User;
import com.ae.alojamento.repository.AnuncioRepository;
import com.ae.alojamento.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnuncioService {

    private final AnuncioRepository repository;
    private final UserRepository userRepository;

    public AnuncioService(AnuncioRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    // --- MÉTODOS DE LEITURA ---

    public List<Anuncio> buscarTodos() {
        return repository.findAll();
    }
    
    /**
     * Busca os 3 anúncios mais recentes para mostrar no carrossel da Home.
     * @return Lista com no máximo 3 anúncios.
     */

    public List<Anuncio> buscarDestaques() {
        return repository.findTop3ByOrderByIdDesc();
    }
    
    public List<Anuncio> buscarAtivos() {
        return repository.findByAtivoTrue();
    }

    // --- MÉTODOS DE ESCRITA ---

    public void guardar(Anuncio anuncio) {
        repository.save(anuncio);
    }

    /**
     * Alterna a visibilidade de um anúncio (Ativo <-> Inativo).
     * @param anuncio O objeto a ser modificado.
     */

    public void alternarEstado(Anuncio anuncio) {
        anuncio.setAtivo(!anuncio.isAtivo());
        repository.save(anuncio);
    }

    // --- MÉTODOS DE ELIMINAÇÃO ---

    public void eliminar(Anuncio anuncio) {
        repository.delete(anuncio);
    }

    /**
     * Elimina todos os anúncios de um utilizador específico.
     * Anotado com @Transactional para garantir que, se falhar a meio, nada é apagado.
     * * @param anunciante O utilizador cujos anúncios serão removidos.
     */
    
    @Transactional
    public void eliminarPorAnunciante(User anunciante) {
        repository.deleteByAnunciante(anunciante);
    }
}