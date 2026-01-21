package com.ae.alojamento.security;

import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

@Service
public class SecurityService {
    private final AuthenticationContext authenticationContext;

    public SecurityService(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    // Retorna os detalhes do utilizador logado ou null
    public UserDetails getAuthenticatedUser() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class).orElse(null);
    }

    // Faz logout da sessão
    public void logout() {
        authenticationContext.logout();
    }
}