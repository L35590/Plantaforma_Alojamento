package com.ae.alojamento.views;

import com.ae.alojamento.service.AuthService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("registar")
@PageTitle("Registo | AE Alojamento")
@AnonymousAllowed
public class RegistoView extends VerticalLayout {

    private final AuthService authService;

    public RegistoView(AuthService authService) {
        this.authService = authService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background-color", "#f5f5f5"); 

        VerticalLayout card = new VerticalLayout();
        card.setMaxWidth("500px");
        card.setWidth("100%");
        card.setPadding(true);
        card.setSpacing(true);
        card.getStyle().set("background-color", "white").set("box-shadow", "0 0 10px rgba(0,0,0,0.1)");

        H2 titulo = new H2("Crie a sua conta");
        
        TextField pNome = new TextField("Primeiro Nome");
        TextField uNome = new TextField("Último Nome");
        TextField username = new TextField("Username");
        EmailField email = new EmailField("Email");
        PasswordField password = new PasswordField("Password");
        PasswordField confirmPassword = new PasswordField("Confirmar Password");
        
        // Tornar campos full width
        pNome.setWidthFull(); uNome.setWidthFull(); username.setWidthFull();
        email.setWidthFull(); password.setWidthFull(); confirmPassword.setWidthFull();

        Button btnRegistar = new Button("Registar", event -> {
            // Validações básicas
            if (pNome.isEmpty() || uNome.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Notification.show("Preencha todos os campos obrigatórios.")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            if (!password.getValue().equals(confirmPassword.getValue())) {
                Notification.show("As passwords não coincidem.")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            try {
                // Chama o serviço para criar o utilizador
                authService.registarUtilizador(
                    pNome.getValue(),
                    uNome.getValue(),
                    username.getValue(),
                    email.getValue(),
                    password.getValue()
                );

                Notification.show("Registo efetuado! Aguarde a aprovação do Administrador para entrar.", 
                        5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                
                // Redireciona para o login
                getUI().ifPresent(ui -> ui.navigate("login"));
                
            } catch (Exception ex) {
                Notification.show("Erro: " + ex.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        Button btnLogin = new Button("Já tenho conta", event -> 
            getUI().ifPresent(ui -> ui.navigate("login"))
        );
        btnLogin.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnLogin.setWidthFull();

        card.add(titulo, pNome, uNome, username, email, password, confirmPassword, btnRegistar, btnLogin);
        add(card);
    }
}