package com.ae.alojamento.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Login | AE Alojamento")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        
        getStyle().set("background-color", "#F8F8EC");

        Button btnVoltar = new Button("Voltar ao Início", VaadinIcon.ARROW_LEFT.create());
        btnVoltar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnVoltar.getStyle().set("color", "#2C3E42"); 
        btnVoltar.getStyle().set("margin-bottom", "10px");
        
        btnVoltar.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(HomeView.class)));

        H1 title = new H1("🏠 AE Alojamento");
        title.getStyle().set("color", "#8BA6AC"); 

        // Define a ação de POST para o Spring Security processar
        login.setAction("login"); 
        
        // Personalização dos textos (I18n)
        LoginI18n i18n = LoginI18n.createDefault();
        LoginI18n.Form form = i18n.getForm();
        form.setTitle("Entrar");
        i18n.setForm(form);
        
        LoginI18n.ErrorMessage errorMessage = i18n.getErrorMessage();
        errorMessage.setTitle("Erro de Autenticação");
        errorMessage.setMessage("Utilizador ou senha incorretos.");
        i18n.setErrorMessage(errorMessage);
        
        login.setI18n(i18n);
        login.getElement().getThemeList().add("light"); 

        RouterLink registerLink = new RouterLink("Ainda não tem conta? Registe-se aqui.", RegistoView.class);
        registerLink.getStyle().set("margin-top", "20px");
        registerLink.getStyle().set("font-weight", "bold");
        registerLink.getStyle().set("color", "#8BA6AC");

        add(btnVoltar, title, login, registerLink);
    }

    // Mostra erro visual se o Spring Security redirecionar com ?error
    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if(beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            login.setError(true);
        }
    }
}