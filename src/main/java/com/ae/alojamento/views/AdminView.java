package com.ae.alojamento.views;

import com.ae.alojamento.entity.Anuncio;
import com.ae.alojamento.entity.User;
import com.ae.alojamento.repository.UserRepository;
import com.ae.alojamento.service.AnuncioService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;

@Route(value = "admin", layout = MainLayout.class)
@RolesAllowed("ADMIN") // Apenas Administradores podem aceder
public class AdminView extends VerticalLayout {

    private final AnuncioService anuncioService;
    private final UserRepository userRepository;

    private Grid<Anuncio> gridAnuncios;
    private Grid<User> gridUsers;

    public AdminView(AnuncioService anuncioService, UserRepository userRepository) {
        this.anuncioService = anuncioService;
        this.userRepository = userRepository;

        setSizeFull();
        setPadding(true);

        H2 titulo = new H2("Painel de Administração");
        
        // Configuração das Abas (Tabs)
        Tab tabAnuncios = new Tab("Gerir Anúncios");
        Tab tabUsers = new Tab("Gerir Utilizadores");
        Tabs tabs = new Tabs(tabAnuncios, tabUsers);

        // Layouts para cada aba
        VerticalLayout containerAnuncios = criarLayoutAnuncios();
        VerticalLayout containerUsers = criarLayoutUsers();
        containerUsers.setVisible(false); // Começa escondido

        tabs.addSelectedChangeListener(event -> {
            boolean verAnuncios = event.getSelectedTab() == tabAnuncios;
            containerAnuncios.setVisible(verAnuncios);
            containerUsers.setVisible(!verAnuncios);
        });

        add(titulo, tabs, containerAnuncios, containerUsers);
    }

    private VerticalLayout criarLayoutAnuncios() {
        gridAnuncios = new Grid<>(Anuncio.class, false);
        // Definição das colunas
        gridAnuncios.addColumn(Anuncio::getId).setHeader("ID").setWidth("50px").setFlexGrow(0);
        gridAnuncios.addColumn(Anuncio::getTipoAlojamento).setHeader("Tipo");
        gridAnuncios.addColumn(a -> a.getPreco() + "€").setHeader("Preço");
        gridAnuncios.addColumn(anuncio -> anuncio.getAnunciante() != null ? anuncio.getAnunciante().getUsername() : "N/A").setHeader("User");
        
        // Coluna de Estado com badge visual
        gridAnuncios.addComponentColumn(a -> {
            Span badge = new Span(a.isAtivo() ? "Ativo" : "Inativo");
            badge.getElement().getThemeList().add(a.isAtivo() ? "badge success" : "badge error");
            return badge;
        }).setHeader("Estado");

        // Botões de Ação
        gridAnuncios.addComponentColumn(a -> {
            Button toggle = new Button(a.isAtivo() ? "Desativar" : "Ativar");
            toggle.addClickListener(e -> {
                anuncioService.alternarEstado(a);
                gridAnuncios.getDataProvider().refreshItem(a);
            });

            Button eliminar = new Button(VaadinIcon.TRASH.create());
            eliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);
            eliminar.addClickListener(e -> confirmarEliminacaoAnuncio(a));

            return new HorizontalLayout(toggle, eliminar);
        }).setHeader("Ações");

        atualizarGridAnuncios();
        return new VerticalLayout(gridAnuncios);
    }

    private VerticalLayout criarLayoutUsers() {
        gridUsers = new Grid<>(User.class, false);
        gridUsers.addColumn(User::getId).setHeader("ID");
        gridUsers.addColumn(User::getUsername).setHeader("Username");
        gridUsers.addColumn(User::getEmail).setHeader("Email");
        
        // Coluna para Aprovar/Bloquear User
        gridUsers.addComponentColumn(u -> {
            Button btnToggle = new Button(u.isEnabled() ? "Bloquear" : "Aprovar");
            if (!u.isEnabled()) btnToggle.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            
            btnToggle.addClickListener(e -> {
                u.setEnabled(!u.isEnabled());
                userRepository.save(u);
                gridUsers.getDataProvider().refreshItem(u);
                Notification.show("Estado do utilizador alterado.");
            });
            return btnToggle;
        }).setHeader("Estado");

        gridUsers.addComponentColumn(u -> {
            Button btnDel = new Button(VaadinIcon.TRASH.create());
            btnDel.addThemeVariants(ButtonVariant.LUMO_ERROR);
            btnDel.addClickListener(e -> confirmarEliminacaoUser(u));
            return btnDel;
        });

        atualizarGridUsers();
        return new VerticalLayout(gridUsers);
    }

    private void confirmarEliminacaoAnuncio(Anuncio a) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Eliminar Anúncio " + a.getId() + "?");
        
        Button btnCancelar = new Button("Cancelar", e -> dialog.close());
        Button btnConfirmar = new Button("Eliminar", e -> {
            anuncioService.eliminar(a);
            atualizarGridAnuncios();
            dialog.close();
            Notification.show("Anúncio eliminado.");
        });
        btnConfirmar.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

        dialog.getFooter().add(btnCancelar, btnConfirmar);
        dialog.open();
    }

    // Ação perigosa: apagar user e os seus anúncios
    @Transactional 
    private void confirmarEliminacaoUser(User user) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Eliminar Utilizador?");
        dialog.add("CUIDADO: Ao eliminar o utilizador '" + user.getUsername() + 
                   "', todos os anúncios dele também serão apagados!");
        
        Button btnCancelar = new Button("Cancelar", e -> dialog.close());
        Button btnConfirmar = new Button("Eliminar Tudo", e -> {
            try {
                // Remove anúncios primeiro (Constraints FK)
                anuncioService.eliminarPorAnunciante(user);
                // Remove user
                userRepository.delete(user);
                
                atualizarGridUsers();
                dialog.close();
                Notification.show("Utilizador e seus dados eliminados.");
            } catch (Exception ex) {
                Notification.show("Erro ao eliminar: " + ex.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        btnConfirmar.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

        dialog.getFooter().add(btnCancelar, btnConfirmar);
        dialog.open();
    }

    private void atualizarGridAnuncios() {
        gridAnuncios.setItems(anuncioService.buscarTodos());
    }

    private void atualizarGridUsers() {
        gridUsers.setItems(userRepository.findAll());
    }
}