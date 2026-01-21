package com.ae.alojamento.views;

import com.ae.alojamento.entity.Anuncio;
import com.ae.alojamento.service.AnuncioService;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "anuncios", layout = MainLayout.class)
@PageTitle("Todos os Anúncios | AE Alojamento")
@AnonymousAllowed
public class AnunciosView extends VerticalLayout {

    public AnunciosView(AnuncioService service) {
        setSizeFull();
        setPadding(true);

        H2 titulo = new H2("🏠 Todos os Anúncios Disponíveis");
        titulo.getStyle().set("color", "#2C3E42");
        
        FlexLayout gridContainer = new FlexLayout();
        gridContainer.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        gridContainer.setWidthFull();
        gridContainer.getStyle().set("gap", "20px");
        gridContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // Busca apenas os ativos
        var anuncios = service.buscarAtivos();
        
        if (anuncios.isEmpty()) {
            gridContainer.add(new Paragraph("Não existem anúncios ativos de momento."));
        } else {
            for (Anuncio a : anuncios) {
                gridContainer.add(criarCard(a));
            }
        }
        
        add(titulo, gridContainer);
    }

    private VerticalLayout criarCard(Anuncio a) {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("300px");
        card.getStyle().set("border", "1px solid #ddd")
                       .set("border-radius", "8px")
                       .set("padding", "15px")
                       .set("background-color", "white")
                       .set("box-shadow", "0 2px 5px rgba(0,0,0,0.05)");

        // Lógica da imagem
        String imagemPath = (a.getTipoAnuncio() == Anuncio.TipoAnuncio.OFERTA) 
                            ? "images/casa.png" 
                            : "images/lupa.png";

        // Layout Horizontal para o topo (Imagem e Título)
        Div headerCard = new Div();
        headerCard.getStyle().set("display", "flex").set("align-items", "center").set("gap", "15px").set("margin-bottom", "10px");

        Image icon = new Image(imagemPath, "Icone");
        icon.setHeight("50px"); 

        H4 titulo = new H4(a.getTipoAnuncio() + ": " + a.getTipoAlojamento());
        titulo.getStyle().set("margin", "0").set("color", "#8BA6AC");

        headerCard.add(icon, titulo);
        
        // Detalhes
        Paragraph zona = new Paragraph("📍 " + a.getZona());
        Paragraph preco = new Paragraph("💰 " + a.getPreco() + "€");
        preco.getStyle().set("font-weight", "bold").set("color", "#2C3E42");
        
        Paragraph genero = new Paragraph("Para: " + a.getGeneroAlvo());
        Paragraph desc = new Paragraph(a.getDescricao());
        desc.getStyle().set("font-style", "italic").set("font-size", "0.9em").set("color", "#555");
        
        card.add(headerCard, zona, preco, genero, desc);
        return card;
    }
}