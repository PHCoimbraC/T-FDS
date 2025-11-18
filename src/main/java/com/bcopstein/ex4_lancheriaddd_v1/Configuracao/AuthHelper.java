package com.bcopstein.ex4_lancheriaddd_v1.Configuracao;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;


@Component
public class AuthHelper {

    public void login(HttpServletRequest request, Usuario usuario) {
        HttpSession session = request.getSession(true);
        session.setAttribute("usuarioAutenticado", usuario);
        System.out.println("  Usuário " + usuario.getEmail() + " logado");
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            System.out.println("Sessão invalidada");
        }
    }

    public boolean isAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Usuario usuario = (Usuario) session.getAttribute("usuarioAutenticado");
        return usuario != null && usuario.isAtivo();
    }

    public Usuario getUsuarioAutenticado(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        
        if (session == null) {
            throw new IllegalStateException("Usuário não autenticado");
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioAutenticado");
        
        if (usuario == null) {
            throw new IllegalStateException("Usuário não autenticado");
        }

        if (!usuario.isAtivo()) {
            throw new IllegalStateException("Usuário inativo");
        }

        return usuario;
    }

}