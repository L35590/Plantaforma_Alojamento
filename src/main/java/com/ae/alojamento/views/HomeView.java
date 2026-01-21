package com.ae.alojamento.views;
import com.ae.alojamento.entity.Anuncio;
import com.ae.alojamento.service.AnuncioService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Início | AE Alojamento")
@AnonymousAllowed // Permite acesso sem login
public class HomeView extends VerticalLayout {

    public HomeView(AnuncioService service) {
        setAlignItems(Alignment.CENTER);
        
        // Secção Hero (Título Principal)
        H1 tituloPrincipal = new H1("Encontra o teu lugar ideal");
        tituloPrincipal.getStyle().set("color", "#2C3E42");
        tituloPrincipal.getStyle().set("margin-bottom", "0"); 

        Paragraph subtitulo = new Paragraph("A plataforma oficial de alojamento da Associação de Estudantes.");
        subtitulo.getStyle().set("margin-top", "5px");

        VerticalLayout hero = new VerticalLayout(tituloPrincipal, subtitulo);
        hero.setAlignItems(Alignment.CENTER);
        hero.setPadding(true);
        hero.setSpacing(false); 
        hero.getStyle().set("margin-bottom", "20px");

        // Secção de Destaques
        H3 tituloDestaques = new H3("Destaques do Momento");
        tituloDestaques.getStyle().set("color", "#8BA6AC");

        // Contentor Flex para os cards
        FlexLayout gridContainer = new FlexLayout();
        gridContainer.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        gridContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        gridContainer.getStyle().set("gap", "20px");
        gridContainer.setWidthFull();


        // Busca e exibe os 3 últimos anúncios
        var destaques = service.buscarDestaques();
        
        if (destaques.isEmpty()) {
            gridContainer.add(new Paragraph("Sem destaques no momento."));
        } else {
            for (Anuncio anuncio : destaques) {
                gridContainer.add(criarCartaoSimples(anuncio));
            }
        }

        Button verTodosBtn = new Button("Ver Todos os Anúncios");
        verTodosBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        verTodosBtn.getStyle().set("background-color", "#8BA6AC");
        verTodosBtn.getStyle().set("margin-top", "40px"); 
        verTodosBtn.getStyle().set("margin-bottom", "20px");
        
        verTodosBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(AnunciosView.class)));


        add(hero, tituloDestaques, gridContainer, verTodosBtn, criarRodape());
    }

    // Cria um cartão visual para um anúncio
    private Div criarCartaoSimples(Anuncio a) {
        Div card = new Div();
        card.getStyle().set("border", "1px solid var(--cor-borda)")
                       .set("border-radius", "12px")
                       .set("padding", "20px")
                       .set("width", "280px")
                       .set("background", "#FFFFFF") 
                       .set("box-shadow", "0 4px 6px rgba(0,0,0,0.05)")
                       .set("text-align", "center"); 

        // Escolhe ícone com base no tipo
        String imagemPath = (a.getTipoAnuncio() == Anuncio.TipoAnuncio.OFERTA) 
                            ? "images/casa.png" 
                            : "images/lupa.png";
        
        Image icon = new Image(imagemPath, "Icone");
        icon.setHeight("64px"); 
        icon.getStyle().set("margin-bottom", "15px");

        H4 titulo = new H4(a.getTipoAlojamento());
        titulo.getStyle().set("margin", "0").set("color", "#2C3E42");

        Paragraph zona = new Paragraph("📍 " + a.getZona());
        zona.getStyle().set("margin", "5px 0");
        
        Paragraph preco = new Paragraph(a.getPreco() + "€");
        preco.getStyle().set("font-weight", "bold").set("color", "#8BA6AC").set("font-size", "1.2em");

        card.add(icon, titulo, zona, preco);
        return card;
    }


    private Footer criarRodape() {
        Footer footer = new Footer();
        footer.getStyle().set("margin-top", "60px")
                         .set("padding", "20px")
                         .set("width", "100%")
                         .set("background-color", "#f1f1f1") 
                         .set("text-align", "center");

        // Linha 1
        Paragraph p1 = new Paragraph("Universidade de Évora | Tecnologias Web");
        p1.getStyle().set("margin", "0").set("font-weight", "600").set("color", "#555");

        // Linha 2
        Paragraph p2 = new Paragraph("Desenvolvido por Miguel Gézaro | l35590@alunos.uevora.pt");
        p2.getStyle().set("margin", "5px 0 0 0").set("font-size", "0.9em").set("color", "#777");

        footer.add(p1, p2);
        return footer;
    }
}