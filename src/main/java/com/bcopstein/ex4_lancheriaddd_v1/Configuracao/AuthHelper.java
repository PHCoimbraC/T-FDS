package com.bcopstein.ex4_lancheriaddd_v1.Configuracao;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;


@Component
public class AuthHelper {

    private static final String USER_SESSION_KEY = "usuarioAutenticado";

    public void login(HttpServletRequest request, Usuario usuario) {
        HttpSession session = request.getSession(true);
        session.setAttribute(USER_SESSION_KEY, usuario);
        System.out.println("  Usuário " + usuario.getEmail() + " logado");
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            System.out.println("Sessão invalidada (logout)");
        }
    }

    public boolean isAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Usuario usuario = (Usuario) session.getAttribute(USER_SESSION_KEY);
        return usuario != null && usuario.isAtivo();
    }

    public Usuario getUsuarioAutenticado(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        
        if (session == null) {
            throw new IllegalStateException("Sessão não existe. Usuário não autenticado.");
        }

        Usuario usuario = (Usuario) session.getAttribute(USER_SESSION_KEY);
        
        if (usuario == null) {
            throw new IllegalStateException("Usuário não autenticado");
        }

        if (!usuario.isAtivo()) {
            throw new IllegalStateException("Usuário inativo");
        }

        return usuario;
    }


    public boolean isMaster(HttpServletRequest request) {
        try {
            Usuario usuario = getUsuarioAutenticado(request);
            return usuario.isMaster();
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public boolean isCliente(HttpServletRequest request) {
        try {
            Usuario usuario = getUsuarioAutenticado(request);
            return usuario.isCliente();
        } catch (IllegalStateException e) {
            return false;
        }
    }
}