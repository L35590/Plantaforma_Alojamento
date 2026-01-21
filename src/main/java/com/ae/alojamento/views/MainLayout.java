package com.ae.alojamento.views;

import com.ae.alojamento.security.SecurityService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

public class MainLayout extends AppLayout {

    private final SecurityService securityService;

    public MainLayout(@Autowired SecurityService securityService) {
        this.securityService = securityService;

        // Configura largura do menu lateral
        getElement().getStyle().set("--vaadin-app-layout-drawer-width", "300px");

        aplicarCoresPersonalizadas();
        createHeader();
        createDrawer();
    }

    // Injeta CSS global para variáveis de cor
    private void aplicarCoresPersonalizadas() {
        String css = """
            html {
                --cor-primaria: #8BA6AC;    /* Azul Petróleo */
                --cor-topo: #D7D7B8;        /* Cáqui */
                --cor-menu: #E5E6C9;        /* Verde Bege */
                --cor-fundo: #F8F8EC;       /* Creme */
                --cor-borda: #BDCDD0;       /* Cinza Azulado */

                
                --lumo-primary-color: var(--cor-primaria);
                --lumo-primary-text-color: var(--cor-primaria);
                
                --lumo-base-color: var(--cor-fundo);
                
                --lumo-body-text-color: #2C3E42;
                --lumo-header-text-color: #1A282B;

                --lumo-contrast-10pct: var(--cor-topo);  /* Usado no hover */
                --lumo-contrast-20pct: var(--cor-borda); /* Usado nas bordas de cartões */
            }

            vaadin-app-layout::part(navbar) {
                background-color: var(--cor-topo);
                border-bottom: 1px solid var(--cor-borda);
                box-shadow: 0 2px 4px rgba(0,0,0,0.05);
            }

            vaadin-app-layout::part(drawer) {
                background-color: var(--cor-menu);
                border-right: 1px solid var(--cor-borda);
            }
            
            h1 {
                color: #2C3E42;
            }
        """;

        
        getElement().appendChild(new com.vaadin.flow.dom.Element("style").setText(css));
    }

    private void createHeader() {
        H1 logo = new H1("🏠 AE Alojamento");
        logo.getStyle().set("font-size", "var(--lumo-font-size-l)")
                       .set("margin", "0")
                       .set("color", "#2C3E42") 
                       .set("font-weight", "bold");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);
        
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassName("header-padding");
        
        addToNavbar(header);
    }

    private void createDrawer() {
        // Criação dos links de navegação
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true); 

        layout.add(criarLinkMenu("Início", HomeView.class, VaadinIcon.HOME));
        layout.add(criarLinkMenu("Ver Anúncios", AnunciosView.class, VaadinIcon.LIST));

        UserDetails user = securityService.getAuthenticatedUser();

        if (user != null) {
            layout.add(new com.vaadin.flow.component.html.Hr());
            layout.add(criarLinkMenu("Novo Anúncio", FormularioView.class, VaadinIcon.PLUS_CIRCLE));

            boolean isAdmin = user.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                RouterLink adminLink = criarLinkMenu("Administração", AdminView.class, VaadinIcon.DASHBOARD);
                adminLink.getStyle().set("color", "#cf3434"); 
                layout.add(adminLink);
            }

            layout.add(new com.vaadin.flow.component.html.Hr());
            layout.add(new Span("👤 " + user.getUsername()));

            // Botão de Logout
            Button logoutBtn = new Button("Terminar Sessão");
            logoutBtn.setIcon(VaadinIcon.SIGN_OUT.create());
            logoutBtn.setIconAfterText(true);
            logoutBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            logoutBtn.setWidthFull();
            
        
            logoutBtn.getStyle().set("display", "flex");
            logoutBtn.getStyle().set("justify-content", "space-between");
            logoutBtn.getStyle().set("color", "var(--lumo-body-text-color)");
            
            logoutBtn.addClickListener(e -> securityService.logout());
            layout.add(logoutBtn);

        } else {
            layout.add(new com.vaadin.flow.component.html.Hr());
            layout.add(criarLinkMenu("Login", LoginView.class, VaadinIcon.SIGN_IN));
            layout.add(criarLinkMenu("Registar Conta", RegistoView.class, VaadinIcon.USER_CARD));
        }

        addToDrawer(layout);
    }

    // Método auxiliar para estilizar os links do menu
    private RouterLink criarLinkMenu(String texto, Class<? extends Component> view, VaadinIcon icone) {
        RouterLink link = new RouterLink();
        link.setRoute(view);

        Span label = new Span(texto);
        Icon i = icone.create();
        i.setSize("20px");
        
        link.add(label, i);

        // Estilos CSS inline para o link
        link.getStyle().set("display", "flex");
        link.getStyle().set("justify-content", "space-between");
        link.getStyle().set("align-items", "center");
        link.getStyle().set("width", "100%");
        link.getStyle().set("box-sizing", "border-box"); 
        link.getStyle().set("padding", "10px 15px");
        link.getStyle().set("text-decoration", "none");
        link.getStyle().set("border-radius", "8px");
        link.getStyle().set("color", "var(--lumo-body-text-color)");
        link.getStyle().set("transition", "all 0.3s ease");

        // Efeito hover simples em JS
        link.getElement().addEventListener("mouseover", e -> {
            
            link.getStyle().set("background-color", "rgba(255,255,255, 0.5)"); 
            link.getStyle().set("color", "var(--lumo-primary-color)"); 
            link.getStyle().set("padding-left", "25px"); 
            link.getStyle().set("font-weight", "bold");
        });

        link.getElement().addEventListener("mouseout", e -> {
            link.getStyle().set("background-color", "transparent");
            link.getStyle().set("color", "var(--lumo-body-text-color)");
            link.getStyle().set("padding-left", "15px"); 
            link.getStyle().set("font-weight", "normal");
        });

        return link;
    }
}