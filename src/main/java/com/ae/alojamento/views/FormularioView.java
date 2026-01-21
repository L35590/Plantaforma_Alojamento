package com.ae.alojamento.views;

import com.ae.alojamento.entity.Anuncio;
import com.ae.alojamento.entity.User;
import com.ae.alojamento.repository.UserRepository;
import com.ae.alojamento.service.AnuncioService;
import com.ae.alojamento.service.PagamentoService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;
import java.math.BigDecimal;


@Route(value = "inserir", layout = MainLayout.class)
@PermitAll // Requer login
public class FormularioView extends VerticalLayout {

    private final AnuncioService anuncioService;
    private final PagamentoService pagamentoService;
    private final UserRepository userRepository;
    
    // Objeto que está a ser editado
    private Anuncio anuncio = new Anuncio();
    // Binder liga os campos do formulário ao objeto Java
    private Binder<Anuncio> binder = new Binder<>(Anuncio.class);

    public FormularioView(AnuncioService anuncioService, PagamentoService pagamentoService, UserRepository userRepository) {
        this.anuncioService = anuncioService;
        this.pagamentoService = pagamentoService;
        this.userRepository = userRepository;

        setAlignItems(Alignment.CENTER);
        
        H2 titulo = new H2("Publicar Novo Anúncio");
        
        // Definição dos campos
        ComboBox<Anuncio.TipoAnuncio> tipoAnuncio = new ComboBox<>("O que pretende?", Anuncio.TipoAnuncio.values());
        TextField tipoAlojamento = new TextField("Tipo de Alojamento (ex: Quarto, T1)");
        TextField zona = new TextField("Zona");
        BigDecimalField preco = new BigDecimalField("Preço (€)");
        ComboBox<Anuncio.Genero> genero = new ComboBox<>("Género Alvo", Anuncio.Genero.values());
        TextField contacto = new TextField("Contacto Telefónico");
        TextArea descricao = new TextArea("Descrição Detalhada");
        descricao.setMinHeight("150px");

        // Binding (Ligação)
        binder.bindInstanceFields(this); // Liga automaticamente os campos aos nomes das variáveis
        binder.setBean(anuncio);

        Button btnGuardar = new Button("Gerar Pagamento e Publicar", e -> guardarAnuncio());
        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnGuardar.setWidthFull();

        VerticalLayout formLayout = new VerticalLayout(titulo, tipoAnuncio, tipoAlojamento, zona, preco, genero, contacto, descricao, btnGuardar);
        formLayout.setMaxWidth("600px");
        
        add(formLayout);
    }

    private void guardarAnuncio() {
        // Validação de campos vazios pelo binder (opcional, pode ser melhorado)
        if (anuncio.getPreco() == null) {
            Notification.show("Defina um preço.").addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        // Obtém utilizador logado
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User utilizadorReal = userRepository.findByUsername(username).orElse(null);
        
        if (utilizadorReal != null) {
            anuncio.setAnunciante(utilizadorReal);
        }

        // Obtém Referência Multibanco (Chamada externa)
        Double valorParaServico = anuncio.getPreco().doubleValue();
        var dadosPagamento = pagamentoService.obterReferencia(valorParaServico);

        if ("ERRO".equals(dadosPagamento.mb_entity())) {
             anuncio.setEntidadeMB("00000"); 
             anuncio.setReferenciaMB("000 000 000");
        } else {
            anuncio.setEntidadeMB(dadosPagamento.mb_entity());
            anuncio.setReferenciaMB(dadosPagamento.mb_reference());
        }
        
        // Define como inativo até pagamento
        anuncio.setAtivo(false); 
        anuncioService.guardar(anuncio);

        // Mostra Dialog com dados MB
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Pagamento Multibanco");
        
        VerticalLayout info = new VerticalLayout();
        info.setSpacing(false);
        info.getStyle().set("text-align", "center");
        
        info.add(new Paragraph("Referência gerada com sucesso!"));
        info.add(new H3("Entidade: " + anuncio.getEntidadeMB()));
        info.add(new H3("Referência: " + anuncio.getReferenciaMB()));
        info.add(new H3("Valor: " + anuncio.getPreco() + " €"));
        
        Button fechar = new Button("Concluir", e -> {
            dialog.close();
            getUI().ifPresent(ui -> ui.navigate(AnunciosView.class));
        });
        
        dialog.add(info);
        dialog.getFooter().add(fechar);
        dialog.open();
    }
}