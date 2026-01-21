package com.ae.alojamento.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Anuncio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tipo de alojamento em texto livre (ex: "T1", "Quarto partilhado").
     * Alterado de Enum para String para permitir maior flexibilidade ao utilizador.
     */
    private String tipoAlojamento; 
    
    // Define se é OFERTA (alugo casa) ou PROCURA (procuro casa)
    @Enumerated(EnumType.STRING)
    private TipoAnuncio tipoAnuncio;

    private String zona;
    private BigDecimal preco; // BigDecimal é usado para evitar erros de arredondamento financeiro

    @Enumerated(EnumType.STRING)
    private Genero generoAlvo; // Misto, Rapazes ou Raparigas

    @Column(length = 1000) // Aumenta o limite de caracteres para descrições longas
    private String descricao;
    
    private String contacto;
    
    // Dados gerados pelo PagamentoService
    private String entidadeMB;
    private String referenciaMB;
    
    // Controla se o anúncio aparece na lista pública
    private boolean ativo;
    private LocalDate dataCriacao;

    // Relação Muitos-para-Um: Um utilizador pode ter vários anúncios
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User anunciante;

    // Enums
    public enum TipoAnuncio { OFERTA, PROCURA }
    public enum Genero { MISTO, RAPAZES, RAPARIGAS }

    // GETTERS E SETTERS 
    
    public String getTipoAlojamento() { 
        return tipoAlojamento; 
    }
    
    public void setTipoAlojamento(String tipoAlojamento) { 
        this.tipoAlojamento = tipoAlojamento; 
    }
    
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }
    
    public TipoAnuncio getTipoAnuncio() { 
        return tipoAnuncio; 
    }
    
    public void setTipoAnuncio(TipoAnuncio tipoAnuncio) { 
        this.tipoAnuncio = tipoAnuncio; 
    }
    
    public String getZona() { 
        return zona; 
    }
    
    public void setZona(String zona) { 
        this.zona = zona; 
    }
    
    public BigDecimal getPreco() { 
        return preco; 
    }
    
    public void setPreco(BigDecimal preco) { 
        this.preco = preco; 
    }
    
    public Genero getGeneroAlvo() { 
        return generoAlvo; 
    }
    
    public void setGeneroAlvo(Genero generoAlvo) { 
        this.generoAlvo = generoAlvo; 
    }
    
    public String getDescricao() { 
        return descricao; 
    }
    
    public void setDescricao(String descricao) { 
        this.descricao = descricao; 
    }
    
    public String getContacto() { 
        return contacto; 
    }
    
    public void setContacto(String contacto) { 
        this.contacto = contacto; 
    }
    
    public String getEntidadeMB() { 
        return entidadeMB; 
    }
    
    public void setEntidadeMB(String entidadeMB) { 
        this.entidadeMB = entidadeMB; 
    }
    
    public String getReferenciaMB() { 
        return referenciaMB; 
    }
    
    public void setReferenciaMB(String referenciaMB) { 
        this.referenciaMB = referenciaMB; 
    }
    
    public boolean isAtivo() { 
        return ativo; 
    }
    
    public void setAtivo(boolean ativo) { 
        this.ativo = ativo; 
    }
    
    public LocalDate getDataCriacao() { 
        return dataCriacao; 
    }

    public void setDataCriacao(LocalDate dataCriacao) { 
        this.dataCriacao = dataCriacao; 
    }
    
    public User getAnunciante() { 
        return anunciante; 
    }
    
    public void setAnunciante(User anunciante) { 
        this.anunciante = anunciante; 
    }
}